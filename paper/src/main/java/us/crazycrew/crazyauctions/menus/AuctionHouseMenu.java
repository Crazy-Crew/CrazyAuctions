package us.crazycrew.crazyauctions.menus;

import com.ryderbelserion.cluster.api.builders.InventoryBuilder;
import com.ryderbelserion.cluster.items.ItemBuilder;
import com.ryderbelserion.cluster.items.ParentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AuctionHouseMenu extends InventoryBuilder {

    public AuctionHouseMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        // Get inventory.
        Inventory inventory = getInventory();

        // Create item builder.
        ItemBuilder builder = ParentBuilder.of();

        return this;
    }
}