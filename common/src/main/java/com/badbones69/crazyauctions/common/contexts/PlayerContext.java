package com.badbones69.crazyauctions.common.contexts;

import com.ryderbelserion.cluster.utils.AdvUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

public class PlayerContext {

    private final Audience audience;

    public PlayerContext(Audience audience) {
        this.audience = audience;
    }

    public void reply(String message) {
        if (empty(message)) return;

        Component component = AdvUtils.parse(message);

        this.audience.sendMessage(component);
    }

    public void reply(Component message) {
        if (message == null) return;

        this.audience.sendMessage(message);
    }

    public void send(Component component) {
        if (component == null) return;

        this.audience.sendMessage(component);
    }

    public void send(Audience audience, String message) {
        if (empty(message)) return;

        Component component = AdvUtils.parse(message);

        audience.sendMessage(component);
    }

    public void send(Audience audience, Component component) {
        if (component == null) return;

        audience.sendMessage(component);
    }

    private boolean empty(String message) {
        return message.isBlank() || message.isEmpty();
    }

    public Audience getSender() {
        return this.audience;
    }
}