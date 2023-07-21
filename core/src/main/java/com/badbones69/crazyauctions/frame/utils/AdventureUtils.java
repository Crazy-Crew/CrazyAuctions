package com.badbones69.crazyauctions.frame.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

public class AdventureUtils {

    public static Component parse(@NotNull String message, @NotNull TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(message, placeholders).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE);
    }

    public static void send(Audience audience, String component, boolean isEnabled, String prefix) {
        audience.sendMessage(isEnabled ? parse(prefix).append(parse(component)) : parse(component));
    }

    public static void hover(Audience audience, String message, String text, String value, ClickEvent.Action action) {
        Component textComponent = parse(message)
                .hoverEvent(HoverEvent.showText(parse(text)))
                .clickEvent(ClickEvent.clickEvent(action, value));

        audience.sendMessage(textComponent);
    }

    public static void hover(Audience audience, String message, String text, String button, String value, ClickEvent.Action action) {
        Component textComponent = parse(message)
                .append(parse(button).hoverEvent(HoverEvent.showText(parse(text))))
                .clickEvent(ClickEvent.clickEvent(action, value));

        audience.sendMessage(textComponent);
    }
}