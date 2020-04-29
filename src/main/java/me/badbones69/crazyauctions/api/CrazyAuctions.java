package me.badbones69.crazyauctions.api;

public class CrazyAuctions {
    
    private static CrazyAuctions instance = new CrazyAuctions();
    
    public static CrazyAuctions getInstance() {
        return instance;
    }
    
}