package com.badbones69.crazyauctions.api.enums;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public enum Files {

    config("config.yml"),
    messages("messages.yml"),
    data("data.yml");

    private @NotNull final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private @NotNull final PaperFileManager fileManager = this.plugin.getFileManager();

    private final Path path;

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(@NotNull final String fileName) {
        this.path = this.plugin.getDataPath().resolve(fileName);
    }

    public final YamlConfiguration getConfiguration() {
        return this.fileManager.getPaperFile(this.path).orElseThrow().getConfiguration();
    }

    public void save() {
        this.fileManager.saveFile(this.path);
    }

    public void reload() {
        this.fileManager.reloadFile(this.path);
    }
}