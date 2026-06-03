package com.badbones69.crazyauctions.common;

import com.badbones69.crazyauctions.common.registry.MessageImpl;
import com.badbones69.crazyauctions.common.registry.adapters.sender.ISenderAdapter;
import com.badbones69.crazyauctions.common.storage.StorageManager;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import net.kyori.adventure.audience.Audience;
import org.jspecify.annotations.NonNull;
import us.crazycrew.api.CrazyAuctions;
import us.crazycrew.api.storage.IStorageHolder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class CrazyPlugin<S extends Audience> extends CrazyAuctions<S, FusionPaper> {

    protected MessageRegistry messageRegistry;
    protected IStorageHolder storageHolder;
    protected final FusionPaper fusion;
    protected final long startTime;

    public CrazyPlugin(@NonNull final FusionPaper fusion) {
        this.startTime = System.nanoTime();
        this.fusion = fusion;
    }

    public abstract ISenderAdapter getSenderAdapter();

    protected PaperFileManager fileManager;
    protected MessageImpl messageImpl;

    @Override
    public void init() {
        this.fusion.init();

        if (!this.fusion.isPluginEnabled("Vault")) {
            this.fusion.log(Level.ERROR, "Vault was not found on the server, so the plugin will not function!");

            return;
        }

        this.fileManager = this.fusion.getFileManager();

        final Path path = getDataPath();

        try {
            Files.createDirectories(path);
        } catch (final IOException ignored) {}

        this.fileManager.addFile(path.resolve("database.yml"), FileType.YAML)
                .addFile(path.resolve("messages.yml"), FileType.YAML);

        this.messageImpl = new MessageImpl(this.messageRegistry = this.fusion.getMessageRegistry());
        this.messageImpl.init();

        this.fileManager.addPaperFile(path.resolve("config.yml"));

        try {
            this.storageHolder = new StorageManager(this).init();
        } catch (final Exception exception) {
            this.fusion.log(Level.ERROR, "Failed to initialize storage impl", exception);
        }
    }

    @Override
    public void post() {

    }

    @Override
    public void reload() {
        this.fileManager.refresh(false);

        this.messageImpl.reload();

        this.storageHolder.reload();
    }

    @Override
    public @NonNull final MessageRegistry getMessageRegistry() {
        return this.messageRegistry;
    }

    @Override
    public @NonNull final IStorageHolder getStorageHolder() {
        return this.storageHolder;
    }

    @Override
    public @NonNull final FusionPaper getFusion() {
        return this.fusion;
    }

    @Override
    public @NonNull final Path getDataPath() {
        return this.fusion.getDataPath();
    }

    public @NonNull final MessageImpl getMessageImpl() {
        return this.messageImpl;
    }
}