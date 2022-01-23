package me.badbones69.crazyauctions.api;

import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.api.FileManager.Files;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public enum Messages {
    
    PLAYERS_ONLY("Players-Only", "&cOnly players can use this command."),
    RELOAD("Reload", "&7You have just reloaded the Crazy Auctions Files."),
    NEED_MORE_MONEY("Need-More-Money", "&cYou are in need of &a$%money_needed%&c."),
    INVENTORY_FULL("Inventory-Full", "&cYour inventory is too full. Please open up some space to buy that."),
    NO_PERMISSION("No-Permission", "&cYou do not have permission to use that command!"),
    NOT_ONLINE("Not-Online", "&cThat player is not online at this time."),
    DOSENT_HAVE_ITEM_IN_HAND("Doesnt-Have-Item-In-Hand", "&cYou must have an item in your hand."),
    NOT_A_NUMBER("Not-A-Number", "&c%arg% is not a number."),
    GOT_ITEM_BACK("Got-Item-Back", "&7Your item has been returned."),
    CANCELLED_ITEM("Cancelled-Item", "&7You have cancelled an item on the auction list, return your items with /ah expired."),
    ITEM_HAS_EXPIRED("Item-Has-Expired", "&7An item you have in the Crazy Auctions has just expired."),
    ADMIN_FORCE_CENCELLED("Admin-Force-Cancelled", "&7You have just force cancelled a sale."),
    ADMIN_FORCE_CANCELLED_TO_PLAYER("Admin-Force-Cancelled-To-Player", "&cOne of your items was just force cancelled by an Admin."),
    ITEM_DOESNT_EXIST("Item-Doesnt-Exist", "&cThat item isnt in the crazy auctions any more."),
    MAX_ITEMS("Max-Items", "&cYou cant list any more items to the Crazy Auctions."),
    ITEM_BLACKLISTED("Item-BlackListed", "&cThat item is not allowed to be sold here."),
    ITEM_DAMAGED("Item-Damaged", "&cThat item is damaged and is not allowed to be sold here."),
    SOLD_MESSAGE("Sold-Msg", "&7Thank you for buying this item."),
    BID_MORE_MONEY("Bid-More-Money", "&cYour bid is to low, please bid more."),
    NOT_A_CURRENCY("Not-A-Currency", "&cThat is not a currency. Please use Money or Tokens ."),
    SELL_PRICE_TO_LOW("Sell-Price-To-Low", "&cYour sell price is to low the minimum is &a$10&c."),
    SELL_PRICE_TO_HIGH("Sell-Price-To-High", "&cYour sell price is to high the maximum is &a$1000000&c."),
    BID_PRICE_TO_LOW("Bid-Price-To-Low", "&cYour starting bid price is to low the minimum is &a$100&c."),
    BID_PRICE_TO_HIGH("Bid-Price-To-High", "&cYour starting bid price is to high the maximum is &a$1000000&c."),
    BOUGHT_ITEM("Bought-Item", "&7You have just bought a item for &a$%price%&7."),
    WIN_BIDDING("Win-Bidding", "&7You have just won a bid for &a$%price%&7. Do /Ah Collect to collect your winnings."),
    PLAYER_BOUGHT_ITEM("Player-Bought-Item", "&7%player% has bought your item for &a$%price%."),
    SOMEONE_WON_PLAYERS_BID("Someone-Won-Players-Bid", "&7%player% has won your item you from a bid for &a$%price%."),
    ADDED_ITEM_TO_AUCTION("Added-Item-To-Auction", "&7You have just added a item to the crazy auctions for &a$%price%&7."),
    BID_MESSAGE("Bid-Msg", "&7You have just bid &a$%Bid% &7on that item."),
    SELLING_DISABLED("Selling-Disabled", "&cThe selling option is disabled."),
    BIDDING_DISABLED("Bidding-Disabled", "&cThe bidding option is disabled."),
    CRAZYAUCTIONS_HELP("CrazyAuctions-Help", "&c/ah help"),
    CRAZYAUCTIONS_VIEW("CrazyAuctions-View", "&c/ah view <player>"),
    CRAZYAUCTIONS_SEARCH("CrazyAuctions-View", "&c/ah search <player>"),
    CRAZYAUCTIONS_SELL_BID("CrazyAuctions-Sell-Bid", "&c/ah sell/bid <price> [amount of items]"),
    BOOK_NOT_ALLOWED("Book-Not-Allowed", "&cThat book is not able to be sold in this auction house!"),
    HELP("Help-Menu", Arrays.asList(
    "&e-- &6Crazy Auctions Help &e--",
    "&9/Ah - &eOpens the crazy auction.",
    "&9/Ah View <Player> - &eSee what a player is selling.",
    "&9/Ah Search <Phrase> - &eSearch all items containing given phrase.",
    "&9/Ah Sell/Bid <Price> [Amount of items] - &eList the item you are holding on the crazy auction.",
    "&9/Ah Expired/Collect - &eView and manage your cancelled and expired items.",
    "&9/Ah Listed - &eView and manage the items you are selling.",
    "&9/Ah Help - &eView this help menu."));
    
    private static FileManager fileManager = FileManager.getInstance();
    private String path;
    private String defaultMessage;
    private List<String> defaultListMessage;
    
    private Messages(String path, String defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }
    
    private Messages(String path, List<String> defaultListMessage) {
        this.path = path;
        this.defaultListMessage = defaultListMessage;
    }
    
    public static String convertList(List<String> list) {
        String message = "";
        for (String m : list) {
            message += Methods.color(m) + "\n";
        }
        return message;
    }
    
    public static String convertList(List<String> list, HashMap<String, String> placeholders) {
        String message = "";
        for (String m : list) {
            message += Methods.color(m) + "\n";
        }
        for (String ph : placeholders.keySet()) {
            message = Methods.color(message.replace(ph, placeholders.get(ph))).replace(ph, placeholders.get(ph).toLowerCase());
        }
        return message;
    }
    
    public static void addMissingMessages() {
        FileConfiguration messages = Files.MESSAGES.getFile();
        boolean saveFile = false;
        for (Messages message : values()) {
            if (!messages.contains("Messages." + message.getPath())) {
                saveFile = true;
                if (message.getDefaultMessage() != null) {
                    messages.set("Messages." + message.getPath(), message.getDefaultMessage());
                } else {
                    messages.set("Messages." + message.getPath(), message.getDefaultListMessage());
                }
            }
        }
        if (saveFile) {
            Files.MESSAGES.saveFile();
        }
    }
    
    public String getMessage() {
        if (isList()) {
            if (exists()) {
                return Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
            } else {
                return Methods.color(convertList(getDefaultListMessage()));
            }
        } else {
            if (exists()) {
                return Methods.getPrefix(Files.MESSAGES.getFile().getString("Messages." + path));
            } else {
                return Methods.getPrefix(getDefaultMessage());
            }
        }
    }
    
    public String getMessage(HashMap<String, String> placeholders) {
        String message;
        if (isList()) {
            if (exists()) {
                message = Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path), placeholders));
            } else {
                message = Methods.color(convertList(getDefaultListMessage(), placeholders));
            }
        } else {
            if (exists()) {
                message = Methods.getPrefix(Files.MESSAGES.getFile().getString("Messages." + path));
            } else {
                message = Methods.getPrefix(getDefaultMessage());
            }
            for (String ph : placeholders.keySet()) {
                if (message.contains(ph)) {
                    message = message.replace(ph, placeholders.get(ph)).replace(ph, placeholders.get(ph).toLowerCase());
                }
            }
        }
        return message;
    }
    
    public String getMessageNoPrefix() {
        if (isList()) {
            if (exists()) {
                return Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path)));
            } else {
                return Methods.color(convertList(getDefaultListMessage()));
            }
        } else {
            if (exists()) {
                return Methods.color(Files.MESSAGES.getFile().getString("Messages." + path));
            } else {
                return Methods.color(getDefaultMessage());
            }
        }
    }
    
    public String getMessageNoPrefix(HashMap<String, String> placeholders) {
        String message;
        if (isList()) {
            if (exists()) {
                message = Methods.color(convertList(Files.MESSAGES.getFile().getStringList("Messages." + path), placeholders));
            } else {
                message = Methods.color(convertList(getDefaultListMessage(), placeholders));
            }
        } else {
            if (exists()) {
                message = Methods.color(Files.MESSAGES.getFile().getString("Messages." + path));
            } else {
                message = Methods.color(getDefaultMessage());
            }
            for (String ph : placeholders.keySet()) {
                if (message.contains(ph)) {
                    message = message.replace(ph, placeholders.get(ph)).replace(ph, placeholders.get(ph).toLowerCase());
                }
            }
        }
        return message;
    }
    
    private Boolean exists() {
        return Files.MESSAGES.getFile().contains("Messages." + path);
    }
    
    private Boolean isList() {
        if (Files.MESSAGES.getFile().contains("Messages." + path)) {
            return !Files.MESSAGES.getFile().getStringList("Messages." + path).isEmpty();
        } else {
            return defaultMessage == null;
        }
    }
    
    private String getPath() {
        return path;
    }
    
    private String getDefaultMessage() {
        return defaultMessage;
    }
    
    private List<String> getDefaultListMessage() {
        return defaultListMessage;
    }
    
}