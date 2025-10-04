package us.crazycrew.crazyauctions.exceptions;

import org.jetbrains.annotations.NotNull;

public class AuctionsException extends IllegalStateException {

    public AuctionsException(@NotNull final String message, @NotNull final Exception exception) {
        super(message, exception);
    }

    public AuctionsException(@NotNull final String message) {
        super(message);
    }
}