package us.crazycrew.crazyauctions.api.manager.objects;

import org.bukkit.Material;
import us.crazycrew.crazycore.paper.items.ItemBuilder;
import java.util.List;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Created: 2/19/2023
 * Time: Unknown
 * Last Edited: 2/28/2023 @ 3:04 AM
 *
 * Description: Creates the auction categories
 */
public class AuctionCategory {

    private String name;
    private int slot;
    private ItemBuilder displayItem;
    private List<Material> categoryItemList;

    //TODO make it so each AH can have their own categories and the default ones should be able to be disabled.
    public AuctionCategory(String name, int slot, ItemBuilder displayItem, List<Material> categoryItemList) {
        this.name = name;
        this.slot = slot;
        this.displayItem = displayItem;
        this.categoryItemList = categoryItemList;
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }

    public ItemBuilder getDisplayItem() {
        return displayItem;
    }

    public List<Material> getCategoryItemList() {
        return categoryItemList;
    }
}