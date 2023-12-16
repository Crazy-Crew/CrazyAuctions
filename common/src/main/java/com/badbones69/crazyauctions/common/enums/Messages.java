package com.badbones69.crazyauctions.common.enums;

import ch.jalu.configme.properties.Property;
import com.badbones69.crazyauctions.common.AuctionsFactory;
import com.badbones69.crazyauctions.common.config.types.Config;
import com.badbones69.crazyauctions.common.config.types.Locale;
import com.ryderbelserion.cluster.utils.AdvUtils;
import com.ryderbelserion.cluster.utils.StringUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Messages {

    plugin_reload(Locale.plugin_reload),
    page_header(Locale.page_header),
    page_format(Locale.page_format),
    page_footer(Locale.page_footer),
    hover_format(Locale.hover_format),
    invalid_page(Locale.invalid_page),
    next_button(Locale.next_button),
    navigation_text(Locale.navigation_text),
    back_button(Locale.back_button);

    private Property<String> property;

    private Property<List<String>> listProperty;

    private boolean isList = false;

    private String message;

    /**
     * Used for strings
     *
     * @param property the property
     */
    Messages(Property<String> property) {
        this.property = property;
    }

    /**
     * Used for string lists
     *
     * @param listProperty the list property
     * @param isList Defines if it's a list or not.
     */
    Messages(Property<List<String>> listProperty, boolean isList) {
        this.listProperty = listProperty;

        this.isList = isList;
    }

    private boolean isList() {
        return this.isList;
    }

    private @NotNull List<String> getPropertyList(Property<List<String>> properties) {
        return AuctionsFactory.getMessages().getProperty(properties);
    }

    private @NotNull String getProperty(Property<String> property) {
        return AuctionsFactory.getMessages().getProperty(property);
    }

    public String getString() {
        return getMessage().toString();
    }

    public Messages getMessage() {
        return getMessage(new HashMap<>());
    }

    public Messages getMessage(String placeholder, String replacement) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return getMessage(placeholders);
    }

    public Messages getMessage(Map<String, String> placeholders) {
        // Get the string first.
        String message;

        if (isList()) {
            message = StringUtils.convertList(getPropertyList(this.listProperty));
        } else {
            message = getProperty(this.property);
        }

        if (!placeholders.isEmpty()) {
            for (Map.Entry<String, String> placeholder : placeholders.entrySet()) {
                message = message.replace(placeholder.getKey(), placeholder.getValue()).replace(placeholder.getKey().toLowerCase(), placeholder.getValue());
            }
        }

        this.message = message;

        return this;
    }

    public String asString() {
        return this.message;
    }

    public Component asComponent() {
        return AdvUtils.parse(this.message.replaceAll("\\{prefix}", AuctionsFactory.getConfig().getProperty(Config.command_prefix)));
    }

    public void sendMessage(Audience audience) {
        sendMessage(audience, new HashMap<>());
    }

    public void sendMessage(Audience audience, Map<String, String> placeholders) {
        audience.sendMessage(getMessage(placeholders).asComponent());
    }
}