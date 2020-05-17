package me.badbones69.crazyauctions.api.objects.gui;

import me.badbones69.crazyauctions.api.enums.MenuButtons;
import me.badbones69.crazyauctions.api.enums.MenuPage;
import me.badbones69.crazyauctions.api.enums.ShopType;
import me.badbones69.crazyauctions.api.interfaces.AuctionItem;
import me.badbones69.crazyauctions.api.managers.AuctionManager;
import me.badbones69.crazyauctions.api.managers.MenuManager;
import me.badbones69.crazyauctions.api.objects.items.BidItem;
import me.badbones69.crazyauctions.api.objects.items.SellItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class AuctionHouse implements InventoryHolder {
    
    private Inventory inventory;
    private int page;
    private ShopType shopType;
    private MenuPage menuPage;
    private List<SellItem> linkedSellItems;
    private List<BidItem> linkedBidItems;
    private static MenuManager menuManager = MenuManager.getInstance();
    private static AuctionManager auctionManager = AuctionManager.getInstance();
    private ItemStack air = new ItemStack(Material.AIR);
    
    public AuctionHouse(int page, ShopType shopType, List<SellItem> linkedSellItems, List<BidItem> linkedBidItems) {
        this.page = page;
        this.shopType = shopType;
        this.linkedSellItems = linkedSellItems;
        this.linkedBidItems = linkedBidItems;
        menuPage = shopType == ShopType.SELL ? MenuPage.AUCTION_SELLING : MenuPage.AUCTION_BIDDING;
        inventory = Bukkit.createInventory(this, menuPage.getSlots(), menuManager.getInventoryName(menuPage));
        setNavBar();
        updateInventory();
    }
    
    public int getPage() {
        return page;
    }
    
    public ShopType getShopType() {
        return shopType;
    }
    
    public MenuPage getMenuPage() {
        return menuPage;
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    public InventoryView openInventory(Player player) {
        return player.openInventory(inventory);
    }
    
    public void updateInventory() {
        resetSlots();
        int slot = 0;
        for (AuctionItem auctionItem : getPageItems()) {
            inventory.setItem(slot, auctionItem.getItem());
            slot++;
        }
    }
    
    private void resetSlots() {
        IntStream.range(0, 45).forEach(slot -> inventory.setItem(slot, air));
    }
    
    private void setNavBar() {
        setButton(menuManager.getButton(MenuButtons.CURRENT_ITEMS));
        setButton(menuManager.getButton(MenuButtons.EXPIRED_ITEMS));
        setButton(menuManager.getButton(MenuButtons.CATEGORY_1));
        setButton(menuManager.getButton(MenuButtons.PREVIOUS_PAGE));
        setButton(menuManager.getButton(MenuButtons.REFRESH));
        setButton(menuManager.getButton(MenuButtons.NEXT_PAGE));
        setButton(menuManager.getButton(MenuButtons.CATEGORY_2));
        setButton(menuManager.getButton(shopType == ShopType.SELL ? MenuButtons.SELLING_ITEMS : MenuButtons.BIDDING_ITEMS));
        setButton(menuManager.getButton(shopType == ShopType.SELL ? MenuButtons.WHAT_IS_THIS_SELLING : MenuButtons.WHAT_IS_THIS_BIDDING));
    }
    
    private void setButton(Button button) {
        if (button.isEnabled()) inventory.setItem(button.getSlot(), button.getItem().build());
    }
    
    private List<AuctionItem> getPageItems() {
        List<AuctionItem> itemList = new ArrayList<>(shopType == ShopType.SELL ? linkedSellItems : linkedBidItems);
        List<AuctionItem> pageItems = new ArrayList<>();
        int maxItems = 45;
        int index = page * maxItems - maxItems;
        int endIndex = index >= itemList.size() ? itemList.size() - 1 : index + maxItems;
        while (index < endIndex) {
            if (index < itemList.size()) pageItems.add(itemList.get(index));
            index++;
        }
        return pageItems;
    }
    
}