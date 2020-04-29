package me.badbones69.crazyauctions.api.objects;

import java.util.UUID;

public class TopBidder {
    
    private UUID ownerUUID;
    private String ownerName;
    private long bid;
    private boolean hasBid;
    
    public TopBidder() {
        ownerUUID = UUID.randomUUID();
        ownerName = "None";
        bid = 0;
        hasBid = false;
    }
    
    public TopBidder(UUID ownerUUID, String ownerName, long bid) {
        this.ownerUUID = ownerUUID;
        this.ownerName = ownerName;
        this.bid = bid;
        hasBid = true;
    }
    
    public UUID getOwnerUUID() {
        return ownerUUID;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public long getBid() {
        return bid;
    }
    
    public boolean hasBid() {
        return hasBid;
    }
    
}