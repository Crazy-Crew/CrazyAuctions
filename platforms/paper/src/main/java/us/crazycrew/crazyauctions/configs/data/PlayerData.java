package us.crazycrew.crazyauctions.configs.data;

import com.google.gson.annotations.Expose;
import us.crazycrew.crazyauctions.api.interfaces.Universal;
import us.crazycrew.crazycore.files.FileExtension;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData extends FileExtension implements Universal {

    @Expose
    public static ConcurrentHashMap<UUID, String> auctions = new ConcurrentHashMap<>();

    public PlayerData(UUID uuid) {
        super(uuid + ".json", plugin.getUsers());
    }

    public static void load(UUID uuid) {
        plugin.getCrazyCore().getFileHandler().addFile(new PlayerData(uuid));
    }

    public static void save(UUID uuid) {
        plugin.getCrazyCore().getFileHandler().saveFile(new PlayerData(uuid));
    }
}