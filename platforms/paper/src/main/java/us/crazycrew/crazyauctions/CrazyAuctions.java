package us.crazycrew.crazyauctions;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycore.CrazyLogger;
import us.crazycrew.crazycore.paper.PaperCore;
import java.io.File;
import java.nio.file.Path;
import java.util.logging.Logger;

public class CrazyAuctions extends JavaPlugin {

    private static CrazyAuctions plugin;

    private final PaperCore paperCore;

    private final File users;

    public CrazyAuctions(PaperCore paperCore) {
        this.paperCore = paperCore;

        plugin = this;

        this.users = new File(paperCore.getDirectory() + "/userdata");

        if (users.mkdir()) CrazyLogger.info("Created the folder " + users.getName() + ".");
    }

    @Override
    public @NotNull Logger getLogger() {
        return CrazyLogger.getLogger();
    }

    @Override
    public void onEnable() {
        // Enable the player registry.
        getCrazyCore().createPlayerRegistry(this);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public static CrazyAuctions getPlugin() {
        return plugin;
    }

    public PaperCore getCrazyCore() {
        return this.paperCore;
    }

    public Path getUsers() {
        return users.toPath();
    }
}