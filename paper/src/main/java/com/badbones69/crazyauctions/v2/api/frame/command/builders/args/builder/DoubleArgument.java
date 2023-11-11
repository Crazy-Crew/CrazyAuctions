package com.badbones69.crazyauctions.api.frame.command.builders.args.builder;

import com.badbones69.crazyauctions.api.frame.command.builders.args.ArgumentType;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DoubleArgument extends ArgumentType {

    private final int numberCap;

    public DoubleArgument(Integer numberCap) {
        if (numberCap == null) {
            this.numberCap = 100;
            return;
        }

        this.numberCap = numberCap;
    }

    @Override
    public List<String> getPossibleValues() {
        List<String> numbers = new ArrayList<>();

        DecimalFormat decimalFormat = new DecimalFormat("0.0");

        for (double value = 0.1; value <= this.numberCap; value += 0.1) {
            String formattedNumber = decimalFormat.format(value);
            numbers.add(formattedNumber);
        }

        return numbers;
    }
}