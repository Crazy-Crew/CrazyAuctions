package com.badbones69.crazyauctions.utils;

import com.badbones69.crazyauctions.api.objects.effects.SoundEffect;
import com.badbones69.crazyauctions.configs.beans.SoundProperty;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MiscUtils {

    public static void play(final Player player, final Location location, final String sound, double volume, double pitch, Sound.Source source) {
        // don't play the sound, if it is empty.
        if (sound.isEmpty()) return;

        new SoundEffect(sound, source, volume, pitch).play(player, location);
    }

    public static void play(final Player player, final Location location, final SoundProperty property, final Sound.Source source) {
        play(player, location, property.value, property.volume, property.pitch, source);
    }

    public static void play(final Player player, final String sound, double volume, double pitch, Sound.Source source) {
        play(player, player.getLocation(), sound, volume, pitch, source);
    }
}