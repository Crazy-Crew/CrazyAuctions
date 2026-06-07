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

public enum FileKey {

    config("config.yml", FileType.PAPER_YAML),
    //test_file("test-file.yml"),
    data("data.yml", FileType.PAPER_YAML),

    messages("messages.yml", FileType.YAML),
    database("database.yml", FileType.YAML),

    auction("auction.yml", "guis", FileType.YAML);

    private final FusionPaper fusion = (FusionPaper) FusionProvider.getInstance();

    private final PaperFileManager fileManager = this.fusion.getFileManager();

    private final Path path = this.fusion.getDataPath();

    private final FileType fileType;
    private final Path location; // the file location
    private final Path folder; // the folder which defaults to the data path

    FileKey(@NotNull final String fileName, @NotNull final String folder, @NotNull final FileType fileType) {
        this.folder = this.path.resolve(folder);
        this.location = this.folder.resolve(fileName);
        this.fileType = fileType;
    }

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    FileKey(@NotNull final String fileName, @NotNull final FileType fileType) {
        this.folder = this.path;
        this.location = this.folder.resolve(fileName);
        this.fileType = fileType;
    }

    public @NotNull final YamlConfiguration getConfiguration() {
        return getPaperCustomFile().getConfiguration();
    }

    public @NotNull final PaperCustomFile getPaperCustomFile() {
        final Optional<PaperCustomFile> customFile = this.fileManager.getPaperFile(this.location);

        if (customFile.isEmpty()) {
            throw new FileException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final CommentedConfigurationNode getYamlConfig() {
        return getYamlCustomFile().getConfiguration();
    }

    public @NotNull final YamlCustomFile getYamlCustomFile() {
        final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.location);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.location);
        }

        return customFile.get();
    }

    public @NotNull final FileType getFileType() {
        return this.fileType;
    }

    public @NotNull final Path getPath() {
        return this.location;
    }

    public void save() {
        if (!this.fileManager.hasFile(this.location)) {
            return;
        }

        switch (this.fileType) {
            case PAPER_YAML -> getPaperCustomFile().save();
            case YAML -> getYamlCustomFile().save();
        }
    }

    public void load() {
        if (!this.fileManager.hasFile(this.location)) {
            return;
        }

        switch (this.fileType) {
            case PAPER_YAML -> getPaperCustomFile().load();
            case YAML -> getYamlCustomFile().load();
        }
    }
}