package com.badbones69.crazyauctions.configs.beans;

import ch.jalu.configme.Comment;
import com.badbones69.crazyauctions.api.builders.ItemBuilder;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiAction;
import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.GuiItem;
import com.ryderbelserion.vital.paper.api.builders.gui.types.PaginatedGui;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

public class ButtonProperty {

    @Comment("The name of the item.")
    public String displayName;

    @Comment("The lore of the item.")
    public List<String> displayLore;

    @Comment("The material of the item.")
    public String material;

    @Comment("The slot of the item, Set to -1 to disable.")
    public int slot;

    public final ButtonProperty populate(final String buttonName) {
        switch (buttonName) {
            case "expired-item" -> {
                this.displayName = "<bold><gray>(<gold>!<gray>)</bold> <gold>Expired Items";
                this.displayLore = List.of(
                        "<gray>Click to view items that have expired."
                );
                this.material = "poisonous_potato";
                this.slot = 46;
            }

            case "selling-item" -> {
                this.displayName = "<bold><gray>(<gold>!<gray>)</bold> <gold>Items that are being sold";
                this.displayLore = List.of(
                        "<gray>Click to see items to bid on."
                );
                this.material = "emerald";
                this.slot = 49;
            }

            case "bidding-item" -> {
                this.displayName = "<bold><gray>(<gold>!<gray>)</bold> <gold>Items that can be bid on";
                this.displayLore = List.of(
                        "<gray>Click to see items that can be bought."
                );
                this.material = "magma_cream";
                this.slot = 49;
            }

            case "sold-item" -> {
                this.displayName = "<bold><gray>(<gold>!<gray>)</bold> <gold>Items you are currently selling";
                this.displayLore = List.of(
                        "<gray>Click to view all items you have up for auction."
                );
                this.material = "gold_ingot";
                this.slot = 52;
            }
        }

        return this;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public void setDisplayLore(final List<String> displayLore) {
        this.displayLore = displayLore;
    }

    public void setMaterial(final String material) {
        this.material = material;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public List<String> getDisplayLore() {
        return this.displayLore;
    }

    public String getMaterial() {
        return this.material;
    }

    public int getSlot() {
        return this.slot;
    }

    public final ItemBuilder getItemStack() {
        return new ItemBuilder().withType(getMaterial()).setDisplayName(getDisplayName()).setDisplayLore(getDisplayLore());
    }

    public final GuiItem getGuiItem(final @Nullable GuiAction<@NotNull InventoryClickEvent> event) {
        return getItemStack().asGuiItem(event);
    }

    public void setItem(final @Nullable GuiAction<@NotNull InventoryClickEvent> event, final PaginatedGui gui) {
        gui.setItem(getSlot(), getGuiItem(event));
    }
}