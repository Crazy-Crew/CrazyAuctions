package us.crazycrew.crazyauctions.api;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyauctions.common.config.ConfigFactory;
import com.badbones69.crazyauctions.common.api.interfaces.AbstractPlugin;
import java.io.File;

public class PaperAbstractPlugin extends AbstractPlugin {

    private final File file;

    public PaperAbstractPlugin(File file) {
        this.file = file;
    }

    @Override
    public File getDataFolder() {
        return this.file;
    }

    private ConfigFactory factory;

    @Override
    public void init() {
        super.enable();

        this.factory = new ConfigFactory();
        this.factory.load();
    }

    @Override
    public void stop() {
        super.disable();

        this.factory.reload();
    }

    @Override
    public void reload() {
        this.factory.reload();
    }

    @Override
    public SettingsManager getConfig() {
        return this.factory.getConfig();
    }

    @Override
    public SettingsManager getLocale() {
        return this.factory.getMessages();
    }
}