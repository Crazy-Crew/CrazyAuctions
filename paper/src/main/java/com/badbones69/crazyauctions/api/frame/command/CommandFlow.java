package com.badbones69.crazyauctions.api.frame.command;

import com.badbones69.crazyauctions.api.frame.command.builders.CommandActor;
import com.badbones69.crazyauctions.api.frame.command.builders.CommandDataEntry;
import com.badbones69.crazyauctions.api.frame.command.builders.CommandHelpEntry;
import java.util.List;
import java.util.Map;

public interface CommandFlow {

    void addCommand(CommandEngine engine);

    boolean hasCommand(String label);

    CommandHelpEntry generateCommandHelp(CommandActor actor);

    int defaultHelpPerPage();

    void updateHelpPerPage(int newAmount);

    CommandDataEntry getCommand(String label);

    void removeCommand(String label);

    Map<String, CommandDataEntry> getCommands();

    List<CommandEngine> getClasses();

    List<String> handleTabComplete(String[] args, CommandEngine engine);
}