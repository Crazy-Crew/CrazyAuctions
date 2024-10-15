package com.badbones69.crazyauctions.api.builders.gui;

import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.badbones69.crazyauctions.configs.impl.gui.AuctionKeys;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import java.util.function.Consumer;

public abstract class DynamicInventoryBuilder extends InventoryBuilder {

    private final PaginatedGui gui;

    public DynamicInventoryBuilder(final Player player, final String title, final int rows) {
        super(player);

        this.gui = Gui.paginated().setTitle(title).setRows(rows).disableInteractions().create();
    }

    /**
     * Opens the {@link PaginatedGui}.
     */
    public void open() {
        open(null);
    }

    /**
     * Opens the {@link PaginatedGui}.
     *
     * @param consumer {@link Consumer(DynamicInventoryBuilder)}
     */
    public void open(@Nullable final Consumer<DynamicInventoryBuilder> consumer) {
        if (consumer != null) {
            consumer.accept(this);
        }
    }

    /**
     * Gets the {@link Player}.
     *
     * @return {@link Player}
     */
    public final Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the {@link PaginatedGui}.
     *
     * @return {@link PaginatedGui}
     */
    public final PaginatedGui getGui() {
        return this.gui;
    }

    // Adds the back button
    public void setBackButton(final int row, final int column) {
        if (this.gui.getCurrentPageNumber() <= 1) {
            return;
        }

        this.gui.setItem(row, column, new ItemBuilder().withType(Material.ARROW).asGuiItem(event -> {
            event.setCancelled(true);

            this.gui.previous();

            final int page = this.gui.getCurrentPageNumber();

            if (page <= 1) {
                this.gui.removeItem(row, column);

                this.gui.setItem(row, column, new GuiItem(Material.BLACK_STAINED_GLASS_PANE));
            } else {
                setBackButton(row, column);
            }

            if (page < this.gui.getMaxPages()) {
                setNextButton(6, 6);
            }
        }));
    }

    // Adds the next button
    public void setNextButton(final int row, final int column) {
        if (this.gui.getCurrentPageNumber() >= this.gui.getMaxPages()) {
            return;
        }

        this.gui.setItem(row, column, new ItemBuilder().withType(Material.ARROW).asGuiItem(event -> {
            event.setCancelled(true);

            this.gui.next();

            final int page = this.gui.getCurrentPageNumber();

            if (page >= this.gui.getMaxPages()) {
                this.gui.removeItem(row, column);

                this.gui.setItem(row, column, new GuiItem(Material.BLACK_STAINED_GLASS_PANE));
            } else {
                setNextButton(row, column);
            }

            if (page <= 1) {
                this.gui.removeItem(6, 4);

                this.gui.setItem(6, 4, new GuiItem(Material.BLACK_STAINED_GLASS_PANE));
            } else {
                setBackButton(6, 4);
            }
        }));
    }

    /**
     * Sets the bidding item
     */
    public void setBiddingItem() {
        this.auctions.getProperty(AuctionKeys.bidding_item_button).setItem(event -> {
            setSellingItem();

            //todo() open selling gui
        }, this.gui);
    }

    /**
     * Sets the selling item
     */
    public void setSellingItem() {
        this.auctions.getProperty(AuctionKeys.selling_item_button).setItem(event -> {
            setBiddingItem();

            //todo() open bidding gui
        }, this.gui);
    }
}