package com.badbones69.crazyauctions.v2.api;

import com.badbones69.crazyauctions.v2.CrazyAuctionsPlus;
import com.badbones69.crazyauctions.v2.api.items.AuctionItem;
import com.ryderbelserion.fusion.core.files.types.YamlCustomFile;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.FileManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CrazyManager {

    private final CrazyAuctionsPlus plugin = JavaPlugin.getPlugin(CrazyAuctionsPlus.class);

    private final FileManager fileManager = this.plugin.getFileManager();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Path path = this.plugin.getDataPath();

    private final Map<String, AuctionItem> items = new HashMap<>();

    public void init() {
        final YamlCustomFile configuration = this.fileManager.getConfigurateFile(this.path.resolve("v2-config.yml"));

        if (configuration != null) {
            final CommentedConfigurationNode configNode = configuration.getConfiguration();

            final Map<Object, CommentedConfigurationNode> buttons = configNode.node("buttons").childrenMap();

            buttons.forEach((object, child) -> {
                final String name = object.toString();

                this.items.put(name, new AuctionItem(child));
            });
        }
    }

    public Map<String, AuctionItem> getItems() {
        return this.items;
    }
}