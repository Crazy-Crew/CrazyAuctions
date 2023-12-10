package us.crazycrew.crazyauctions.api.enums;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.properties.Property;
import com.badbones69.crazyauctions.common.config.types.ConfigKeys;
import com.ryderbelserion.cluster.paper.utils.AdvUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazyauctions.CrazyAuctions;
import java.util.HashMap;
import java.util.Map;

public enum Messages {

    ;

    private final Property<String> property;

    private String message;

    Messages(Property<String> property) {
        this.property = property;
    }

    @NotNull
    private final CrazyAuctions plugin = CrazyAuctions.get();

    @NotNull
    private final SettingsManager config = this.plugin.getFactory().getConfig();

    @NotNull
    private String getProperty(@NotNull Property<String> property) {
        return this.config.getProperty(property);
    }

    @NotNull
    public Messages getMessage() {
        return getMessage(new HashMap<>());
    }

    @NotNull
    public Messages getMessage(@NotNull String placeholder, @NotNull String replacement) {
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put(placeholder, replacement);

        return getMessage(placeholders);
    }

    @NotNull
    public Messages getMessage(@NotNull HashMap<String, String> placeholder) {
        String message;

        message = getProperty(this.property);

        if (!placeholder.isEmpty()) {
            for (Map.Entry<String, String> value : placeholder.entrySet()) {
                message = message.replace(value.getKey(), value.getValue()).replace(value.getKey().toLowerCase(), value.getValue());
            }
        }

        this.message = message;

        return this;
    }

    @NotNull
    public String toMessage() {
        String prefix = this.config.getProperty(ConfigKeys.command_prefix);

        return this.message.replaceAll("\\{prefix}", prefix);
    }

    @NotNull
    public String toStringMessage() {
        return getMessage().message;
    }

    @NotNull
    public Component toSimpleComponent() {
        return AdvUtils.parse(getMessage().toMessage());
    }

    @NotNull
    public Component toAdvancedComponent() {
        return AdvUtils.parse(toMessage());
    }
}