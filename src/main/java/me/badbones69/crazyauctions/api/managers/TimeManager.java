package me.badbones69.crazyauctions.api.managers;

import me.badbones69.crazyauctions.api.FileManager.Files;
import me.badbones69.crazyauctions.api.objects.Time;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public enum TimeManager {
    
    SELL_TIME("Selling-Time", "2d"),
    BID_TIME("Bidding-Time", "2h"),
    EXPIRE_TIME("Expire-Time", "10d"),
    BID_WINNER_TIME("Bidding-Winner-Time", "20d");
    
    private static Map<TimeManager, Time> cachedTimes = new HashMap<>();
    private String path;
    private String defaultTime;
    
    private TimeManager(String path, String defaultTime) {
        this.path = path;
        this.defaultTime = defaultTime;
    }
    
    public static void load() {
        cachedTimes.clear();
        FileConfiguration config = Files.CONFIG.getFile();
        String path = "Settings.Timers.";
        for (TimeManager time : TimeManager.values()) {
            cachedTimes.put(time, new Time(config.getString(path + time.path, time.defaultTime)));
        }
    }
    
    public Time getTime() {
        return cachedTimes.get(this);
    }
    
}