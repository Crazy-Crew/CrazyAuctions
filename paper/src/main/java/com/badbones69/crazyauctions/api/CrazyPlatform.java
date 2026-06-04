package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.api.objects.items.PaperAuctionItem;
import com.badbones69.crazyauctions.api.registry.PaperUserRegistry;
import com.badbones69.crazyauctions.api.registry.adapters.PaperSenderAdapter;
import com.badbones69.crazyauctions.commands.AuctionCommand;
import com.badbones69.crazyauctions.commands.admin.ReloadCommand;
import com.badbones69.crazyauctions.commands.admin.auction.ForceCancelCommand;
import com.badbones69.crazyauctions.commands.admin.migrate.MigrateCommand;
import com.badbones69.crazyauctions.commands.player.CollectCommand;
import com.badbones69.crazyauctions.commands.player.ExpiredCommand;
import com.badbones69.crazyauctions.commands.player.HelpCommand;
import com.badbones69.crazyauctions.commands.player.ListCommand;
import com.badbones69.crazyauctions.commands.player.ViewCommand;
import com.badbones69.crazyauctions.commands.player.auction.BidCommand;
import com.badbones69.crazyauctions.commands.player.auction.SellCommand;
import com.badbones69.crazyauctions.common.CrazyPlugin;
import com.badbones69.crazyauctions.common.enums.FileKey;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ryderbelserion.fusion.paper.FusionPaper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrazyPlatform extends CrazyPlugin<CommandSender, ItemStack> {

    private boolean isSellingModuleEnabled;
    private boolean isBiddingModuleEnabled;

    private final CrazyAuctions plugin;

    public CrazyPlatform(@NonNull final CrazyAuctions plugin, @NonNull final FusionPaper fusion) {
        super(fusion);

        this.plugin = plugin;
    }

    private PaperUserRegistry userRegistry;
    private PaperSenderAdapter userAdapter;

    @Override
    public void init() {
        super.init();

        this.userRegistry = new PaperUserRegistry();
        this.userRegistry.init();

        this.userAdapter = new PaperSenderAdapter(this);

        final YamlConfiguration configuration = FileKey.config.getConfiguration();

        this.isSellingModuleEnabled = configuration.getBoolean("Settings.Feature-Toggle.Selling", true);

        this.isBiddingModuleEnabled = configuration.getBoolean("Settings.Feature-Toggle.Bidding", true);

        Arrays.stream(Permissions.values()).forEach(Permissions::registerPermission);

        final LifecycleEventManager<Plugin> eventManager = this.plugin.getLifecycleManager();

        // Register commands.
        eventManager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final AuctionCommand root = new AuctionCommand();

            root.registerPermissions();

            LiteralArgumentBuilder<CommandSourceStack> literal = root.literal().createBuilder();

            List.of(
                    new ForceCancelCommand(),
                    new ReloadCommand(),

                    new MigrateCommand(),

                    new CollectCommand(),
                    new ExpiredCommand(),

                    new BidCommand(),
                    new SellCommand(),

                    new ViewCommand(),
                    new HelpCommand(),
                    new ListCommand()
            ).forEach(command -> literal.then(command.registerPermissions().literal()));

            event.registrar().register(literal.build(), "The base command for CrazyAuctions!", root.getAliases());
        });
    }

    @Override
    public void reload() {
        super.reload();

        final YamlConfiguration configuration = FileKey.config.getConfiguration();

        this.isSellingModuleEnabled = configuration.getBoolean("Settings.Feature-Toggle.Selling", true);

        this.isBiddingModuleEnabled = configuration.getBoolean("Settings.Feature-Toggle.Bidding", true);
    }

    @Override
    public void stop() {
        FileKey.data.save();
    }

    public @NonNull final List<ItemStack> getItems(@NonNull final Player player) {
        final YamlConfiguration data = FileKey.data.getConfiguration();

        final List<ItemStack> items = new ArrayList<>();

        final ConfigurationSection section = data.getConfigurationSection("Items");

        if (section == null) return items;

        final String uuid = player.getUniqueId().toString();

        for (final String id : section.getKeys(false)) {
            final ConfigurationSection item = section.getConfigurationSection(id);

            if (item == null) continue;

            final String seller = item.getString("Seller", "");

            if (seller.isBlank()) continue;

            if (!seller.equals(uuid)) continue;

            final String base64 = item.getString("Item", "");

            if (base64.isBlank()) continue;

            items.add(Methods.fromBase64(base64));
        }

        return items;
    }

    @Override
    public @NotNull final PaperSenderAdapter getSenderAdapter() {
        return this.userAdapter;
    }

    @Override
    public @NonNull PaperAuctionItem getItem(@NonNull final ItemStack itemStack) {
        return new PaperAuctionItem(itemStack);
    }

    @Override
    public @NotNull final PaperUserRegistry getUserRegistry() {
        return this.userRegistry;
    }

    @Override
    public boolean isSellModuleEnabled() {
        return isSellingModuleEnabled;
    }

    @Override
    public boolean isBidModuleEnabled() {
        return isBiddingModuleEnabled;
    }
}