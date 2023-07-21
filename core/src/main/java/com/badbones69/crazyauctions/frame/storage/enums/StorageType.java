package com.badbones69.crazyauctions.frame.storage.enums;

import java.util.List;

public enum StorageType {

    // Config style databases
    JSON("JSON", "json"),
    YAML("YAML", "yaml"),

    // Local databases
    SQLITE("SQLite", "sqlite");

    private final String name;

    private final List<String> identifiers;

    StorageType(String name, String... identifiers) {
        this.name = name;
        this.identifiers = List.of(identifiers);
    }

    public String getName() {
        return this.name;
    }

    public List<String> getIdentifiers() {
        return this.identifiers;
    }
}