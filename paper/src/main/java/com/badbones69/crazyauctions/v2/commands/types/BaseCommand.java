package com.badbones69.crazyauctions.v2.commands.types;

import com.badbones69.crazyauctions.v2.CrazyAuctionsPlus;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.FileManager;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

@Command(value = "crazyauctions", alias = {"ah", "ca"})
public abstract class BaseCommand {

    protected final CrazyAuctionsPlus plugin = JavaPlugin.getPlugin(CrazyAuctionsPlus.class);

    protected final FusionPaper fusion = this.plugin.getFusion();

    protected final FileManager fileManager = this.plugin.getFileManager();

    protected final Path path = this.plugin.getDataPath();

}