package me.badbones69.crazyauctions.api.multiworld;

import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class PerWorld {
    
    private String worldName;
    private List<AuctionItem> auctionItems = new ArrayList<>();
    
    public PerWorld(String worldName) {
        this.worldName = worldName;
    }
    
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
    
    public String getWorldName() {
        return worldName;
    }
    
    public void addAuctionItem(AuctionItem auctionItem) {
        auctionItems.add(auctionItem);
    }
    
    public void removeAuctionItem(AuctionItem auctionItem) {
        auctionItems.remove(auctionItem);
    }
    
    public List<AuctionItem> getAuctionItems() {
        return auctionItems;
    }
    
}