package us.crazycrew.crazyauctions.data;

import java.util.UUID;

public interface ProfileCache {

    void load();

    void save();

    void add(final UUID uuid);

    String getName(final UUID uuid);

}