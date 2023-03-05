package us.crazycrew.crazyauctions.api.manager.objects;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Created: 2/19/2023
 * Time: Unknown
 * Last Edited: 2/28/2023 @ 3:04 AM
 *
 * Description: Creates the auction house inventory settings
 */
public class InventorySettings {

    private final String title;
    private final AuctionButtons auctionButtons;

    public InventorySettings(FileConfiguration file) {
        String path = "auction-house.settings.";
        this.title = file.getString(path + "inventory-title");
        this.auctionButtons = new AuctionButtons(file);
    }

    public String getTitle() {
        return title;
    }

    public AuctionButtons getAuctionButtons() {
        return auctionButtons;
    }
}