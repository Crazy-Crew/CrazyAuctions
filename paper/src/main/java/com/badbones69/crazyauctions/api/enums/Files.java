package com.badbones69.crazyauctions.api.enums;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.ryderbelserion.vital.paper.api.files.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public enum Files {

    config("config.yml"),
    messages("messages.yml"),
    //test_file("test-file.yml"),
    data("data.yml");

    private final String fileName;
    private final String strippedName;

    private @NotNull final CrazyAuctions plugin = JavaPlugin.getPlugin(CrazyAuctions.class);

    private @NotNull final FileManager fileManager = this.plugin.getFileManager();

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(final String fileName) {
        this.fileName = fileName;
        this.strippedName = this.fileName.replace(".yml", "");
    }

    public final YamlConfiguration getConfiguration() {
        return this.fileManager.getFile(this.fileName).getConfiguration();
    }

    public final String getStrippedName() {
        return this.strippedName;
    }

    public final String getFileName() {
        return this.fileName;
    }

    public void save() {
        this.fileManager.saveFile(this.fileName);
    }

    public void reload() {
        this.fileManager.addFile(this.fileName);
    }
}