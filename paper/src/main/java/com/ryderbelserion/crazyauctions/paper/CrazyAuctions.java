package com.ryderbelserion.crazyauctions.paper;

import com.ryderbelserion.fusion.api.files.FileManager;
import com.ryderbelserion.fusion.api.utils.FileUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Path;

public class CrazyAuctions extends JavaPlugin {

    private FusionPaper api = null;
    private FileManager fileManager = null;

    @Override
    public void onEnable() {
        this.api = new FusionPaper(getComponentLogger(), getDataPath());
        this.api.enable(this);

        this.fileManager = this.api.getFileManager();

        final Path path = getDataPath();

        FileUtils.extract("auctions.json", path.resolve("data"), false);

        this.fileManager.extr

        //this.fileManager.extractResource("auctions.json", "data", false).addFile(path.resolve("auctions.json"));
    }

    @Override
    public void onDisable() {
        if (this.api != null) {
            this.api.save();
        }
    }

    public final FileManager getFileManager() {
        return this.fileManager;
    }

    public final FusionPaper getApi() {
        return this.api;
    }
}