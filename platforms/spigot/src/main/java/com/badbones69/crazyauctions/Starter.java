package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.economy.vault.VaultSupport;
import com.badbones69.crazyauctions.api.enums.PluginSupport;

public class Starter {

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();
    private CrazyManager crazyManager = new CrazyManager();

    private VaultSupport vaultSupport;

    public void init() {
        if (getVaultSupport() != null && PluginSupport.SupportedPlugins.VAULT.isPluginLoaded()) plugin.getLogger().warning("Vault support is now enabled.");
        crazyManager = new CrazyManager();
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
    }

    public Starter setVaultSupport(VaultSupport vaultSupport) {
        this.vaultSupport = vaultSupport;

        this.vaultSupport.loadVault();

        return this;
    }

    public VaultSupport getVaultSupport() {
        return this.vaultSupport;
    }

}