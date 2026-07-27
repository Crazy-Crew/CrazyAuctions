package com.badbones69.crazyauctions.common;

import com.badbones69.crazyauctions.common.enums.keys.FileKeys;
import com.badbones69.crazyauctions.common.enums.messages.Messages;
import com.badbones69.crazyauctions.common.storage.StorageManager;
import com.badbones69.crazyauctions.common.storage.holder.StorageHolder;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.AuctionsProvider;
import us.crazycrew.api.CrazyAuctions;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public abstract class CrazyAuctionsPlugin<I> extends CrazyAuctions<FusionPaper, I> {

    protected MessageRegistry messageRegistry;
    protected final FusionPaper fusion;
    protected final long startTime;

    public CrazyAuctionsPlugin(final FusionPaper fusion, final Path path) {
        super(path);

        this.startTime = System.nanoTime();
        this.fusion = fusion;
    }

    protected StorageManager storageManager;
    protected PaperFileManager fileManager;
    protected StorageHolder storageHolder;

    @Override
    public void init() {
        this.fusion.init().post();

        if (!this.fusion.isPluginEnabled("Vault")) {
            this.fusion.log(Level.ERROR, "Vault was not found on the server, so the plugin will not function!");

            return;
        }

        this.fusion.post();

        this.messageRegistry = this.fusion.getMessageRegistry();
        this.fileManager = this.fusion.getFileManager();

        for (final FileKeys key : FileKeys.values()) {
            key.load();
        }

        loadExamples();

        this.storageManager = new StorageManager(this);

        this.storageHolder = this.storageManager.init();
        this.storageHolder.save();

        AuctionsProvider.register(this);
    }

    @Override
    public void post() {

    }

    @Override
    public void reload() {
        for (final FileKeys key : FileKeys.values()) {
            key.reload();
        }

        this.storageHolder.save().reload();

        loadMessages();
        loadExamples();
    }

    @Override
    public void loadMessages() {
        final List<Path> paths = this.fileManager.getFilesByPath(this.path.resolve("locale"), ".yml", this.fileManager.getDepth());

        paths.add(this.path.resolve("messages.yml")); // add to list

        this.fusion.getMessageRegistry().init(action -> {
            for (final Path path : paths) {
                this.fileManager.getYamlFile(path).ifPresentOrElse(customFile -> {
                    final String fileName = customFile.getFileName();

                    final FusionKey key = FusionKey.key(CrazyAuctions.namespace, fileName.equalsIgnoreCase("messages.yml") ? "default" : fileName.toLowerCase());

                    final CommentedConfigurationNode configuration = customFile.getConfiguration();

                    for (final Messages message : Messages.values()) {
                        message.addKey(action, configuration, key);
                    }
                }, () -> this.fusion.log(Level.INFO, "Path %s not found in cache.".formatted(path)));
            }
        });
    }

    @Override
    public void loadExamples() {
        final Path examples = getDataPath().resolve("examples");

        if (Files.exists(examples)) {
            try (final Stream<Path> values = Files.walk(examples)) {
                values.sorted(Comparator.reverseOrder()).forEach(path -> { // sorted in reverse order, to ensure the directories are empty first.
                    try {
                        this.fusion.log(Level.WARNING, "Successfully deleted path %s, re-generating the examples later.", path);

                        Files.delete(path);
                    } catch (final IOException exception) {
                        this.fusion.log(Level.WARNING, "Failed to delete %s in loop.", exception, path);
                    }
                });
            } catch (final Exception exception) {
                this.fusion.log(Level.WARNING, "Failed to delete %s.", exception, examples);
            }
        }

        List.of(
                "config.yml",
                "data.yml",
                "messages.yml"
        ).forEach(file -> this.fileManager.extractFile(file, examples.resolve(file)));
    }

    @Override
    public @NonNull final MessageRegistry getMessageRegistry() {
        return this.messageRegistry;
    }

    @Override
    public @NonNull final FusionPaper getFusion() {
        return this.fusion;
    }

    @Override
    public @NonNull final Path getDataPath() {
        return this.path;
    }

    public @NonNull final StorageHolder getStorageHolder() {
        return this.storageHolder;
    }
}