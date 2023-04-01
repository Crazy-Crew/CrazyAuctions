package us.crazycrew.crazyauctions.storage.data;

import com.google.gson.annotations.Expose;
import us.crazycrew.crazyauctions.api.interfaces.Universal;
import us.crazycrew.crazycore.files.FileExtension;
import us.crazycrew.crazycore.files.enums.FileType;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author RyderBelserion
 * @author BadBones69
 *
 * Date: 2/28/2023
 * Time: 2:28 AM
 * Last Edited: 2/28/2023 @ 3:04 AM
 *
 * Description: The default data type for CrazyAuctions.
 */
public class PlayerData extends FileExtension implements Universal {

    @Expose
    public static ConcurrentHashMap<UUID, String> auctions = new ConcurrentHashMap<>();

    public PlayerData(UUID uuid) {
        super(uuid + ".json", plugin.getUsers(), FileType.YAML);
    }

    public static void load(UUID uuid) {
        plugin.getCrazyCore().getFileHandler().addFile(new PlayerData(uuid));
    }

    public static void save(UUID uuid) {
        plugin.getCrazyCore().getFileHandler().saveFile(new PlayerData(uuid));
    }
}