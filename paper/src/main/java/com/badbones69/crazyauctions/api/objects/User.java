package com.badbones69.crazyauctions.api.objects;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyauctions.configs.ConfigManager;
import com.ryderbelserion.vital.paper.api.enums.Support;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("UnusedReturnValue")
public class User {

    public final CommandSender sender;
    public final Player player;

    public User(final CommandSender sender) {
        if (sender instanceof Player target) {
            this.sender = target;
            this.player = target;
        } else {
            this.sender = sender;
            this.player = null;
        }
    }

    public String locale = "en_US";

    public final List<String> activeBypassTypes = new ArrayList<>();

    public boolean isPvpEnabled = false;

    public transient BossBar bossBar = null;

    public final User showBossBar() {
        if (this.player == null) return this;

        final BossBar bar = this.bossBar;

        this.player.showBossBar(bar);

        return this;
    }

    public final User createBossBar(final String name) {
        if (this.player == null) return this;

        this.bossBar = BossBar.bossBar(
                AdvUtil.parse(Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, name) : name),
                0,
                BossBar.Color.PURPLE,
                BossBar.Overlay.NOTCHED_12
        );

        return this;
    }

    public final User hideBossBar() {
        if (this.bossBar == null || this.player == null) return null;

        this.player.hideBossBar(this.bossBar);

        this.bossBar = null;

        return this;
    }

    public final User updateLocale(final Locale locale) {
        if (this.player == null) return this;

        this.locale = locale.getLanguage() + "_" + locale.getCountry();

        return this;
    }

    public final SettingsManager getLocale() {
        return ConfigManager.getLocale(this.locale);
    }

    // other checks
    public final boolean hasPermission(final String permission) {
        if (this.player == null) return false;

        return this.player.hasPermission(permission);
    }

    public final String getName() {
        return this.player != null ? this.player.getName() : this.sender.getName();
    }
}