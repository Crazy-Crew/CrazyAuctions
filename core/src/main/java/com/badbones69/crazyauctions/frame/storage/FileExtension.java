package com.badbones69.crazyauctions.frame.storage;

import com.badbones69.crazyauctions.frame.storage.enums.StorageType;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.nio.file.Path;

public abstract class FileExtension {

    private final String name;
    private final Path path;
    private final StorageType type;

    private GsonBuilder builder;

    public FileExtension(String name, Path path, StorageType type) {
        this.path = path;

        this.name = name;

        this.type = type;
    }

    public void setGsonBuilder(GsonBuilder builder) {
        if (type != StorageType.JSON) return;

        this.builder = builder;
    }

    public GsonBuilder getBuilder() {
        return this.builder;
    }

    public StorageType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public File getFile() {
        return new File(this.path.toFile(), this.name);
    }

    public Path getPath() {
        return this.path;
    }
}