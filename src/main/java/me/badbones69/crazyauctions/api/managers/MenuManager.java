package me.badbones69.crazyauctions.api.managers;

import me.badbones69.crazyauctions.Methods;
import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.enums.MenuButtons;
import me.badbones69.crazyauctions.api.enums.MenuPage;
import me.badbones69.crazyauctions.api.objects.ItemBuilder;
import me.badbones69.crazyauctions.api.objects.gui.Button;
import me.badbones69.crazyauctions.api.objects.gui.PlayerPage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MenuManager {
    
    private static MenuManager instance = new MenuManager();
    private List<PlayerPage> playerPages = new ArrayList<>();
    private Map<MenuButtons, Button> menuButtons = new HashMap<>();
    private Map<MenuPage, String> menuPageNames = new HashMap<>();
    
    public static MenuManager getInstance() {
        return instance;
    }
    
    public void load() {
        menuButtons.clear();
        menuPageNames.clear();
        FileConfiguration config = Files.CONFIG.getFile();
        for (MenuButtons button : MenuButtons.values()) {
            String path = "Settings.Buttons." + button.getPath() + ".";
            menuButtons.put(button, new Button(new ItemBuilder()
            .setMaterial(config.getString(path + "Item", "Stone"))
            .setName(config.getString(path + "Name", ""))
            .setLore(config.getStringList(path + "Lore")),
            config.getInt(path + "Slot", 0),
            config.getBoolean(path + "Enabled", true)));
        }
        Arrays.stream(MenuPage.values()).forEach(menuPage -> menuPageNames.put(menuPage, Methods.color(config.getString("Settings.Menu-Pages." + menuPage.getPathName() + ".Name", "&l&7>> &8Crazy Auctions"))));
    }
    
    public String getInventoryName(MenuPage menuPage) {
        return menuPageNames.get(menuPage);
    }
    
    public Button getButton(MenuButtons button) {
        return menuButtons.get(button);
    }
    
    private PlayerPage getPlayerPage(UUID uuid) {
        for (PlayerPage playerPage : playerPages) {
            if (playerPage.getUUID().equals(uuid)) {
                return playerPage;
            }
        }
        return null;
    }
    
}