package com.badbones69.crazyauctions.storage.interfaces;

import com.badbones69.crazyauctions.frame.storage.enums.StorageType;
import com.badbones69.crazyauctions.storage.objects.UserData;
import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

public interface UserManager {

    void load(UUID uuid);

    void saveSingular(UUID uuid, boolean serverExit);

    void save(boolean serverExit);

    void convert(File file, UUID uuid, StorageType storageType);

    void addAuction(UUID uuid);

    File getFile(Path path, UUID uuid);

    UserData getUser(UUID uuid);

    Map<UUID, UserData> getUsers();

}