package com.badbones69.crazyauctions.api.builders;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Server;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public abstract class InventoryBuilder implements InventoryHolder, Listener {

    protected @NotNull final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    protected @NotNull final Server server = this.plugin.getServer();

    private Inventory inventory;
    private Player player;
    private String title;
    private int size;
    private int page;

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size) {
        this.player = player;
        this.title = title;
        this.size = size;

        String inventoryTitle = PluginSupport.PLACEHOLDERAPI.isPluginEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtil.parse(inventoryTitle));
    }

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size, final int page) {
        this.player = player;
        this.title = title;
        this.size = size;
        this.page = page;

        String inventoryTitle = PluginSupport.PLACEHOLDERAPI.isPluginEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtil.parse(inventoryTitle));
    }

    public InventoryBuilder() {}

    public abstract InventoryBuilder build();

    public abstract void run(InventoryClickEvent event);

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        run(event);
    }

    public void size(final int size) {
        this.size = size;
    }

    public final int getSize() {
        return this.size;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public final int getPage() {
        return this.page;
    }

    public void title(@NotNull final String title) {
        this.title = title;
    }

    public final boolean contains(@NotNull final String message) {
        return this.title.contains(message);
    }

    public @NotNull final Player getPlayer() {
        return this.player;
    }

    public @NotNull final InventoryView getView() {
        return getPlayer().getOpenInventory();
    }

    public void sendTitleChange() {
        ServerPlayer entityPlayer = (ServerPlayer) ((CraftHumanEntity) getView().getPlayer()).getHandle();
        int containerId = entityPlayer.containerMenu.containerId;
        MenuType<?> windowType = CraftContainer.getNotchInventoryType(getView().getTopInventory());
        entityPlayer.connection.send(new ClientboundOpenScreenPacket(containerId, windowType, CraftChatMessage.fromJSON(JSONComponentSerializer.json().serialize(AdvUtil.parse(this.title)))));
        getPlayer().updateInventory();
    }

    @Override
    public @NotNull final Inventory getInventory() {
        return this.inventory;
    }
}