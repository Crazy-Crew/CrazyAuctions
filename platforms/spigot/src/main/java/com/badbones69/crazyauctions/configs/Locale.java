package com.badbones69.crazyauctions.configs;

import com.badbones69.crazyauctions.CrazyAuctions;
import net.dehya.ruby.common.annotations.FileBuilder;
import net.dehya.ruby.common.enums.FileType;
import net.dehya.ruby.files.FileExtension;
import java.nio.file.Path;

@FileBuilder(isLogging = true, isAsync = false, isData = false, fileType = FileType.YAML)
public class Locale extends FileExtension {

    public Locale(Path path) {
        super(Config.LOCALE_FILE, path.resolve("locale"));
    }

    public static void reload(CrazyAuctions plugin) {
        plugin.getSpigotFileManager().extract("/locale", plugin.getDirectory());

        plugin.getSpigotFileManager().addFile(new Locale(plugin.getDirectory()));
    }
}