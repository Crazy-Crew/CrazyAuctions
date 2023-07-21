package com.badbones69.crazyauctions.api.frame.command.builders.args.builder;

import com.badbones69.crazyauctions.api.frame.command.builders.args.ArgumentType;
import java.util.ArrayList;
import java.util.List;

public class IntArgument extends ArgumentType {

    private final int numberCap;

    public IntArgument(Integer numberCap) {
        if (numberCap == null) {
            this.numberCap = 100;
            return;
        }

        this.numberCap = numberCap;
    }

    @Override
    public List<String> getPossibleValues() {
        List<String> numbers = new ArrayList<>();

        for (int value = 1; value <= this.numberCap; value += 1) numbers.add(String.valueOf(value));

        return numbers;
    }
}