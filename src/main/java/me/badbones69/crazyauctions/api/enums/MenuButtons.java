package me.badbones69.crazyauctions.api.enums;

import me.badbones69.crazyauctions.api.managers.MenuManager;
import me.badbones69.crazyauctions.api.objects.gui.Button;

public enum MenuButtons {
    
    CURRENT_ITEMS("Current-Items"),
    EXPIRED_ITEMS("Expired-Items"),
    NEXT_PAGE("Next-Page"),
    REFRESH("Refresh"),
    PREVIOUS_PAGE("Previous-Page"),
    CATEGORY_1("Category-1"),
    CATEGORY_2("Category-2"),
    BACK_BUTTON("Back-Button"),
    RETURN_ALL_BUTTON("Return-All-Button"),
    CONFIRM("Confirm"),
    CANCEL("Cancel"),
    YOUR_ITEM("Your-Item"),
    CANT_AFFORD("Cant-Afford"),
    TOP_BIDDER("Top-Bidder"),
    BIDDING_EXPLAIN("Bidding-Explain"),
    BID_CONFIRM("Bid-Confirm"),
    SELLING_ITEMS("Selling-Items"),
    BIDDING_ITEMS("Bidding-Items"),
    WHAT_IS_THIS_SELLING("What-Is-This.Selling"),
    WHAT_IS_THIS_BIDDING("What-Is-This.Bidding"),
    WHAT_IS_THIS_CURRENT_ITEMS("What-Is-This.Current-Items"),
    WHAT_IS_THIS_EXPIRED_ITEMS("What-Is-This.Expired-Items"),
    WHAT_IS_THIS_VIEWING("What-Is-This.Viewing"),
    WHAT_IS_THIS_CATEGORIES("What-Is-This.Categories");
    
    private String path;
    private MenuManager menuManager = MenuManager.getInstance();
    
    private MenuButtons(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    
    public Button getButton() {
        return menuManager.getButton(this);
    }
    
}