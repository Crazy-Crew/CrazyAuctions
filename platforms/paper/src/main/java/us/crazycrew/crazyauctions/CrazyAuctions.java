package us.crazycrew.crazyauctions;

import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazyauctions.storage.StorageManager;
import us.crazycrew.crazycore.paper.PaperCore;
import java.io.File;
import java.nio.file.Path;

/**
 * Description: The java plugin instance of our plugin where we handle post world startup tasks.
 */
public class CrazyAuctions extends JavaPlugin {

    private static CrazyAuctions plugin;

    private final PaperCore paperCore;

    private final File users;

    private StorageManager storageManager;

    public CrazyAuctions(PaperCore paperCore) {
        this.paperCore = paperCore;

        plugin = this;

        this.users = new File(paperCore.getDirectory() + "/userdata");

        if (users.mkdir()) getLogger().info("Created the folder " + users.getName() + ".");
    }

    @Override
    public void onEnable() {

        this.storageManager = new StorageManager();
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
        return this.users.toPath();
    }

    public StorageManager getStorageManager() {
        return this.storageManager;
    }
}