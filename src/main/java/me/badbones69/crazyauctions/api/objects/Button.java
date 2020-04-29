package me.badbones69.crazyauctions.api.objects;

public class Button {
    
    private ItemBuilder item;
    private int slot;
    private boolean enabled;
    
    public Button(ItemBuilder item, int slot, boolean enabled) {
        this.item = item;
        this.slot = slot;
        this.enabled = enabled;
    }
    
    public ItemBuilder getItem() {
        return item;
    }
    
    public int getSlot() {
        return slot;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
}