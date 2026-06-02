package com.badbones69.crazyauctions.api.enums.misc;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.ryderbelserion.fusion.files.FileException;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;
import java.util.Optional;

public enum Files {

    config("config.yml"),
    messages("messages.yml"),
    //test_file("test-file.yml"),
    data("data.yml");

    private final CrazyAuctions plugin = CrazyAuctions.get();
    private final PaperFileManager fileManager = this.plugin.getFileManager();
    private final Path path = this.plugin.getDataPath();

    private final Path location;

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    Files(@NotNull final String fileName) {
        this.location = this.path.resolve(fileName);
    }

    public @NotNull final YamlConfiguration getConfiguration() {
        return getYamlCustomFile().getConfiguration();
    }

    public @NotNull final PaperCustomFile getYamlCustomFile() {
        @NotNull final Optional<PaperCustomFile> customFile = this.fileManager.getPaperFile(this.location);

        if (customFile.isEmpty()) {
            throw new FileException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final Path getPath() {
        return this.location;
    }

    public void save() {
        getYamlCustomFile().save();
    }

    public void load() {
        getYamlCustomFile().load();
    }
}