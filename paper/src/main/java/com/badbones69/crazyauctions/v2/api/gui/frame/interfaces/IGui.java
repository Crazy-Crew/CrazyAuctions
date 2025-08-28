package com.badbones69.crazyauctions.v2.api.gui.frame.interfaces;

import com.badbones69.crazyauctions.v2.CrazyAuctionsPlus;
import com.badbones69.crazyauctions.v2.api.gui.frame.items.GuiFiller;
import com.badbones69.crazyauctions.v2.api.gui.frame.items.GuiItem;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class IGui implements InventoryHolder {

    protected final CrazyAuctionsPlus plugin = JavaPlugin.getPlugin(CrazyAuctionsPlus.class);
    protected final FusionPaper fusion = this.plugin.getFusion();
    protected final Server server = this.plugin.getServer();

    protected final Map<Integer, IGuiAction<InventoryClickEvent>> slotAction;
    protected final Map<Integer, GuiItem> guiItems;

    private IGuiAction<InventoryClickEvent> defaultAction;
    private IGuiAction<InventoryClickEvent> playerAction;

    private IGuiAction<InventoryCloseEvent> closeAction;
    private IGuiAction<InventoryOpenEvent> openAction;

    private final Inventory inventory;
    private final String title;
    private final int size;

    private final GuiFiller guiFiller;

    public IGui(@NotNull final String title, final int rows, @Nullable final Player player, @NotNull final Map<String, String> placeholders) {
        final Component component = this.fusion.color(player != null ? player : Audience.empty(), title, placeholders);

        this.inventory = this.server.createInventory(this, this.size = rows * 9, component);

        this.title = PlainTextComponentSerializer.plainText().serialize(component);

        this.slotAction = new LinkedHashMap<>(this.size);
        this.guiItems = new LinkedHashMap<>(this.size);

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

    public void setDefaultAction(@NotNull final IGuiAction<InventoryClickEvent> defaultAction) {
        this.defaultAction = defaultAction;
    }

    public IGuiAction<InventoryClickEvent> getDefaultAction() {
        return this.defaultAction;
    }

    public void setPlayerAction(@NotNull final IGuiAction<InventoryClickEvent> playerAction) {
        this.playerAction = playerAction;
    }

    public IGuiAction<InventoryClickEvent> getPlayerAction() {
        return this.playerAction;
    }

    public void setOpenAction(@NotNull final IGuiAction<InventoryOpenEvent> openAction) {
        this.openAction = openAction;
    }

    public IGuiAction<InventoryOpenEvent> getOpenAction() {
        return this.openAction;
    }

    public void setCloseAction(@NotNull final IGuiAction<InventoryCloseEvent> closeAction) {
        this.closeAction = closeAction;
    }

    public IGuiAction<InventoryCloseEvent> getCloseAction() {
        return this.closeAction;
    }

    public void addSlotAction(final int slot, @NotNull final IGuiAction<InventoryClickEvent> action) {
        this.slotAction.put(slot, action);
    }

    public IGuiAction<InventoryClickEvent> getSlotAction(final int slot) {
        return this.slotAction.get(slot);
    }

    public GuiItem getGuiItem(final int slot) {
        return this.guiItems.get(slot);
    }

    public GuiFiller getGuiFiller() {
        return this.guiFiller;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return this.inventory;
    }
}
