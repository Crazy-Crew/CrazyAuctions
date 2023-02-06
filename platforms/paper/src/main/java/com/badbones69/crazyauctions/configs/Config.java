package com.badbones69.crazyauctions.configs;

import com.badbones69.crazyauctions.CrazyAuctions;
import net.dehya.ruby.common.annotations.FileBuilder;
import net.dehya.ruby.common.annotations.yaml.Comment;
import net.dehya.ruby.common.annotations.yaml.Header;
import net.dehya.ruby.common.annotations.yaml.Key;
import net.dehya.ruby.common.enums.FileType;
import net.dehya.ruby.files.FileExtension;

@FileBuilder(isLogging = true, isAsync = true, isData = false, fileType = FileType.YAML)
@Header("""
            Discord: https://discord.gg/crazycrew
            Github: https://github.com/Crazy-Crew
            
            Report Issues: https://github.com/Crazy-Crew/CrazyAuctions/issues
            Request Features/Support: https://github.com/orgs/Crazy-Crew/discussions
            """)
public class Config extends FileExtension {

    @Key("settings.toggle-metrics")
    @Comment("Whether metrics are sent to https://bstats.org or not.")
    public static boolean TOGGLE_METRICS = true;

    public Config() {
        super("config.yml");
    }

    public static void reload(CrazyAuctions plugin) {
        plugin.getPaperManager().getPaperFileManager().addFile(new Config());
    }
}