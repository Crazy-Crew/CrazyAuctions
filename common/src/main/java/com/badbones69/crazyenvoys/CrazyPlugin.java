package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.storage.StorageManager;
import com.ryderbelserion.fusion.core.api.enums.Level;
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
import java.util.UUID;

public abstract class CrazyPlugin<S extends Audience> extends CrazyAuctions<S, FusionPaper> {

    public static final UUID CONSOLE_UUID = new UUID(0, 0);
    public static final String CONSOLE_NAME = "Console";

    protected IStorageHolder storageHolder;
    protected final FusionPaper fusion;
    protected final long startTime;

    public CrazyPlugin(@NonNull final FusionPaper fusion) {
        this.startTime = System.nanoTime();
        this.fusion = fusion;
    }

    protected PaperFileManager fileManager;

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

        this.fileManager.addFile(path.resolve("database.yml"), FileType.YAML);

        this.fileManager.addPaperFile(path.resolve("config.yml"))
                .addPaperFile(path.resolve("messages.yml"));

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

        this.storageHolder.reload();
    }

    @Override
    public @NonNull IStorageHolder getStorageHolder() {
        return this.storageHolder;
    }

    @Override
    public @NonNull Path getDataPath() {
        return this.fusion.getDataPath();
    }

    @Override
    public @NonNull FusionPaper getFusion() {
        return this.fusion;
    }
}