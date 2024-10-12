package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.guis.types.AuctionsMenu;
import com.badbones69.crazyauctions.api.guis.types.CategoriesMenu;
import com.badbones69.crazyauctions.api.guis.types.CurrentMenu;
import com.badbones69.crazyauctions.api.guis.types.ExpiredMenu;
import com.badbones69.crazyauctions.api.guis.types.other.BidMenu;
import com.badbones69.crazyauctions.api.guis.types.other.BuyingMenu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GuiManager {

    public static void openShop(Player player, ShopType shopType, Category category, int page) {
        FileConfiguration config = Files.config.getConfiguration();

        new AuctionsMenu(player, shopType, category, config.getString("Settings.GUIName", "&4Crazy &bAuctions&8 #{page}").replaceAll("\\{page}", String.valueOf(page)), 54, page).build();
    }

    public static void openCategories(Player player, ShopType shopType) {
        FileConfiguration config = Files.config.getConfiguration();

        new CategoriesMenu(player, shopType, config.getString("Settings.Categories", "N/A"), 54).build();
    }

    public static void openPlayersCurrentList(Player player, int page) {
        FileConfiguration config = Files.config.getConfiguration();

        new CurrentMenu(player, config.getString("Settings.Players-Current-Items", "N/A"), 54, page).build();
    }

    public static void openPlayersExpiredList(Player player, int page) {
        FileConfiguration config = Files.config.getConfiguration();

        new ExpiredMenu(player, config.getString("Settings.Cancelled/Expired-Items", "&8Cancelled/Expired Listings #{page}").replaceAll("\\{page}", String.valueOf(page)), 54, page).build();
    }

    public static void openBuying(Player player, String ID) {
        final FileConfiguration config = Files.config.getConfiguration();

        new BuyingMenu(player, ID, config.getString("Settings.Buying-Item", "N/A")).build();
    }

    public static void openBidding(Player player, String ID) {
        FileConfiguration config = Files.config.getConfiguration();

        new BidMenu(player, ID, config.getString("Settings.Bidding-On-Item")).build();
    }

    public static void openViewer(Player player, String other, int page) {
        FileConfiguration config = Files.config.getConfiguration();

        new AuctionsMenu(player, other, config.getString("Settings.GUIName", "&4Crazy &bAuctions&8 #{page}").replaceAll("\\{page}", String.valueOf(page)), 54, page).build();
    }
}