package com.badbones69.crazyauctions.api;

import com.badbones69.crazyauctions.CrazyAuctions;
import com.badbones69.crazyauctions.Methods;
import com.badbones69.crazyauctions.api.enums.Files;
import com.badbones69.crazyauctions.api.enums.CurrencyData;
import com.badbones69.crazyauctions.api.enums.ShopType;
import com.badbones69.crazyauctions.currency.CoinsEngineSupport;
import com.badbones69.crazyauctions.currency.EconomySession;
import com.badbones69.crazyauctions.currency.EconomySessionFactory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CrazyManager {

    private static final CrazyAuctions plugin = CrazyAuctions.get();

    private boolean sellingEnabled;
    private boolean biddingEnabled;
    private boolean coinsEngineEnabled;
    private Map<String, CurrencyData> registeredCoinsEngineCurrencies;
    private String defaultCurrencySymbol;

    public void load() {
        FileConfiguration config = Files.config.getConfiguration();
        this.sellingEnabled = config.getBoolean("Settings.Feature-Toggle.Selling", true);

        this.biddingEnabled = config.getBoolean("Settings.Feature-Toggle.Bidding", true);

        this.coinsEngineEnabled = plugin.isCoinsEngineEnabled();

        this.defaultCurrencySymbol = config.getString("Settings.defaultCurrencySymbol", "$");
        this.registeredCoinsEngineCurrencies = this.getValidCoinsEngineCurrencies(config);
    }

    public void unload() {
        Files.data.save();
    }
    
    public boolean isSellingEnabled() {
        return this.sellingEnabled;
    }
    
    public boolean isBiddingEnabled() {
        return this.biddingEnabled;
    }

    public boolean isCurrencyEnabled() {
        return this.coinsEngineEnabled;
    }

    public Map<String, CurrencyData> getRegisteredCurrencies() {
        return this.registeredCoinsEngineCurrencies;
    }

    public String getPriceWithCurrency(String price, String currency) {
        String currencySymbol = this.defaultCurrencySymbol;
        if (currency != null && !currency.isEmpty()) {
            if (this.coinsEngineEnabled && this.registeredCoinsEngineCurrencies.containsKey(currency)) {
                currencySymbol = this.registeredCoinsEngineCurrencies.get(currency).getSymbol();
            }
        }
        return price + " " + currencySymbol;
    }

    public EconomySession getEconomySession(String currency) {
        if (currency != null && !currency.isEmpty()) {
            if (this.coinsEngineEnabled && this.registeredCoinsEngineCurrencies.containsKey(currency)) {
                return EconomySessionFactory.create(plugin.getCoinsEngineSupport(), currency);
            }
        }
        return EconomySessionFactory.create(plugin.getSupport());
    }

    public EconomySession getEconomySession() {
        return getEconomySession(null);
    }

    public ArrayList<ConfigurationSection> getItems(ShopType shop, String playerId) {
        FileConfiguration data = Files.data.getConfiguration();
        ArrayList<ConfigurationSection> items = new ArrayList<>();

        String section = "Items";
        if (data.contains(section)) {
            ConfigurationSection itemsSection = data.getConfigurationSection(section);
            for (String id : itemsSection.getKeys(false)) {
                ConfigurationSection item = itemsSection.getConfigurationSection(id);
                if (!playerId.isEmpty() && !item.getString("Seller", "").equalsIgnoreCase(playerId)) {
                    continue;
                }
                if (shop == null || shop == (item.getBoolean("Biddable") ? ShopType.BID : ShopType.SELL)) {
                    items.add(item);
                }
            }
        }
        return items;
    }

    public ArrayList<ConfigurationSection> getPlayerItems(String playerId, ShopType shop) {
        return getItems(shop, playerId);
    }

    public ArrayList<ConfigurationSection> getPlayerItems(String playerId) {
        return getItems(null, playerId);
    }

    public ArrayList<ConfigurationSection> getItems(ShopType shop) {
        return getItems(shop, "");
    }

    public ArrayList<ConfigurationSection> getItems() {
        return getItems(null, "");
    }

    public ArrayList<ConfigurationSection> getExpiredItems(String playerId) {
        FileConfiguration data = Files.data.getConfiguration();
        ArrayList<ConfigurationSection> items = new ArrayList<>();

        String section = "OutOfTime/Cancelled";
        if (data.contains(section)) {
            ConfigurationSection itemsSection = data.getConfigurationSection(section);
            for (String id : itemsSection.getKeys(false)) {
                ConfigurationSection item = itemsSection.getConfigurationSection(id);
                if (!playerId.isEmpty() && !item.getString("Seller", "").equalsIgnoreCase(playerId)) {
                    continue;
                }
                items.add(item);
            }
        }
        return items;
    }

    public ArrayList<ConfigurationSection> getExpiredItems() {
        return getExpiredItems("");
    }

    private Map<String, CurrencyData> getValidCoinsEngineCurrencies(FileConfiguration config) {
        Map<String, CurrencyData> validCurrencies = new HashMap<>();
        if (this.coinsEngineEnabled) {
            List<String> currencies = config.getStringList("Settings.CoinsEngineSupport.currencies");

            if (currencies == null || currencies.isEmpty()) {
                return validCurrencies;
            }

            CoinsEngineSupport coinsSupport = plugin.getCoinsEngineSupport();
            for (String currencyId : currencies) {
                CurrencyData currency = coinsSupport.getCurrency(currencyId);
                if (currency != null) {
                    validCurrencies.put(currencyId, currency);
                }
            }
        }

        return validCurrencies;
    }
}