package com.badbones69.crazyauctions.api.enums.misc;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.ryderbelserion.fusion.paper.files.FileManager;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

public enum Files {

    config("config.yml"),
    messages("messages.yml"),
    //test_file("test-file.yml"),
    data("data.yml");

    private final Path path;

    private @NotNull final CrazyAuctions plugin = CrazyAuctions.get();

    private @NotNull final Path dataPath = this.plugin.getDataPath();

    private @NotNull final FileManager fileManager = this.plugin.getFileManager();

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(@NotNull final String fileName) {
        this.path = this.dataPath.resolve(fileName);
    }

    public final YamlConfiguration getConfiguration() {
        return getCustomFile().getConfiguration();
    }

    public final PaperCustomFile getCustomFile() {
        return this.fileManager.getPaperCustomFile(this.path);
    }

    public void save() {
        this.fileManager.saveFile(this.path);
    }
}