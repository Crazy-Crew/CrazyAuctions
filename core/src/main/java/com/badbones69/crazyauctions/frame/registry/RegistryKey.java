package com.badbones69.crazyauctions.frame.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * https://github.com/BillyGalbreath/Pl3xMap/blob/v3/LICENSE
 *
 * @author BillyGalbreath
 */
public abstract class RegistryKey {

    private final String key;

    public RegistryKey(@NotNull String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return this.key;
    }

    @Override
    public boolean equals(@Nullable Object instance) {
        if (this == instance) return true;
        if (instance == null) return false;

        if (this.getClass() != instance.getClass()) return false;

        RegistryKey other = (RegistryKey) instance;
        return getKey().equals(other.getKey());
    };

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

    @Override
    public @NotNull String toString() {
        return this.key;
    }
}