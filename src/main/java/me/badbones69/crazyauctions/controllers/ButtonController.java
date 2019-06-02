package me.badbones69.crazyauctions.controllers;

import org.bukkit.configuration.file.FileConfiguration;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.objects.DeIncrementButton;
import me.badbones69.crazyauctions.objects.DeIncrementType;

public class ButtonController {
    
    private static ButtonController instance;
    
    private DeIncrementButton _increment_1;
    private DeIncrementButton _increment_2;
    private DeIncrementButton _increment_3;
    private DeIncrementButton _increment_4;
    private DeIncrementButton _decrement_1;
    private DeIncrementButton _decrement_2;
    private DeIncrementButton _decrement_3;
    private DeIncrementButton _decrement_4;
    private FileConfiguration _config;

    public DeIncrementButton getIncrement_1() {
        return _increment_1;
    }

    public DeIncrementButton getIncrement_2() {
        return _increment_2;
    }

    public DeIncrementButton getIncrement_3() {
        return _increment_3;
    }

    public DeIncrementButton getIncrement_4() {
        return _increment_4;
    }

    public DeIncrementButton getDecrement_1() {
        return _decrement_1;
    }

    public DeIncrementButton getDecrement_2() {
        return _decrement_2;
    }

    public DeIncrementButton getDecrement_3() {
        return _decrement_3;
    }

    public DeIncrementButton getDecrement_4() {
        return _decrement_4;
    }

    public static ButtonController getInstance() {
        if (instance == null) {
            instance = new ButtonController();
        }
        return instance;
    }

    public ButtonController() {
        init();
    }

    public void reload() {
        init();
    }

    private void init() {
        _config = Files.CONFIG.getFile();
        _increment_1 = new DeIncrementButton(_config.getString("Settings.Increments.1.Color", "&a"), _config.getString("Settings.Increments.1.Material", ""), _config.getInt("Settings.Increments.1.Amount", 1), DeIncrementType.ADD);
        _increment_2 = new DeIncrementButton(_config.getString("Settings.Increments.2.Color", "&a"), _config.getString("Settings.Increments.2.Material", ""), _config.getInt("Settings.Increments.2.Amount", 10), DeIncrementType.ADD);
        _increment_3 = new DeIncrementButton(_config.getString("Settings.Increments.3.Color", "&a"), _config.getString("Settings.Increments.3.Material", ""), _config.getInt("Settings.Increments.3.Amount", 100), DeIncrementType.ADD);
        _increment_4 = new DeIncrementButton(_config.getString("Settings.Increments.4.Color", "&a"), _config.getString("Settings.Increments.4.Material", ""), _config.getInt("Settings.Increments.4.Amount", 1000), DeIncrementType.ADD);
        _decrement_1 = new DeIncrementButton(_config.getString("Settings.Decrements.1.Color", "&c"), _config.getString("Settings.Decrements.1.Material", ""), _config.getInt("Settings.Decrements.1.Amount", 1), DeIncrementType.SUBTRACT);
        _decrement_2 = new DeIncrementButton(_config.getString("Settings.Decrements.2.Color", "&c"), _config.getString("Settings.Decrements.2.Material", ""), _config.getInt("Settings.Decrements.2.Amount", 10), DeIncrementType.SUBTRACT);
        _decrement_3 = new DeIncrementButton(_config.getString("Settings.Decrements.3.Color", "&c"), _config.getString("Settings.Decrements.3.Material", ""), _config.getInt("Settings.Decrements.3.Amount", 100), DeIncrementType.SUBTRACT);
        _decrement_4 = new DeIncrementButton(_config.getString("Settings.Decrements.4.Color", "&c"), _config.getString("Settings.Decrements.4.Material", ""), _config.getInt("Settings.Decrements.4.Amount", 1000), DeIncrementType.SUBTRACT);
    }

}
