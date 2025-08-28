package com.badbones69.crazyauctions.v2.api.gui.frame.interfaces;

import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface IGuiAction<T extends Event> {

    void execute(@NotNull final T event);

}
