package com.badbones69.crazyauctions.commands.player.auction;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.api.events.AuctionListEvent;
import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.common.enums.FileKey;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.kyori.permissions.PermissionContext;
import com.ryderbelserion.fusion.paper.builders.commands.context.PaperCommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.api.constants.Messages;
import us.crazycrew.api.enums.ShopType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class BidCommand extends BaseCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final CommandSender sender = context.getSender();

        if (!context.isPlayer()) {
            this.adapter.sendMessage(sender, Messages.players_only);

            return;
        }

        final Player player = context.getPlayer();

        final ItemStack item = Methods.getItemInHand(player);

        if (item.isEmpty()) {
            this.adapter.sendMessage(player, Messages.doesnt_have_item_in_hand);

            return;
        }

        final YamlConfiguration configuration = FileKey.config.getConfiguration();

        final double minimumPrice = configuration.getDouble("Settings.Minimum-Bid-Price", 100.0);

        final double price = context.getDoubleArgument("price").orElse(minimumPrice);

        if (price < minimumPrice) {
            this.adapter.sendMessage(player, Messages.bid_price_too_low);

            return;
        }

        final double beginningPrice = configuration.getDouble("Settings.Max-Beginning-Bid-Price", 1000000.0);

        if (price > beginningPrice) {
            this.adapter.sendMessage(player, Messages.bid_price_too_high);

            return;
        }

        final AtomicInteger integer = new AtomicInteger(0);

        if (!Permissions.bypass.hasPermission(player)) {
            for (final PermissionAttachmentInfo info : player.getEffectivePermissions()) {
                final String permission = info.getPermission();

                if (!permission.startsWith("crazyauctions.bid.")) continue;

                final String result = permission.replace("crazyauctions.bid.", "");

                StringUtils.tryParseInt(result).ifPresent(number -> {
                    final int value = number.intValue();

                    if (value > integer.get()) {
                        integer.set(value);
                    }
                });
            }
        }

        final List<ItemStack> items = this.platform.getItems(player);

        final int limit = integer.get();

        if (limit > 0 && items.size() >= limit) {
            this.adapter.sendMessage(player, Messages.max_items);

            return;
        }

        if (configuration.getStringList("Settings.BlackList").contains(item.getType().getKey().getKey())) {
            this.adapter.sendMessage(player, Messages.item_blacklisted);

            return;
        }

        final List<String> keys = configuration.getStringList("Settings.PDC-BlackList");

        final PersistentDataContainerView container = item.getPersistentDataContainer();

        if (container.getKeys().stream().anyMatch(key -> keys.contains(key.asString()))) {
            this.adapter.sendMessage(player, Messages.item_blacklisted);

            return;
        }

        if (!configuration.getBoolean("Settings.Allow-Damaged-Items", false) && item instanceof Damageable damageable) {
            final int durability = damageable.getDamage();

            if (durability > 0) {
                this.adapter.sendMessage(player, Messages.item_damaged);

                return;
            }
        }

        final int size = item.getMaxStackSize();
        final int amount = context.hasArgument("amount") ? Math.min(context.getIntegerArgument("amount").orElse(size), size) : size;

        final int fee = configuration.getInt("Settings.Auction-List-Fee", 0);

        if (fee > 0) {
            if (this.support.getMoney(player) < fee) {
                Map<String, String> placeholders = new HashMap<>();

                placeholders.put("%Money_Needed%", String.valueOf(fee));
                placeholders.put("%money_needed%", String.valueOf(fee));

                this.adapter.sendMessage(player, Messages.need_more_money, placeholders);

                return;
            }

            this.support.removeMoney(player, fee);
        }

        final ItemStack result = item.clone();

        result.setAmount(amount);

        this.holder.addItem(
                player.getUniqueId(),
                player.getName(),
                Methods.toBase64(result),

                price,
                amount,

                ShopType.BID
        );

        new AuctionListEvent(player, ShopType.BID, result, price).callEvent();

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Price%", String.valueOf(price));
        placeholders.put("%price%", String.valueOf(price));

        this.adapter.sendMessage(player, Messages.added_item_to_auction, placeholders);

        if (amount >= 1) {
            item.setAmount(item.getAmount() - amount);

            return;
        }

        Methods.setItemInHand(player, ItemStack.empty());
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("bid").requires(this::requirement);

        final RequiredArgumentBuilder<CommandSourceStack, Double> arg1 = argument("price", DoubleArgumentType.doubleArg()).suggests((_, builder) -> suggestDoubleArgument(builder, "", 1, 100)).executes(context -> {
            run(new PaperCommandContext(context));

            return Command.SINGLE_SUCCESS;
        });

        final RequiredArgumentBuilder<CommandSourceStack, Integer> arg2 = argument("amount", IntegerArgumentType.integer(1, 64)).executes(context -> {
            run(new PaperCommandContext(context));

            return Command.SINGLE_SUCCESS;
        });

        return root.then(arg1.then(arg2)).build();
    }

    @Override
    public @NotNull final List<PermissionContext> getPermissions() {
        return List.of(Permissions.bid.getContext());
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }
}