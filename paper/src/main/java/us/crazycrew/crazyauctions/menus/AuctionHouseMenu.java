package us.crazycrew.crazyauctions.menus;

import com.ryderbelserion.cluster.api.builders.InventoryBuilder;
import org.bukkit.entity.Player;

public class AuctionHouseMenu extends InventoryBuilder {

    public AuctionHouseMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        return this;
    }
}