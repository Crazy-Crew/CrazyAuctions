package com.badbones69.crazyauctions.commands;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.enums.Category;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.api.enums.misc.Files;
import com.badbones69.crazyauctions.controllers.GuiListener;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.files.FileManager;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.nio.file.Path;

@Command(value = "crazyauctions", alias = {"ah", "ca"})
public abstract class BaseCommand {

    protected @NotNull final CrazyAuctions plugin = CrazyAuctions.get();

    protected @NotNull final Path dataPath = this.plugin.getDataPath();

    protected @NotNull final FileManager fileManager = this.plugin.getFileManager();

    protected @NotNull final CrazyManager crazyManager = this.plugin.getCrazyManager();

    protected @NotNull final FusionPaper fusion = this.plugin.getFusion();

    protected void view(Player player) {
        final FileConfiguration configuration = Files.config.getConfiguration();

        if (configuration.getBoolean("Settings.Category-Page-Opens-First", false)) {
            GuiListener.openCategories(player, ShopType.SELL);

            return;
        }

        ShopType shopType = ShopType.NONE;

        if (this.crazyManager.isSellingEnabled()) {
            shopType = ShopType.SELL;
        } else if (this.crazyManager.isBiddingEnabled()) {
            shopType = ShopType.BID;
        }

        if (shopType == ShopType.NONE) {
            //todo() add better message

            return;
        }

        GuiListener.openShop(player, shopType, Category.NONE, 1);
    }
}