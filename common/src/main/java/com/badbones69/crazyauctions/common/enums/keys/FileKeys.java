package com.badbones69.crazyauctions.common.enums.keys;

import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.files.FileException;
import com.ryderbelserion.fusion.files.enums.FileAction;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import com.ryderbelserion.fusion.paper.files.types.PaperCustomFile;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@NullMarked
public enum FileKeys {

    config("config.yml", FileType.PAPER_YAML, List.of(
            FileAction.EXTRACT_FILE
    )),

    data("data.yml", FileType.PAPER_YAML, List.of(
            FileAction.EXTRACT_FILE
    )),

    messages("messages.yml", FileType.YAML, List.of(
            FileAction.EXTRACT_FILE
    ));

    private final FusionPaper fusion = (FusionPaper) FusionProvider.getInstance();
    private final PaperFileManager fileManager = this.fusion.getFileManager();
    private final Path dataPath = this.fusion.getDataPath();

    private final List<FileAction> actions;
    private final FileType fileType;
    private final Path path;

    /**
     * A constructor to build a file
     *
     * @param fileName the name of the file
     */
    FileKeys(final String fileName, final FileType fileType, final List<FileAction> actions) {
        this.path = this.dataPath.resolve(fileName);
        this.fileType = fileType;
        this.actions = actions;
    }

    public final YamlConfiguration getConfiguration() {
        return getPaperCustomFile().getConfiguration();
    }

    public final PaperCustomFile getPaperCustomFile() {
        final Optional<PaperCustomFile> customFile = this.fileManager.getPaperFile(this.path);

        if (customFile.isEmpty()) {
            throw new FileException("Could not find custom file for " + this.path);
        }

        return customFile.get();
    }

    public final CommentedConfigurationNode getYamlConfiguration() {
        return getYamlCustomFile().getConfiguration();
    }

    public final YamlCustomFile getYamlCustomFile() {
        final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.path);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.path);
        }

        return customFile.get();
    }

    public void reload() {
        if (this.actions.contains(FileAction.DELETE_FILE)) {
            try {
                java.nio.file.Files.deleteIfExists(this.path);
            } catch (final IOException exception) {
                this.fusion.log(Level.ERROR, "Failed to delete: %s", exception, this.path);
            }
        }

        if (!hasFile()) {
            return;
        }

        switch (this.fileType) {
            case PAPER_YAML -> this.fileManager.addPaperFile(this.path, action -> action.addActions(this.actions).addAction(FileAction.RELOAD_FILE));
            default -> this.fileManager.addFile(this.path, this.fileType, action -> action.addActions(this.actions).addAction(FileAction.RELOAD_FILE));
        }
    }

    public void save() {
        if (!hasFile()) {
            return;
        }

        this.fileManager.saveFile(this.path);
    }

    public void load() {
        switch (this.fileType) {
            case PAPER_YAML -> this.fileManager.addPaperFile(this.path, action -> action.addActions(this.actions));
            default -> this.fileManager.addFile(this.path, this.fileType, action -> action.addActions(this.actions));
        }
    }

    public final FileType getFileType() {
        return this.fileType;
    }

    public final boolean hasFile() {
        return this.fileManager.hasFile(this.path);
    }

    public final Path getPath() {
        return this.path;
    }
}