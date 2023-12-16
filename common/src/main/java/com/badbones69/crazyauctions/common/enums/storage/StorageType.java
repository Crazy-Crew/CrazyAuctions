package com.badbones69.crazyauctions.common.enums.storage;

import java.util.List;

public enum StorageType {

    H2("H2", "h2"),
    YAML("YAML", "yml"),
    MARIADB("MariaDB", "mariadb");

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