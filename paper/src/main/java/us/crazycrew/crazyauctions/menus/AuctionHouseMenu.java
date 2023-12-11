package us.crazycrew.crazyauctions.menus;

import org.bukkit.entity.Player;
import us.crazycrew.crazyauctions.api.builders.InventoryBuilder;

public class AuctionHouseMenu extends InventoryBuilder {

    public AuctionHouseMenu(Player player, int size, String title) {
        super(player, size, title);
    }

    @Override
    public InventoryBuilder build() {
        return this;
    }
}