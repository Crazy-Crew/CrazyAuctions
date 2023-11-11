package com.badbones69.crazyauctions.api.frame.command.builders.args.builder.custom;

import com.badbones69.crazyauctions.api.frame.command.builders.args.ArgumentType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerArgument extends ArgumentType {

    @Override
    public List<String> getPossibleValues() {
        return Bukkit.getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }
}