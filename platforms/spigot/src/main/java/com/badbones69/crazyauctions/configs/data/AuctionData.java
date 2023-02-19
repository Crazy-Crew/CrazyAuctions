package com.badbones69.crazyauctions.configs.data;

import com.badbones69.crazyauctions.api.interfaces.Universal;
import com.google.gson.annotations.Expose;
import net.dehya.ruby.common.annotations.FileBuilder;
import net.dehya.ruby.common.enums.FileType;
import net.dehya.ruby.files.FileExtension;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@FileBuilder(isLogging = true, isAsync = false, isData = false, fileType = FileType.JSON)
public class AuctionData extends FileExtension implements Universal {

    @Expose
    public static ConcurrentHashMap<UUID, String> auctionData = new ConcurrentHashMap<>();

    public AuctionData() {
        super("auction-data.json");
    }

    public static void load() {
        plugin.getSpigotFileManager().addFile(new AuctionData());
    }

    public static void save() {
        plugin.getSpigotFileManager().saveFile(new AuctionData());
    }
}