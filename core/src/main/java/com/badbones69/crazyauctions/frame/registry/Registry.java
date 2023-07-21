package com.badbones69.crazyauctions.frame.registry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * https://github.com/BillyGalbreath/Pl3xMap/blob/v3/LICENSE
 *
 * @author BillyGalbreath
 */
public class Registry<T extends RegistryKey> implements Iterable<@NotNull T> {

    protected final Map<@NotNull String, @NotNull T> entries = new ConcurrentHashMap<>();

    public @NotNull T register(@NotNull T value) {
        return register(value.getKey(), value);
    }

    public @NotNull T register(@NotNull String id, @NotNull T value) {
        this.entries.put(id, value);
        return value;
    }

    public void unregister(@NotNull String id) {
        this.entries.remove(id);
    }

    public void unregister() {
        Collections.unmodifiableSet(this.entries.keySet()).forEach(this::unregister);
    }

    public boolean has(@NotNull String key) {
        return this.entries.containsKey(key);
    }

    public @Nullable T get(@NotNull String id) {
        return this.entries.get(id);
    }

    public @NotNull T getOrDefault(@NotNull String id, @NotNull T def) {
        return this.entries.getOrDefault(id, def);
    }

    public @NotNull Set<Map.@NotNull Entry<@NotNull String, @NotNull T>> entrySet() {
        return this.entries.entrySet();
    }

    public @NotNull Collection<@NotNull T> values() {
        return this.entries.values();
    }

    public int size() {
        return this.entries.size();
    }

    @Override
    public @NotNull Iterator<@NotNull T> iterator() {
        return this.entries.values().iterator();
    }
}