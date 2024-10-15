package com.badbones69.crazyauctions.configs.beans;

import ch.jalu.configme.Comment;

public class SoundProperty {

    @Comment("The sound to play when an item is sold, Set to '' to disable it.")
    public String value;

    @Comment("The volume of the sound to play.")
    public double volume;

    @Comment("The direction of the sound to play.")
    public double pitch;

    public final SoundProperty populate() {
        this.value = "ui.button.click";
        this.volume = 1.0;
        this.pitch = 1.0;

        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public String getValue() {
        return this.value;
    }

    public double getVolume() {
        return this.volume;
    }

    public double getPitch() {
        return this.pitch;
    }
}