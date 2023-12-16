package us.crazycrew.crazyauctions;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.api.PaperAbstractPlugin;
import us.crazycrew.crazyauctions.api.database.Storage;
import us.crazycrew.crazyauctions.api.database.StorageFactory;
import us.crazycrew.crazyauctions.commands.CommandManager;

public class CrazyAuctions extends JavaPlugin {

    private final PaperAbstractPlugin plugin;

    public CrazyAuctions(PaperAbstractPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private CommandManager commandManager;

    @Override
    public void onLoad() {
        // Load command manager
        this.commandManager = new CommandManager();
        this.commandManager.load();
    }

    private Storage storage;

    @Override
    public void onEnable() {
        // Enable command manager.
        this.commandManager.enable();

        // Load storage factory.
        StorageFactory storageFactory = new StorageFactory();
        this.storage = storageFactory.getInstance();
    }

    @Override
    public void onDisable() {
        // Shutdown storage factory.
        this.storage.shutdown();
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public Storage getStorage() {
        return this.storage;
    }
}