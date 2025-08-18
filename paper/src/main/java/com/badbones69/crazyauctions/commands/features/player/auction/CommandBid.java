package com.badbones69.crazyauctions.commands.features.player.auction;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.commands.BaseCommand;
import com.badbones69.crazyauctions.currency.VaultSupport;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import dev.triumphteam.cmd.core.annotations.Syntax;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.permissions.PermissionDefault;
import java.util.HashMap;
import java.util.UUID;

public class CommandBid extends BaseCommand {

    @Command(value = "bid", alias = "bid")
    @Permission(value = "crazyauctions.bid", def = PermissionDefault.TRUE)
    @Syntax("/crazyauctions bid <amount> <price>")
    public void bid(final Player player, @Suggestion("amount") final int amount, @Suggestion("price") final int price) {
        if (!this.crazyManager.isBiddingEnabled()) {
            player.sendMessage(Messages.BIDDING_DISABLED.getMessage(player));

            return;
        }

        final ItemStack itemStack = Methods.getItemInHand(player);
        final int count = itemStack.getAmount();

        if (itemStack.isEmpty()) {
            player.sendMessage(Messages.DOESNT_HAVE_ITEM_IN_HAND.getMessage(player));

            return;
        }

        final FileConfiguration configuration = Files.config.getConfiguration();

        if (player.hasPermission("crazyauctions.bypass") || player.isOp()) {
            final FileConfiguration data = Files.data.getConfiguration();
            final ConfigurationSection section = data.getConfigurationSection("Items");
            final ConfigurationSection items = section == null ? data.createSection("Items") : section;

            final int size = items.getKeys(false).size();

            final ConfigurationSection user = items.createSection(String.valueOf(size)); //todo() this isn't correct, it has to increment.

            user.set("Price", price);
            user.set("Seller", player.getUniqueId().toString());
            user.set("SellerName", player.getName());

            user.set("Time-Till-Expire", Methods.convertToMill(configuration.getString("Settings.Bid-Time", "2m 30s")));

            user.set("Full-Time", Methods.convertToMill(configuration.getString("Settings.Full-Expire-Time", "10d")));

            user.set("StoreID", UUID.randomUUID());

            user.set("Biddable", true);

            user.set("TopBidderName", "None");
            user.set("TopBidder", "None");

            final PlayerInventory inventory = player.getInventory();

            if (count <= 1) {
                inventory.setItemInMainHand(null);
            } else {
                itemStack.setAmount(count - amount);
            }

            user.set("Item", Methods.toBase64(itemStack));

            player.sendMessage(Messages.ADDED_ITEM_TO_AUCTION.getMessage(player, new HashMap<>() {{
                put("%Price%", String.valueOf(price));
                put("%price%", String.valueOf(price));
            }}));

            return;
        }

        final int maximumBidPrice = configuration.getInt("Settings.Max-Beginning-Bid-Price", 1000000);

        if (price > maximumBidPrice) {
            player.sendMessage(Messages.BID_PRICE_TO_HIGH.getMessage(player));

            return;
        }

        final int minimumBidPrice = configuration.getInt("Settings.Minimum-Bid-Price", 10);

        if (price < minimumBidPrice) {
            player.sendMessage(Messages.BID_PRICE_TO_LOW.getMessage(player));

            return;
        }

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        if (configuration.getStringList("Settings.BlackList").contains(itemStack.getType().getKey().getKey()) ||
                container.getKeys().stream().anyMatch(key -> configuration.getStringList("Settings.PDC-BlackList").contains(key.asString()))) {
            player.sendMessage(Messages.ITEM_BLACKLISTED.getMessage(player));

            return;
        }

        if (itemStack instanceof Damageable damageable && !configuration.getBoolean("Settings.Allow-Damaged-Items", false)) {
            if (damageable.getDamage() > 0) {
                player.sendMessage(Messages.ITEM_DAMAGED.getMessage(player));

                return;
            }
        }

        // we put the permission check here as it is the heaviest operation.
        int limit = 0;

        for (final PermissionAttachmentInfo permissions : player.getEffectivePermissions()) {
            final String node = permissions.getPermission();

            if (!node.startsWith("crazyauctions.bid.")) continue;

            final String yoink = node.replace("crazyauctions.bid.", "");

            if (!Methods.isInt(yoink)) {
                //todo() add debugging

                continue;
            }

            final int parsedYoink = Integer.parseInt(yoink);

            if (parsedYoink > limit) {
                limit = parsedYoink;
            }
        }

        if (this.crazyManager.getItems(player, ShopType.BID).size() >= limit) {
            player.sendMessage(Messages.MAX_ITEMS.getMessage(player));

            return;
        }

        final VaultSupport support = this.plugin.getSupport();
        final int auctionFee = configuration.getInt("Settings.Auction-List-Fee", 0);

        if (auctionFee > 0) {
            final long balance = support.getMoney(player);

            if (balance < auctionFee) {
                player.sendMessage(Messages.NEED_MORE_MONEY.getMessage(player, new HashMap<>() {{
                    put("%Money_Needed%", String.valueOf(auctionFee));
                    put("%money_needed%", String.valueOf(auctionFee));
                }}));

                return;
            } else {
                support.removeMoney(player, auctionFee);
            }
        }

        final FileConfiguration data = Files.data.getConfiguration();
        final ConfigurationSection section = data.getConfigurationSection("Items");
        final ConfigurationSection items = section == null ? data.createSection("Items") : section;

        final int size = items.getKeys(false).size();

        final ConfigurationSection user = items.createSection(String.valueOf(size));

        user.set("Price", price);
        user.set("Seller", player.getUniqueId().toString());
        user.set("SellerName", player.getName());

        user.set("Time-Till-Expire", Methods.convertToMill(configuration.getString("Settings.Bid-Time", "2m 30s")));

        user.set("Full-Time", Methods.convertToMill(configuration.getString("Settings.Full-Expire-Time", "10d")));

        user.set("StoreID", UUID.randomUUID());

        user.set("Biddable", true);

        user.set("TopBidderName", "None");
        user.set("TopBidder", "None");

        final PlayerInventory inventory = player.getInventory();

        if (count <= 1) {
            inventory.setItemInMainHand(null);
        } else {
            itemStack.setAmount(count - amount);
        }

        user.set("Item", Methods.toBase64(itemStack));

        player.sendMessage(Messages.ADDED_ITEM_TO_AUCTION.getMessage(player, new HashMap<>() {{
            put("%Price%", String.valueOf(price));
            put("%price%", String.valueOf(price));
        }}));
    }
}