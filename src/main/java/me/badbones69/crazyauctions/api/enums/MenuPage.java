package me.badbones69.crazyauctions.api.enums;

public enum MenuPage {
    
    CATEGORY_MENU("Category-Menu", 54),
    AUCTION_SELLING("Auction-Selling", 54),
    AUCTION_BIDDING("Auction-Bidding", 54),
    CURRENT_SALES("Current-Sales", 54),
    EXPIRED("Expired", 54),
    BUYING_PAGE("Buying-Page", 54),
    BIDDING_PAGE("Bidding-Page", 54);
    
    private String pathName;
    private int slots;
    
    private MenuPage(String pathName, int slots) {
        this.pathName = pathName;
        this.slots = slots;
    }
    
    public String getPathName() {
        return pathName;
    }
    
    public int getSlots() {
        return slots;
    }
    
}