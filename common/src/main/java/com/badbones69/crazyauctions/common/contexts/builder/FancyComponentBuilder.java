package com.badbones69.crazyauctions.common.contexts.builder;

import com.ryderbelserion.cluster.utils.AdvUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

public class FancyComponentBuilder {

    private final TextComponent.@NotNull Builder builder = Component.text();

    private Component parse(String value) {
        return AdvUtils.parse(value);
    }

    public FancyComponentBuilder append(Component component) {
        this.builder.append(component);

        return this;
    }

    public FancyComponentBuilder hover(String text, String hover) {
        this.builder.append(parse(text)).hoverEvent(HoverEvent.showText(parse(hover)));

        return this;
    }

    public FancyComponentBuilder hover(String text) {
        this.builder.hoverEvent(HoverEvent.showText(parse(text)));

        return this;
    }

    public FancyComponentBuilder click(ClickEvent.Action action, String text) {
        this.builder.clickEvent(ClickEvent.clickEvent(action, text));

        return this;
    }

    public Component fancy() {
        return this.builder.build();
    }
}