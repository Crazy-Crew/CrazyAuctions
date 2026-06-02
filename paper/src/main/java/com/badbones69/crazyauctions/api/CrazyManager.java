package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.CrazyAuctions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import java.util.List;

@Deprecated(forRemoval = true)
public class CrazyManager {

    private final CrazyAuctions plugin = CrazyAuctions.get();

    private final CrazyPlatform platform = this.plugin.getPlatform();

    public void load() {
        this.platform.init();
    }

    public void unload() {
        this.platform.stop();
    }
    
    public boolean isSellingEnabled() {
        return this.platform.isSellModuleEnabled();
    }
    
    public boolean isBiddingEnabled() {
        return this.platform.isBidModuleEnabled();
    }
    
    public @NonNull final List<ItemStack> getItems(@NonNull final Player player) {
        return this.platform.getItems(player);
    }
}