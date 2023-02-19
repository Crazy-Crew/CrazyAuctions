package com.badbones69.crazyauctions.configs;

import com.badbones69.crazyauctions.CrazyAuctions;
import net.dehya.ruby.common.annotations.FileBuilder;
import net.dehya.ruby.common.annotations.yaml.BlockType;
import net.dehya.ruby.common.annotations.yaml.Comment;
import net.dehya.ruby.common.annotations.yaml.Key;
import net.dehya.ruby.common.enums.FileType;
import net.dehya.ruby.files.FileExtension;

@FileBuilder(isLogging = true, isAsync = true, isData = false, fileType = FileType.YAML)
/*@Header("""
            Discord: https://discord.gg/crazycrew
            Github: https://github.com/Crazy-Crew
            
            Report Issues: https://github.com/Crazy-Crew/CrazyAuctions/issues
            Request Features/Support: https://github.com/orgs/Crazy-Crew/discussions
            """)*/
public class Config extends FileExtension {

    @Key("settings.prefix")
    @Comment("The prefix used in front of messages.")
    public static String PREFIX = "&8[&bCrazyAuctions&8]: ";

    @Key("settings.locale-file")
    @Comment("The language file to use from the locale folder. Supported languages are English(en).")
    @BlockType
    public static String LOCALE_FILE = "locale-en.yml";

    @Key("settings.update-checker")
    @Comment("Whether you want to be notified when an update is published to Modrinth.")
    public static boolean UPDATE_CHECKER = true;

    @Key("settings.toggle-metrics")
    @Comment("Whether you want your server statistics to be sent to https://bstats.org/ ( Requires a restart! )")
    public static boolean TOGGLE_METRICS = true;

    @Key("settings.config-version")
    @Comment("DO NOT TOUCH THIS: We use this to identify if your configs are outdated.")
    public static int CONFIG_VERSION = 1;

    public Config() {
        super("config.yml");
    }

    public static void reload(CrazyAuctions plugin) {
        plugin.getPaperManager().getPaperFileManager().addFile(new Config());
    }
}