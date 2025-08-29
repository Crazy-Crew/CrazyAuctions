package com.badbones69.crazyauctions.v2.api.gui.frame.interfaces;

import com.badbones69.crazyauctions.v2.CrazyAuctionsPlus;
import com.badbones69.crazyauctions.v2.api.gui.frame.enums.ActionComponent;
import com.badbones69.crazyauctions.v2.api.gui.frame.items.GuiFiller;
import com.badbones69.crazyauctions.v2.api.gui.frame.items.GuiItem;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;

public abstract class IGui implements InventoryHolder {

    protected final CrazyAuctionsPlus plugin = JavaPlugin.getPlugin(CrazyAuctionsPlus.class);
    protected final FusionPaper fusion = this.plugin.getFusion();
    protected final Server server = this.plugin.getServer();

    protected final Map<Integer, IGuiAction<InventoryClickEvent>> slotAction;
    protected final List<ActionComponent> components;
    protected final Map<Integer, GuiItem> guiItems;

    private IGuiAction<InventoryClickEvent> defaultAction;
    private IGuiAction<InventoryClickEvent> playerAction;

    private IGuiAction<InventoryCloseEvent> closeAction;
    private IGuiAction<InventoryOpenEvent> openAction;

    private final GuiFiller guiFiller;
    private final Inventory inventory;
    private final String title;

    private int size;
    private int rows;

    public IGui(@NotNull final String title, final int rows, @Nullable final Player player, @NotNull final Map<String, String> placeholders) {
        final Component component = this.fusion.color(player != null ? player : Audience.empty(), title, placeholders);

        this.inventory = this.server.createInventory(this, this.size = (this.rows = rows) * 9, component);

        this.title = PlainTextComponentSerializer.plainText().serialize(component);

        this.slotAction = new LinkedHashMap<>(this.size);
        this.guiItems = new LinkedHashMap<>(this.size);
        this.components = new ArrayList<>();

        this.guiFiller = new GuiFiller(this);
    }

    public IGui(@NotNull final String title, final int rows, @NotNull final Map<String, String> placeholders) {
        this(title, rows, null, placeholders);
    }

    public IGui(@NotNull final String title, final int rows, @NotNull final Player player) {
        this(title, rows, player, new HashMap<>());
    }

    public IGui(@NotNull final String title, final int rows) {
        this(title, rows, null, new HashMap<>());
    }

    public IGui build(@NotNull final Player player) {
        player.openInventory(this.inventory);

        return this;
    }

    public String getTitle() {
        return this.title;
    }

    public int getSize() {
        return this.size;
    }

    public int getRows() {
        return getSize() / 9;
    }

    public IGui setDefaultAction(@NotNull final IGuiAction<InventoryClickEvent> defaultAction) {
        this.defaultAction = defaultAction;

        return this;
    }

    public IGuiAction<InventoryClickEvent> getDefaultAction() {
        return this.defaultAction;
    }

    public IGui setPlayerAction(@NotNull final IGuiAction<InventoryClickEvent> playerAction) {
        this.playerAction = playerAction;

        return this;
    }

    public IGuiAction<InventoryClickEvent> getPlayerAction() {
        return this.playerAction;
    }

    public IGui setOpenAction(@NotNull final IGuiAction<InventoryOpenEvent> openAction) {
        this.openAction = openAction;

        return this;
    }

    public IGuiAction<InventoryOpenEvent> getOpenAction() {
        return this.openAction;
    }

    public IGui setCloseAction(@NotNull final IGuiAction<InventoryCloseEvent> closeAction) {
        this.closeAction = closeAction;

        return this;
    }

    public IGuiAction<InventoryCloseEvent> getCloseAction() {
        return this.closeAction;
    }

    public IGui addSlotAction(final int slot, @NotNull final IGuiAction<InventoryClickEvent> action) {
        this.slotAction.put(slot, action);

        return this;
    }

    public IGuiAction<InventoryClickEvent> getSlotAction(final int slot) {
        return this.slotAction.get(slot);
    }

    public void setItem(@NotNull final GuiItem guiItem, final int slot) {
        isSlotValid(slot);

        this.inventory.setItem(slot, guiItem.getItemStack());
        this.guiItems.put(slot, guiItem);
    }

    public void setItem(@NotNull final GuiItem guiItem, @NotNull final List<Integer> slots) {
        final Map<Integer, GuiItem> items = new HashMap<>();

        slots.forEach(slot -> items.put(slot, guiItem));

        setItem(items);
    }

    public void setItem(@NotNull final Map<Integer, GuiItem> items) {
        for (final Map.Entry<Integer, GuiItem> entry : items.entrySet()) {
            final GuiItem item = entry.getValue();
            final int slot = entry.getKey();

            setItem(item, slot);
        }
    }

    public void addItems(@NotNull final GuiItem... items) {
        for (final GuiItem guiItem : items) {
            for (int slot = 0; slot < this.size; slot++) {
                if (this.guiItems.containsKey(slot)) {
                    //todo() verbose log for this

                    continue; // already has a slot taken.
                }

                this.guiItems.put(slot, guiItem);

                break;
            }
        }

        if (this.rows >= 6) return;

        this.size = this.rows++ * 9;
    }

    public void removeItem(final int slot) {
        isSlotValid(slot);

        this.guiItems.remove(slot);
    }

    public GuiItem getGuiItem(final int slot) {
        return this.guiItems.get(slot);
    }

    public GuiFiller getGuiFiller() {
        return this.guiFiller;
    }

    public IGui addComponent(@NotNull final ActionComponent component) {
        this.components.add(component);

        return this;
    }

    public IGui removeComponent(@NotNull final ActionComponent component) {
        this.components.remove(component);

        return this;
    }

    public boolean hasComponent(@NotNull final ActionComponent component) {
        return this.components.contains(component);
    }

    public List<ActionComponent> getComponents() {
        return this.components;
    }

    public void isSlotValid(final int slot) {
        if (slot > 0 || slot <= this.size) return;

        //todo() add to a "failed" cache with some identifier maybe?

        throw new FusionException(String.format("Slot %s is not valid!", slot));
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}