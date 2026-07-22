package com.badbones69.crazyauctions.api.registry.adapters;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.registry.PaperUserRegistry;
import us.crazycrew.api.adapters.sender.ISenderAdapter;
import com.badbones69.crazyauctions.api.CrazyAuctionsPaper;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PaperSenderAdapter extends ISenderAdapter<Component, CommandSender> {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final CrazyAuctionsPaper platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final MessageRegistry messageRegistry = this.platform.getMessageRegistry();

    private final PaperUserRegistry userRegistry = this.platform.getUserRegistry();

    @Override
    public UUID getUniqueId(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }

        return us.crazycrew.api.CrazyAuctions.CONSOLE_UUID;
    }

    @Override
    public String getName(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getName();
        }

        return us.crazycrew.api.CrazyAuctions.CONSOLE_NAME;
    }

    @Override
    public void sendActionBar(@NotNull final CommandSender sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders) {
        final Component component = getComponent(sender, id, placeholders);

        if (component.equals(Component.empty())) {
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(component);

            return;
        }

        sender.sendActionBar(component);
    }

    @Override
    public void sendMessage(@NotNull final CommandSender sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders) {
        final Component component = getComponent(sender, id, placeholders);

        if (component.equals(Component.empty())) {
            return;
        }

        sender.sendMessage(component);
    }

    @Override
    public Component getComponent(@NotNull final CommandSender sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders) {
        final List<String> values = new ArrayList<>();

        this.messageRegistry.getMessage(id).ifPresent(value -> values.add(value.getValue()));

        if (values.isEmpty()) {
            return Component.empty();
        }

        final String value = values.getFirst();

        if (value.isEmpty()) {
            return Component.empty();
        }

        final Map<String, String> map = new HashMap<>(placeholders);

        final String prefix = Methods.getPrefix();

        if (!prefix.isEmpty()) {
            map.putIfAbsent("{prefix}", prefix);
        }

        return this.fusion.asComponent(sender, values.getFirst(), map);
    }

    @Override
    public String getMessage(@NotNull final CommandSender sender, @NotNull final FusionKey id, @NotNull final Map<String, String> placeholders) {
        final List<String> values = new ArrayList<>();

        this.messageRegistry.getMessage(id).ifPresent(value -> values.add(value.getValue()));

        if (values.isEmpty()) {
            return "";
        }

        final String value = values.getFirst();

        if (value.isEmpty()) {
            return "";
        }

        final Map<String, String> map = new HashMap<>(placeholders);

        final String prefix = Methods.getPrefix();

        if (!prefix.isEmpty()) {
            map.putIfAbsent("{prefix}", prefix);
        }

        return this.fusion.replacePlaceholders(this.fusion.papi(sender, value), map);
    }

    @Override
    public boolean isConsole(@NotNull final CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}