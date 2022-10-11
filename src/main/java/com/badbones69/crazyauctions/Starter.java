package com.badbones69.crazyauctions;

import com.badbones69.crazyauctions.api.CrazyManager;
import com.badbones69.crazyauctions.api.FileManager;
import com.badbones69.crazyauctions.api.economy.vault.VaultSupport;
import com.badbones69.crazyauctions.utils.SkullCreator;

public class Starter {

    private FileManager fileManager;

    private CrazyManager crazyManager;

    private VaultSupport vaultSupport;

    private Methods methods;

    private SkullCreator skullCreator;

    public void run() {
        fileManager = new FileManager();
        crazyManager = new CrazyManager();

        vaultSupport = new VaultSupport();

        methods = new Methods();

        skullCreator = new SkullCreator();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public CrazyManager getCrazyManager() {
        return crazyManager;
    }

    public VaultSupport getVaultSupport() {
        return vaultSupport;
    }

    public Methods getMethods() {
        return methods;
    }

    public SkullCreator getSkullCreator() {
        return skullCreator;
    }
}