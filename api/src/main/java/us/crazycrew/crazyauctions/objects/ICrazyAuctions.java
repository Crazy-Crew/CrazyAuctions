package us.crazycrew.crazyauctions.objects;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public interface ICrazyAuctions {

    ComponentLogger getLogger();

    void reload();

}