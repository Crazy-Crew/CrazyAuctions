package com.badbones69.crazyauctions.commands.v2;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.UserManager;
import com.badbones69.crazyauctions.configs.ConfigManager;
import com.badbones69.crazyauctions.configs.enums.Files;
import com.ryderbelserion.vital.paper.commands.PaperCommand;
import org.bukkit.Server;

public abstract class AbstractCommand extends PaperCommand {

    protected final CrazyAuctions plugin = CrazyAuctions.getPlugin();
    protected final CrazyManager crazyManager = this.plugin.getCrazyManager();
    protected final UserManager userManager = this.plugin.getUserManager();
    protected final Server server = this.plugin.getServer();

    protected final SettingsManager config = ConfigManager.getConfig();

    protected final SettingsManager auctions = ConfigManager.getCustomConfig(Files.auctions.getFileName());

}