package com.badbones69.crazyauctions.storage.data;

import com.badbones69.crazyauctions.frame.storage.FileExtension;
import com.badbones69.crazyauctions.frame.storage.enums.StorageType;
import com.google.gson.annotations.Expose;
import com.badbones69.crazyauctions.api.interfaces.Universal;
import java.io.File;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData extends FileExtension implements Universal {

    @Expose
    public static ConcurrentHashMap<UUID, String> auctions = new ConcurrentHashMap<>();

    public PlayerData(UUID uuid) {
        super("hi", new File("").toPath(), StorageType.YAML);
        //super(uuid + ".json", plugin.getUsers(), FileType.YAML);
    }

    public static void load(UUID uuid) {
        //plugin.getCrazyCore().getFileHandler().addFile(new PlayerData(uuid));
    }

    public static void save(UUID uuid) {
        //plugin.getCrazyCore().getFileHandler().saveFile(new PlayerData(uuid));
    }
}