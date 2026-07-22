package com.badbones69.crazyauctions.common.storage;

import com.badbones69.crazyauctions.common.CrazyAuctionsPlugin;
import com.badbones69.crazyauctions.common.enums.keys.FileKeys;
import com.badbones69.crazyauctions.common.storage.holder.StorageHolder;
import com.badbones69.crazyauctions.common.storage.impl.file.types.YamlFactory;
import org.bukkit.configuration.file.YamlConfiguration;

public class StorageManager {

    private final CrazyAuctionsPlugin plugin;

    public StorageManager(final CrazyAuctionsPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    public StorageHolder init() {
        final YamlConfiguration configuration = FileKeys.config.getConfiguration();

        final String storageType = configuration.getString("database.type", "YAML").toLowerCase();

        return switch (storageType.toLowerCase()) {
            case "yaml" -> new StorageHolder(new YamlFactory(this.plugin)).init();
            case "sqlite" -> new StorageHolder(new YamlFactory(this.plugin)).init();
            default -> throw new IllegalStateException("Unexpected storage type value: " + storageType);
        };
    }
}