package com.badbones69.crazyauctions.common.enums;

import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.files.FileException;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Optional;

public enum FileKeys {

    config("config.yml", FileType.PAPER_YAML),
    //test_file("test-file.yml"),
    data("data.yml", FileType.PAPER_YAML),

    messages("messages.yml", FileType.YAML),
    database("database.yml", FileType.YAML);

    private final FusionPaper fusion = (FusionPaper) FusionProvider.getInstance();

    private final PaperFileManager fileManager = this.fusion.getFileManager();

    private final Path path = this.fusion.getDataPath();

    private final Path location;
    private final FileType fileType;

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    FileKeys(@NotNull final String fileName, @NotNull final FileType fileType) {
        this.location = this.path.resolve(fileName);
        this.fileType = fileType;
    }

    public @NotNull final YamlConfiguration getConfiguration() {
        return getPaperCustomFile().getConfiguration();
    }

    public @NotNull final PaperCustomFile getPaperCustomFile() {
        @NotNull final Optional<PaperCustomFile> customFile = this.fileManager.getPaperFile(this.location);

        if (customFile.isEmpty()) {
            throw new FileException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final CommentedConfigurationNode getYamlConfig() {
        return getYamlCustomFile().getConfiguration();
    }

    public @NotNull final YamlCustomFile getYamlCustomFile() {
        @NotNull final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.location);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final Path getPath() {
        return this.location;
    }

    public void save() {
        switch (this.fileType) {
            case PAPER_YAML -> getPaperCustomFile().save();
            case YAML -> getYamlCustomFile().save();
        }
    }

    public void load() {
        switch (this.fileType) {
            case PAPER_YAML -> getPaperCustomFile().load();
            case YAML -> getYamlCustomFile().load();
        }
    }
}