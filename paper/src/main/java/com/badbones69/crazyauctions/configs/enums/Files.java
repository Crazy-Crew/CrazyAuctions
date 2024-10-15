package com.badbones69.crazyauctions.configs.enums;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.jetbrains.annotations.Nullable;
import java.io.File;

public enum Files {

    config("config-v2.yml"),
    //locale("messages.yml"),

    auctions("guis", "auctions.yml");

    private final CrazyAuctions plugin = CrazyAuctions.getPlugin();

    private final String fileName;

    private final String directory;

    private final File file;

    Files(final String directory, final String fileName) {
        this.directory = directory;
        this.fileName = fileName;

        this.file = new File(new File(this.plugin.getDataFolder(), this.directory), this.fileName);
    }

    Files(final String fileName) {
        this.fileName = fileName;
        this.directory = null;

        this.file = new File(this.plugin.getDataFolder(), this.fileName);
    }

    public @Nullable final String getDirectory() {
        return this.directory;
    }

    public final String getFileName() {
        return this.fileName;
    }

    public final File getFile() {
        return this.file;
    }
}