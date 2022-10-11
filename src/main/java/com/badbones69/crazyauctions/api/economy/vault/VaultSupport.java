package com.badbones69.crazyauctions.api.economy.vault;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.utils.func.PluginSupport;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultSupport {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();
    
    private Economy vault = null;
    
    public Economy getVault() {
        return vault;
    }
    
    public void loadVault() {
        if (PluginSupport.VAULT.isPluginLoaded()) {
            RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);

            if (rsp != null) vault = rsp.getProvider();
        }
    }
}