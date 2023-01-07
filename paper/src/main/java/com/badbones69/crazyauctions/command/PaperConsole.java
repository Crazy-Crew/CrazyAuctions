package com.badbones69.crazyauctions.command;

import com.ryderbelserion.ithildin.core.Console;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

public class PaperConsole extends Console {

    private final ConsoleCommandSender consoleCommandSender;

    public PaperConsole() {
        this.consoleCommandSender = Bukkit.getConsoleSender();
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        this.consoleCommandSender.sendMessage(source, message, type);
    }

    @Override
    public boolean equals(@Nullable Object instance) {
        if (this == instance) return true;
        if (instance == null) return false;
        if (this.getClass() != instance.getClass()) return false;

        PaperConsole other = (PaperConsole) instance;
        return getKey() == other.getKey() && this.consoleCommandSender == other.consoleCommandSender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), this.consoleCommandSender);
    }

    @Override
    public String toString() {
        return "PaperConsole{" + "key=" + getKey() + ",console=" + this.consoleCommandSender.getName() + "}";
    }
}