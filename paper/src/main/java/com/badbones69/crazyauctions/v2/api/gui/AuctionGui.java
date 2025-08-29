package com.badbones69.crazyauctions.v2.api.gui;

import com.badbones69.crazyauctions.v2.CrazyAuctionsPlus;
import com.badbones69.crazyauctions.v2.api.CrazyManager;
import com.badbones69.crazyauctions.v2.api.gui.frame.enums.ActionComponent;
import com.badbones69.crazyauctions.v2.api.gui.frame.items.GuiFiller;
import com.badbones69.crazyauctions.v2.api.gui.frame.items.GuiItem;
import com.badbones69.crazyauctions.v2.api.gui.frame.types.SimpleGui;
import com.badbones69.crazyauctions.v2.api.items.AuctionItem;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.ArrayList;
import java.util.List;

public class AuctionGui {

    private final CrazyAuctionsPlus plugin = JavaPlugin.getPlugin(CrazyAuctionsPlus.class);

    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    private final List<AuctionItem> items = new ArrayList<>();
    private final SimpleGui gui;

    public AuctionGui(@NotNull final YamlCustomFile auction) {
        final CommentedConfigurationNode config = auction.getConfiguration(); // auction.yml gui

        this.gui = new SimpleGui(config.node("settings", "gui-name").getString("<red>Crazy<dark_blue>Auctions"), config.node("settings", "gui-rows").getInt(4));
    }

    public void open(@NotNull final Player player) {
        final GuiFiller guiFiller = this.gui.getGuiFiller();

        guiFiller.fill(ItemType.RED_STAINED_GLASS_PANE.createItemStack(), ItemType.GREEN_STAINED_GLASS_PANE.createItemStack());

        this.gui.addItems(this.crazyManager.getItems().values().stream().map(auctionItem -> auctionItem.asGuiItem(player)).toList().toArray(new GuiItem[0]));

        this.gui.addSlotAction(0, action -> action.setCurrentItem(ItemType.CHEST.createItemStack()));

        this.gui.setDefaultAction(action -> {
            final ItemStack currentItem = action.getCurrentItem();

            if (currentItem == null || currentItem.isEmpty()) {
                action.setCurrentItem(ItemType.EMERALD.createItemStack());

                return;
            }

            final Material type = currentItem.getType();

            switch (type) {
                case EMERALD -> action.setCurrentItem(ItemType.REDSTONE.createItemStack());
                case REDSTONE -> action.setCurrentItem(ItemType.EMERALD.createItemStack());
            }
        });

        this.gui.setOpenAction(action -> action.titleOverride(Component.text("This is an override!")));

        this.gui.setCloseAction(action -> action.getPlayer().sendMessage(Component.text("The inventory is closed!")));

        this.gui.addComponent(ActionComponent.DISABLE_ALL_INTERACTIONS);

        this.gui.build(player);
    }

    public @NotNull final List<AuctionItem> getItems() {
        return this.items;
    }
}