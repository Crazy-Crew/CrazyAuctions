package com.badbones69.crazyauctions.api.objects.effects;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SoundEffect {

    private final boolean isDisabled;

    private Sound sound;

    /**
     * Builds a sound to play.
     *
     * @param value name of the sound
     * @param source category of sound as to respect the client side sound settings
     * @param volume volume of the sound
     * @param pitch pitch of the sound
     */
    public SoundEffect(@NotNull final String value, @NotNull final Sound.Source source, final double volume, final double pitch) {
        this.isDisabled = value.isEmpty();

        if (!this.isDisabled) {
            //noinspection PatternValidation
            this.sound = Sound.sound(Key.key(value), source, (float) volume, (float) pitch);
        }
    }

    /**
     * Play a sound for a player at a location.
     *
     * @param player player to play sound to.
     * @param location location for sound to play at.
     */
    public void play(@NotNull final Player player, @NotNull final Location location) {
        if (this.isDisabled || this.sound == null) return;

        player.playSound(this.sound, location.x(), location.y(), location.z());
    }
}