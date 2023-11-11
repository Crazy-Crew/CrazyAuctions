package com.badbones69.crazyauctions.api.frame.command.builders;

public class CommandDataEntry {

    private boolean isHidden = false;

    public void setHidden(boolean value) {
        this.isHidden = value;
    }

    public boolean isHidden() {
        return this.isHidden;
    }
}