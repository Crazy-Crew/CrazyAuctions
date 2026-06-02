package com.badbones69.crazyauctions.commands.player.auction;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.other.Permissions;
import com.badbones69.crazyauctions.api.events.AuctionListEvent;
import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyenvoys.enums.Files;
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
import us.crazycrew.api.enums.ShopType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import static io.papermc.paper.command.brigadier.Commands.argument;

public class SellCommand extends BaseCommand {

    @Override
    public void run(@NotNull final PaperCommandContext context) {
        final CommandSender sender = context.getSender();

        if (!context.isPlayer()) {
            sender.sendMessage(Messages.PLAYERS_ONLY.getMessage(sender));

            return;
        }

        final YamlConfiguration configuration = Files.config.getConfiguration();

        final double minimumPrice = configuration.getDouble("Settings.Minimum-Sell-Price", 10.0);

        final double arg1 = context.getDoubleArgument("price").orElse(minimumPrice);

        final Player player = context.getPlayer();

        if (arg1 < minimumPrice) {
            player.sendMessage(Messages.SELL_PRICE_TO_LOW.getMessage(sender));

            return;
        }

        final double beginningPrice = configuration.getDouble("Settings.Max-Beginning-Sell-Price", 1000000.0);

        if (arg1 > beginningPrice) {
            player.sendMessage(Messages.SELL_PRICE_TO_HIGH.getMessage(sender));

            return;
        }

        final AtomicInteger integer = new AtomicInteger(0);

        if (!Permissions.bypass.hasPermission(player)) {
            for (final PermissionAttachmentInfo info : player.getEffectivePermissions()) {
                final String permission = info.getPermission();

                if (!permission.startsWith("crazyauctions.sell.")) continue;

                final String result = permission.replace("crazyauctions.sell.", "");

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
            player.sendMessage(Messages.MAX_ITEMS.getMessage(sender));

            return;
        }

        final ItemStack item = Methods.getItemInHand(player);

        if (configuration.getStringList("Settings.BlackList").contains(item.getType().getKey().getKey())) {
            player.sendMessage(Messages.ITEM_BLACKLISTED.getMessage(sender));

            return;
        }

        final List<String> keys = configuration.getStringList("Settings.PDC-BlackList");

        final PersistentDataContainerView container = item.getPersistentDataContainer();

        if (container.getKeys().stream().anyMatch(key -> keys.contains(key.asString()))) {
            player.sendMessage(Messages.ITEM_BLACKLISTED.getMessage(sender));

            return;
        }

        if (!configuration.getBoolean("Settings.Allow-Damaged-Items", false) && item instanceof Damageable damageable) {
            final int durability = damageable.getDamage();

            if (durability > 0) {
                player.sendMessage(Messages.ITEM_DAMAGED.getMessage(sender));

                return;
            }
        }

        final int itemAmount = item.getAmount();

        final int amount = context.getIntegerArgument("amount").orElse(itemAmount);

        final int fee = configuration.getInt("Settings.Auction-List-Fee", 0);

        if (fee > 0) {
            if (this.support.getMoney(player) < fee) {
                Map<String, String> placeholders = new HashMap<>();

                placeholders.put("%Money_Needed%", String.valueOf(fee));
                placeholders.put("%money_needed%", String.valueOf(fee));

                player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(sender, placeholders));

                return;
            }

            this.support.removeMoney(player, fee);
        }

        final ItemStack result = item.clone();

        result.setAmount(Math.min(amount, itemAmount));

        this.holder.addItem(
                player.getUniqueId(),
                player.getName(),
                Methods.toBase64(result),
                arg1,
                ShopType.SELL
        );

        new AuctionListEvent(player, ShopType.SELL, result, arg1).callEvent();

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("%Price%", String.valueOf(arg1));
        placeholders.put("%price%", String.valueOf(arg1));

        player.sendMessage(Messages.ADDED_ITEM_TO_AUCTION.getMessage(sender, placeholders));

        final int resultAmount = item.getAmount();

        if (resultAmount <= 1 || (resultAmount - amount) <= 0) {
            Methods.setItemInHand(player, ItemStack.empty());
        } else {
            item.setAmount(resultAmount - amount);
        }
    }

    @Override
    public @NotNull final LiteralCommandNode<CommandSourceStack> literal() {
        final LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("sell").requires(this::requirement);

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
        return List.of(Permissions.sell.getContext());
    }

    @Override
    public final boolean requirement(@NotNull final CommandSourceStack context) {
        return context.getSender().hasPermission(getPermissions().getFirst().getPermission());
    }
}