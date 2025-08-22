package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.enums.Messages;
import com.badbones69.crazyauctions.api.support.MetricsWrapper;
import com.badbones69.crazyauctions.commands.features.CommandHandler;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.badbones69.crazyauctions.currency.VaultSupport;
import com.ryderbelserion.fusion.core.api.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.api.enums.Scheduler;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import com.ryderbelserion.fusion.paper.files.FileManager;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public class CrazyAuctions extends JavaPlugin {

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private CrazyManager crazyManager;

    private VaultSupport support;

    private FusionPaper fusion;
    private FileManager fileManager;

    @Override
    public void onEnable() {
        this.fusion = new FusionPaper(this);
        this.fusion.enable(this);

        this.fileManager = this.fusion.getFileManager();

        final Path path = getDataPath();

        this.fileManager.addFile(path.resolve("config.yml"), FileType.PAPER)
                .addFile(path.resolve("data.yml"), FileType.PAPER)
                .addFile(path.resolve("messages.yml"), FileType.PAPER);

        this.crazyManager = new CrazyManager();

        this.crazyManager.load();

        final PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new GuiListener(), this);

        this.support = new VaultSupport();
        this.support.setupEconomy();

        new CommandHandler();

        new FoliaScheduler(this, Scheduler.global_scheduler) {
            @Override
            public void run() {
                Methods.updateAuction();
            }
        }.runAtFixedRate(0L, 5000L);

        Messages.addMissingMessages();

        new MetricsWrapper(this, 4624);
    }

    @Override
    public void onDisable() {
        if (this.crazyManager != null) this.crazyManager.unload();
    }

    public final VaultSupport getSupport() {
        return this.support;
    }

    public final CrazyManager getCrazyManager() {
        return this.crazyManager;
    }

    public final FileManager getFileManager() {
        return this.fileManager;
    }

    public final FusionPaper getFusion() {
        return this.fusion;
    }
}