package com.badbones69.crazyauctions.api.builders.gui.buttons.types;

import com.badbones69.crazyauctions.api.builders.gui.buttons.AbstractButton;
import com.badbones69.crazyauctions.api.builders.items.ItemBuilder;
import com.badbones69.crazyauctions.api.enums.misc.Keys;
import org.jspecify.annotations.NullMarked;
import java.util.List;

@NullMarked
public final class GenericButton extends AbstractButton {

    public GenericButton(final String item, final String name, final String id, final List<String> lore, final int slot, final Keys key) {
        super(ItemBuilder.from(item.toLowerCase()).withDisplayName(name).withDisplayLore(lore).setPersistentString(key.getNamespacedKey(), id), slot);
    }
}