package com.badbones69.crazyauctions.api.builders.gui;

import com.ryderbelserion.vital.paper.api.builders.gui.interfaces.Gui;
import org.bukkit.entity.Player;

public abstract class StaticInventoryBuilder extends InventoryBuilder {

    private final Player player;

    private final Gui gui;

    /**
     * Builds an inventory with a set title/rows
     *
     * @param player {@link Player}
     * @param title {@link String}
     * @param rows {@link Integer}
     */
    public StaticInventoryBuilder(final Player player, final String title, final int rows) {
        super(player);

        this.gui = Gui.gui().setTitle(parse(player, title)).setRows(rows).disableInteractions().create();

        this.player = player;
    }

    public abstract void open();

    /**
     * Gets the {@link Player}.
     *
     * @return {@link Player}
     */
    public final Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the title of the gui.
     *
     * @return the title of the gui
     */
    public final String getTitle() {
        return this.gui.getTitle();
    }

    /**
     * Checks if the title contains a message.
     *
     * @param message the message to check
     * @return true or false
     */
    public final boolean contains(final String message) {
        return getTitle().contains(message);
    }

    /**
     * Gets the {@link Gui}.
     *
     * @return {@link Gui}
     */
    public final Gui getGui() {
        return this.gui;
    }
}