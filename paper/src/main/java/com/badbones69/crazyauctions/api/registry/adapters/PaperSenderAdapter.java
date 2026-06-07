package com.badbones69.crazyauctions.api.registry.adapters;

import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.registry.PaperUserRegistry;
import us.crazycrew.api.adapters.ISenderAdapter;
import com.badbones69.crazyauctions.api.CrazyPlatform;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.paper.FusionPaper;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PaperSenderAdapter extends ISenderAdapter<Component, CommandSender> {

    private final MessageRegistry messageRegistry;
    private final PaperUserRegistry userRegistry;
    private final FusionPaper fusion;

    public PaperSenderAdapter(@NotNull final CrazyPlatform platform) {
        this.messageRegistry = platform.getMessageRegistry();
        this.userRegistry = platform.getUserRegistry();
        this.fusion = platform.getFusion();
    }

    @Override
    public UUID getUniqueId(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getUniqueId();
        }

        return CrazyPlatform.CONSOLE_UUID;
    }

    @Override
    public String getName(@NotNull final CommandSender sender) {
        if (sender instanceof Player player) {
            return player.getName();
        }

        return CrazyPlatform.CONSOLE_NAME;
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
        final Map<String, String> map = new HashMap<>(placeholders);

        final String prefix = Methods.getPrefix();

        if (!prefix.isEmpty()) {
            map.putIfAbsent("{prefix}", prefix);
            map.putIfAbsent("%prefix%", prefix);
        }

        final AtomicReference<String> reference = new AtomicReference<>("");

        if (!(sender instanceof Player player)) {
            this.messageRegistry.getMessage(id).ifPresent(value -> reference.set(value.getValue()));

            return this.fusion.asComponent(sender, reference.get(), map);
        }

        final Optional<PaperPlayerAdapter> optional = this.userRegistry.getUser(player.getUniqueId());

        if (optional.isEmpty()) {
            this.messageRegistry.getMessage(id).ifPresent(value -> reference.set(value.getValue()));

            return this.fusion.asComponent(player, reference.get(), map);
        }

        final PaperPlayerAdapter user = optional.get();

        this.messageRegistry.getMessageByLocale(user.getLocaleKey(), id).ifPresent(value -> reference.set(value.getValue()));

        return this.fusion.asComponent(player, reference.get(), map);
    }

    @Override
    public boolean isConsole(@NotNull final CommandSender sender) {
        return sender instanceof ConsoleCommandSender;
    }
}