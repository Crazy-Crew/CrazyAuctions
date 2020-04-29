package me.badbones69.crazyauctions.api.managers;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.enums.MenuButtons;
import me.badbones69.crazyauctions.api.objects.Button;
import me.badbones69.crazyauctions.api.objects.ItemBuilder;
import me.badbones69.crazyauctions.api.objects.PlayerPage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuManager {
    
    private static MenuManager instance = new MenuManager();
    private List<PlayerPage> playerPages = new ArrayList<>();
    private Map<MenuButtons, Button> menuButtons = new HashMap<>();
    
    public void load() {
        menuButtons.clear();
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
    }
    
    public Button getButton(MenuButtons button) {
        return menuButtons.get(button);
    }
    
    public static MenuManager getInstance() {
        return instance;
    }
    
}