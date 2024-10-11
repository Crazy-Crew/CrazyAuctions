package com.badbones69.crazyauctions.api.guis;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.ryderbelserion.vital.paper.api.enums.Support;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class Holder implements InventoryHolder, Listener {

    protected final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    protected final CrazyManager crazyManager = this.plugin.getCrazyManager();

    protected final Server server = this.plugin.getServer();

    protected Inventory inventory;
    protected ShopType shopType;
    protected Player player;
    protected String title;
    protected int size;
    protected int page;

    public Holder(final Player player, final ShopType shopType, final String title, final int size, final int page) {
        this.player = player;
        this.shopType = shopType;
        this.title = title;
        this.size = size;
        this.page = page;

        final String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(this.player, this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, Methods.color(inventoryTitle));
    }

    public Holder(final Player player, final ShopType shopType, final String title, final int size) {
        this(player, shopType, title, size, 1);
    }

    public Holder() {}

    public abstract Holder build();

    public abstract void run(InventoryClickEvent event);

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        run(event);
    }

    @Override
    public @NotNull final Inventory getInventory() {
        return this.inventory;
    }

    public void click() {
        final FileConfiguration config = Files.config.getConfiguration();

        if (config.getBoolean("Settings.Sounds.Toggle", false)) {
            final String sound = config.getString("Settings.Sounds.Sound", "UI_BUTTON_CLICK");

            try {
                this.player.playSound(this.player.getLocation(), Sound.valueOf(sound), 1, 1);
            } catch (Exception e) {
                this.player.playSound(this.player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1F);
            }
        }
    }
}