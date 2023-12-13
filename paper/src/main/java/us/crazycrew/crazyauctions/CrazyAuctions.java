package us.crazycrew.crazyauctions;

import com.badbones69.crazyauctions.common.AuctionsFactory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.commands.CommandManager;

public class CrazyAuctions extends JavaPlugin {

    private final AuctionsFactory factory;

    public CrazyAuctions(AuctionsFactory factory) {
        this.factory = factory;
    }

    @NotNull
    public static CrazyAuctions get() {
        return JavaPlugin.getPlugin(CrazyAuctions.class);
    }

    private CommandManager commandManager;

    @Override
    public void onLoad() {
        this.commandManager = new CommandManager();
        this.commandManager.load();
    }

    @Override
    public void onEnable() {
        this.commandManager.enable();
    }

    @Override
    public void onDisable() {
        this.factory.reload();
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public AuctionsFactory getFactory() {
        return this.factory;
    }
}