package us.crazycrew.crazyauctions;

import com.badbones69.crazyauctions.common.config.types.Config;
import com.ryderbelserion.cluster.ClusterFactory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.api.PaperAbstractPlugin;
import us.crazycrew.crazyauctions.api.database.Storage;
import us.crazycrew.crazyauctions.api.database.StorageFactory;
import us.crazycrew.crazyauctions.commands.CommandManager;

public class CrazyAuctions extends JavaPlugin {

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private final PaperAbstractPlugin plugin;

    public CrazyAuctions(PaperAbstractPlugin plugin) {
        this.plugin = plugin;
    }

    private CommandManager commandManager;
    private ClusterFactory cluster;

    @Override
    public void onLoad() {
        // Load command manager
        this.commandManager = new CommandManager();
        this.commandManager.load();
    }

    private Storage storage;

    @Override
    public void onEnable() {
        // Enable cluster factory.
        this.cluster = new ClusterFactory(this, this.plugin.getConfig().getProperty(Config.verbose_logging));
        this.cluster.enable();

        // Load storage factory.
        StorageFactory storageFactory = new StorageFactory();
        this.storage = storageFactory.getInstance();
    }

    @Override
    public void onDisable() {
        // Shutdown storage factory.
        if (this.storage != null) this.storage.shutdown();

        // Shut down cluster factory.
        if (this.cluster != null) this.cluster.disable();
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }
}