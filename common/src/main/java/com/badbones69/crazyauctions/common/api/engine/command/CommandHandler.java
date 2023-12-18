package com.badbones69.crazyauctions.common.api.engine.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.meta.CommandMeta;
import com.ryderbelserion.cluster.utils.AdvUtils;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public abstract class CommandHandler {

    @NotNull
    public abstract CommandManager<Audience> getManager();

    @NotNull
    public abstract Command.Builder<Audience> getRoot();

    //TODO() Add exception handler.
    //TODO() Add register command method
    //TODO() Add a method to get the root command.
    //TODO() Add a way to expose registered commands, args etc for the help generator.

    private Command.Builder<Audience> root() {
        return getManager().commandBuilder("crazyauctions", "ca", "ah")
                .permission("crazyauctions.access")
                .meta(CommandMeta.DESCRIPTION, "CrazyAuctions command. /crazyauctions help")
                .handler(context -> {
                   context.getSender().sendMessage(AdvUtils.parse("<red>This is the base command."));

                   //TODO() Add some type of wrapper to open gui's
                });
    }

    protected void register() {

    }
}