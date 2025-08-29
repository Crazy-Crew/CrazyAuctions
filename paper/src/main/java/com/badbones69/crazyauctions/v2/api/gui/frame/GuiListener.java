package com.badbones69.crazyauctions.v2.api.gui.frame;

import com.badbones69.crazyauctions.v2.api.gui.frame.enums.ActionComponent;
import com.badbones69.crazyauctions.v2.api.gui.frame.interfaces.IGui;
import com.badbones69.crazyauctions.v2.api.gui.frame.interfaces.IGuiAction;
import com.badbones69.crazyauctions.v2.api.gui.frame.items.GuiItem;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

@SuppressWarnings("SwitchStatementWithTooFewBranches")
public class GuiListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        final Inventory inventory = event.getClickedInventory();

        if (inventory == null || !(inventory.getHolder(false) instanceof IGui gui)) return; // check if it's the base gui first.

        if (gui.hasComponent(ActionComponent.DISABLE_ALL_INTERACTIONS)) {
            event.setResult(Event.Result.DENY); // deny if we detect a click event.
        }

        final InventoryType inventoryType = inventory.getType();

        switch (inventoryType) {
            case PLAYER -> {
                final IGuiAction<InventoryClickEvent> playerAction = gui.getPlayerAction();

                if (playerAction != null) {
                    playerAction.execute(event);
                }
            }

            default -> {
                final IGuiAction<InventoryClickEvent> defaultAction = gui.getDefaultAction();

                if (defaultAction != null) { // execute default action.
                    defaultAction.execute(event);
                }

                final int rawSlot = event.getRawSlot();

                final IGuiAction<InventoryClickEvent> slotAction = gui.getSlotAction(rawSlot);

                if (slotAction != null) { // execute slot action.
                    slotAction.execute(event);
                }

                final GuiItem guiItem = gui.getGuiItem(rawSlot);

                if (guiItem == null) return;

                final IGuiAction<InventoryClickEvent> guiAction = guiItem.getAction();

                if (guiAction != null) {
                    guiAction.execute(event);
                }
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof IGui)) { // check if it's the base gui first.
            return;
        }

        final Inventory topInventory = event.getView().getTopInventory();

        if (event.getRawSlots().stream().anyMatch(slot -> slot < topInventory.getSize())) { // deny if we detect drag from bottom inventory.
            event.setResult(Event.Result.DENY);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof IGui gui)) { // check if it's the base gui first.
            return;
        }

        final IGuiAction<InventoryCloseEvent> closeAction = gui.getCloseAction();

        if (closeAction != null) {
            closeAction.execute(event);
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        final Inventory inventory = event.getInventory();

        if (!(inventory.getHolder(false) instanceof IGui gui)) { // check if it's the base gui first.
            return;
        }

        final IGuiAction<InventoryOpenEvent> openAction = gui.getOpenAction();

        if (openAction != null) {
            openAction.execute(event);
        }
    }
}