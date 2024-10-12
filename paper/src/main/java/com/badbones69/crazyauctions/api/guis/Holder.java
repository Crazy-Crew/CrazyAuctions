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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public Holder(final Player player, final String title, final int size, final int page) {
        this(player, null, title, size, page);
    }

    public Holder(Player player, String title, int size) {
        this(player, null, title, size, 1);
    }

    public Holder() {}

    public abstract Holder build();

    public abstract void run(InventoryClickEvent event);

    public void setSize(final int size) {
        this.size = size;
    }

    public final int getSize() {
        return this.size - 9;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public void nextPage() {
        setPage(getPage() + 1);
    }

    public void backPage() {
        setPage(getPage() - 1);
    }

    public final int getPage() {
        return this.page;
    }

    public final List<ItemStack> getPageItems(final List<ItemStack> list, int page, final int size) {
        List<ItemStack> items = new ArrayList<>();

        if (page <= 0) page = 1;

        int index = page * size - size;
        int endIndex = index >= list.size() ? list.size() - 1 : index + size;

        for (;index < endIndex; index++) {
            if (index < list.size()) items.add(list.get(index));
        }

        for (;items.isEmpty(); page--) {
            if (page <= 0) break;

            index = page * size - size;

            endIndex = index >= list.size() ? list.size() - 1 : index + size;

            for (; index < endIndex; index++) {
                if (index < list.size()) items.add(list.get(index));
            }
        }

        return items;
    }

    public final int getMaxPage(final List<ItemStack> list) {
        final int size = list.size();

        return (int) Math.ceil((double) size / getSize());
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        run(event);
    }

    @Override
    public @NotNull final Inventory getInventory() {
        return this.inventory;
    }

    public void click(final Player player) {
        final FileConfiguration config = Files.config.getConfiguration();

        if (config.getBoolean("Settings.Sounds.Toggle", false)) {
            final String sound = config.getString("Settings.Sounds.Sound", "UI_BUTTON_CLICK");

            try {
                player.playSound(player.getLocation(), Sound.valueOf(sound), 1, 1);
            } catch (Exception e) {
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 1F, 1F);
            }
        }
    }
}