package com.badbones69.crazyauctions.api.frame.command.builders.args.builder;

import com.badbones69.crazyauctions.api.frame.command.builders.args.ArgumentType;
import java.util.List;

public class BooleanArgument extends ArgumentType {

    @Override
    public List<String> getPossibleValues() {
        return List.of("true", "false");
    }
}