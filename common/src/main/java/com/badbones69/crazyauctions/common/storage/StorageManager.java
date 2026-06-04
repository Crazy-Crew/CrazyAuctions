package com.badbones69.crazyauctions.common.storage;

import com.badbones69.crazyauctions.common.CrazyPlugin;
import com.badbones69.crazyauctions.common.enums.FileKey;
import com.badbones69.crazyauctions.common.storage.impl.types.file.YamlFactory;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.PaperFileManager;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.api.storage.IStorageHolder;
import java.nio.file.Path;

public class StorageManager {

    private final PaperFileManager fileManager;
    private final FusionPaper fusion;
    private final Path dataPath;

    public StorageManager(@NonNull final CrazyPlugin plugin) {
        this.dataPath = plugin.getDataPath();
        this.fusion = plugin.getFusion();

        this.fileManager = this.fusion.getFileManager();
    }

    public IStorageHolder init() {
        final CommentedConfigurationNode configuration = FileKey.database.getYamlConfig();

        final String type = configuration.node("database", "type").getString("YAML").toLowerCase();

        return switch (type) {
            //case "sqlite" -> new ConnectionStorage(new SqliteFactory(this.dataPath.resolve("crazyenvoys.db")), this.fusion).init();

            case "sqlite" -> { // temporarily only enabled yaml for now
                this.fileManager.addPaperFile(this.dataPath.resolve("data.yml"));

                yield new YamlFactory(FileKey.data.getConfiguration()).init();
            }

            case "yaml" -> {
                this.fileManager.addPaperFile(this.dataPath.resolve("data.yml"));

                yield new YamlFactory(FileKey.data.getConfiguration()).init();
            }

            default -> throw new FusionException("Unknown Database Type: %s".formatted(type));
        };
    }
}