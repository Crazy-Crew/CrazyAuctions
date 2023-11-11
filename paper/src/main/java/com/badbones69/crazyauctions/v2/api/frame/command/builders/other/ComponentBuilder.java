package com.badbones69.crazyauctions.api.frame.command.builders.other;

import com.badbones69.crazyauctions.frame.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public class ComponentBuilder {

    private String message;

    private final TextComponent.@NotNull Builder builder = Component.text();

    private final PreciseComponentBuilder preciseBuilder;

    public ComponentBuilder() {
        this.preciseBuilder = new PreciseComponentBuilder();
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ComponentBuilder append(Component component) {
        this.builder.append(component);

        return this;
    }

    public Component parse(String value) {
        return AdventureUtils.parse(value);
    }

    public ComponentBuilder hover(String hoverText) {
        this.builder.hoverEvent(HoverEvent.showText(parse(hoverText)));

        return this;
    }

    public ComponentBuilder click(String clickText, ClickEvent.Action action) {
        this.builder.clickEvent(ClickEvent.clickEvent(action, clickText));

        return this;
    }

    public class PreciseComponentBuilder {
        private final TextComponent.@NotNull Builder preciseBuilder = Component.text();

        public PreciseComponentBuilder text(String text) {
            this.preciseBuilder.append(parse(text));

            return this;
        }

        public PreciseComponentBuilder hoverText(String text, String hoverText) {
            this.preciseBuilder.append(parse(text));
            this.preciseBuilder.hoverEvent(HoverEvent.showText(parse(hoverText)));

            return this;
        }

        public PreciseComponentBuilder hoverText(String hoverText) {
            this.preciseBuilder.hoverEvent(HoverEvent.showText(parse(hoverText)));

            return this;
        }

        public PreciseComponentBuilder click(String clickText, ClickEvent.Action action) {
            this.preciseBuilder.clickEvent(ClickEvent.clickEvent(action, clickText));

            return this;
        }

        public @NotNull TextComponent getPreciseBuilder() {
            return this.preciseBuilder.build();
        }
    }

    public PreciseComponentBuilder getPreciseComponent() {
        return this.preciseBuilder;
    }

    public @NotNull TextComponent build() {
        Component message = AdventureUtils.parse(this.message);

        if (getPreciseComponent() != null) {
            return this.builder.append(message).append(getPreciseComponent().getPreciseBuilder()).build();
        }

        return this.builder.append(message).build();
    }
}