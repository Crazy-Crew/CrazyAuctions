package us.crazycrew.crazyauctions.api.database;

import com.badbones69.crazyauctions.common.AuctionsFactory;
import com.badbones69.crazyauctions.common.config.types.ConfigKeys;
import com.badbones69.crazyauctions.common.enums.StorageType;
import us.crazycrew.crazyauctions.CrazyAuctions;
import us.crazycrew.crazyauctions.api.database.impl.StorageImpl;
import us.crazycrew.crazyauctions.api.database.impl.sql.SqlStorage;
import us.crazycrew.crazyauctions.api.database.impl.sql.file.types.SqliteConnection;
import java.io.File;

public class StorageFactory {

    public Storage getInstance() {
        Storage storage;

        storage = new Storage(create(AuctionsFactory.getConfig().getProperty(ConfigKeys.storage_type)));

        storage.init();

        return storage;
    }

    private StorageImpl create(StorageType type) {
        switch (type) {
            case SQLITE -> {
                return new SqlStorage(new SqliteConnection(new File(CrazyAuctions.get().getDataFolder(), "users.db")));
            }

            case MARIADB -> {

            }

            default -> throw new RuntimeException("This method is not known: " + type);
        }

        return null;
    }
}