package me.badbones69.crazyauctions.api.objects;

import java.util.UUID;

public class PlayerPage {
    
    private UUID uuid;
    private int page;
    
    public PlayerPage(UUID uuid, int page) {
        this.uuid = uuid;
        this.page = page;
    }
    
    public UUID getUUID() {
        return uuid;
    }
    
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public void nextPage() {
        page++;
    }
    
    public void backPage() {
        page--;
    }
    
}