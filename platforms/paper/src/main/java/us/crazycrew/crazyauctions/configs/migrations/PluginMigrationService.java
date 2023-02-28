package us.crazycrew.crazyauctions.configs.migrations;

import ch.jalu.configme.configurationdata.ConfigurationData;
import ch.jalu.configme.migration.PlainMigrationService;
import ch.jalu.configme.resource.PropertyReader;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycore.CrazyCore;
import us.crazycrew.crazycore.CrazyLogger;
import java.nio.file.Path;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Date: 2/28/2023
 * Time: 1:26 AM
 *
 * Description: Migrate old values to new values.
 */
public class PluginMigrationService extends PlainMigrationService {

    @Override
    protected boolean performMigrations(PropertyReader reader, ConfigurationData configurationData) {
        return convert(reader, "example.test", "config.yml", true)
                | convert(reader, "", "", true);
    }

    private boolean convert(PropertyReader reader, String oldValue, String newFile, boolean cascade) {
        if (reader.contains(oldValue)) {
            Path nFile = CrazyCore.api().getDirectory().resolve(newFile);

            YamlConfiguration yamlNewFile = YamlConfiguration.loadConfiguration(nFile.toFile());

            CrazyLogger.info("Starting the config migration process...");
            CrazyLogger.info("Found old config value (" + oldValue + ")");

            if (!nFile.toFile().exists()) {
                try {
                    //noinspection ResultOfMethodCallIgnored
                    nFile.toFile().createNewFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            for (String child : reader.getChildKeys(oldValue)) {
                if (cascade) {
                    for (String doubleChild : reader.getChildKeys(child)) {
                        yamlNewFile.set(doubleChild, reader.getObject(doubleChild));
                    }
                } else {
                    yamlNewFile.set(child, reader.getObject(child));
                }
            }

            try {
                yamlNewFile.save(nFile.toFile());

                CrazyLogger.info("The migration process is complete!");
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return true;
        }

        return false;
    }
}