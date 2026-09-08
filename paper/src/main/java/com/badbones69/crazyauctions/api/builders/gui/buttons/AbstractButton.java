package com.badbones69.crazyauctions.api.builders.gui.buttons;

import com.badbones69.crazyauctions.api.builders.items.BaseItemBuilder;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NullMarked;
import java.util.List;
import java.util.Map;

@NullMarked
public class AbstractButton {

    protected final BaseItemBuilder builder;
    protected final int slot;

    public AbstractButton(final BaseItemBuilder builder, final int slot) {
        this.builder = builder;
        this.slot = slot;
    }

    public void setItem(final Player player, final Inventory inventory, final List<Integer> slots, final Map<NamespacedKey, String> keys, final Map<String, String> placeholders) {
        final ItemStack itemStack = this.builder.setPlaceholders(placeholders).asItemStack(player);

        final PersistentDataContainerView container = itemStack.getPersistentDataContainer();

        for (final Map.Entry<NamespacedKey, String> entry : keys.entrySet()) {
            final NamespacedKey key = entry.getKey();
            final String value = entry.getValue();

            if (container.has(key)) continue;

            itemStack.editPersistentDataContainer(item -> item.set(key, PersistentDataType.STRING, value));
        }

        slots.forEach(slot -> inventory.setItem(slot, itemStack));
    }

    public void setItem(final Player player, final Inventory inventory, final Map<NamespacedKey, String> keys, final List<Integer> slots) {
        setItem(player, inventory, slots, keys, Map.of());
    }

    public void setItem(final Player player, final Inventory inventory, final Map<NamespacedKey, String> keys, final Map<String, String> placeholders) {
        setItem(player, inventory, List.of(this.slot), keys, placeholders);
    }

    public void setItem(final Player player, final Inventory inventory, final int slot, final Map<NamespacedKey, String> keys, final Map<String, String> placeholders) {
        setItem(player, inventory, List.of(slot), keys, placeholders);
    }

    public void setItem(final Player player, final Inventory inventory, final int slot, final Map<NamespacedKey, String> keys) {
        setItem(player, inventory, slot, keys, Map.of());
    }

    public void setItem(final Player player, final Inventory inventory, final Map<NamespacedKey, String> keys) {
        setItem(player, inventory, keys, Map.of());
    }

    public BaseItemBuilder getBuilder() {
        return this.builder;
    }

    public int getSlot() {
        return this.slot;
    }
}