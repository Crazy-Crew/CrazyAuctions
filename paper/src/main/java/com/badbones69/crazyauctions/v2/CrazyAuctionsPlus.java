package com.badbones69.crazyauctions.v2;

import com.badbones69.crazyauctions.v2.api.CrazyManager;
import com.badbones69.crazyauctions.v2.api.gui.frame.GuiListener;
import com.badbones69.crazyauctions.v2.commands.CommandHandler;
import com.ryderbelserion.fusion.core.api.enums.FileAction;
import com.ryderbelserion.fusion.core.api.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.FileManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.ArrayList;

public class CrazyAuctionsPlus extends JavaPlugin {

    private FusionPaper fusion;

    private FileManager fileManager;
    private CrazyManager crazyManager;

    @Override
    public void onEnable() {
        this.fusion = new FusionPaper(this);
        this.fusion.enable(this);

        this.fileManager = this.fusion.getFileManager();

        final Path path = getDataPath();

        this.fileManager.addFile(path.resolve("v2-config.yml"), FileType.CONFIGURATE)
                .addFolder(path.resolve("guis"), FileType.CONFIGURATE, new ArrayList<>() {{
                    add(FileAction.STATIC_FILE);
                }}, null);

        this.crazyManager = new CrazyManager();
        this.crazyManager.init();

        new CommandHandler(); // enable commands

        getServer().getPluginManager().registerEvents(new GuiListener(), this);
    }

    @Override
    public void onDisable() {

    }

    public @NotNull final FileManager getFileManager() {
        return this.fileManager;
    }

    public @NotNull final FusionPaper getFusion() {
        return this.fusion;
    }
}