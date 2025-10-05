package com.badbones69.crazyauctions;

import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.AuctionsProvider;
import us.crazycrew.crazyauctions.objects.ICrazyAuctions;
import java.nio.file.Path;

public class CrazyPlugin implements ICrazyAuctions {

    private final PaperFileManager fileManager;
    private final CrazyAuctions plugin;
    private final FusionPaper fusion;
    private final Path path;

    public CrazyPlugin(@NotNull final CrazyAuctions plugin) {
        this.plugin = plugin;
        this.fusion = new FusionPaper(this.plugin);
        this.fileManager = this.fusion.getFileManager();
        this.path = this.plugin.getDataPath();
    }

    @ApiStatus.Internal
    public void init() {
        this.fileManager.addPaperFile(this.path.resolve("config.yml"))
                .addPaperFile(this.path.resolve("data.yml"))
                .addPaperFile(this.path.resolve("messages.yml"));

        AuctionsProvider.register(this);
    }

    @ApiStatus.Internal
    public @NotNull final PaperFileManager getFileManager() {
        return this.fileManager;
    }

    @ApiStatus.Internal
    public @NotNull final FusionPaper getFusion() {
        return this.fusion;
    }

    @Override
    public void reload() {
        this.fusion.reload();
    }

    @Override
    public @NotNull final ComponentLogger getLogger() {
        return this.plugin.getComponentLogger();
    }
}