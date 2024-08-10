package com.badbones69.crazyauctions.configuration.enums;

public enum FileEnum {

    config("config.yml");

    private final String name;

    FileEnum(final String name) {
        this.name = name;
    }

    public final String getName() {
        return this.name;
    }
}