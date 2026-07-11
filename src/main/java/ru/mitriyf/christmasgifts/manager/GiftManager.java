package ru.mitriyf.christmasgifts.manager;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.model.GiftData;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Getter
public class GiftManager {
    private final Map<String, Integer> limitGifts = new HashMap<>();
    private final Map<Location, GiftData> gifts = new HashMap<>();
    private final ChristmasGifts plugin;
    private final Values values;
    private final Logger logger;
    private final Utils utils;

    public GiftManager(ChristmasGifts plugin) {
        this.plugin = plugin;
        utils = plugin.getUtils();
        values = plugin.getValues();
        logger = plugin.getLogger();
    }

    public void close() {
        utils.getGiftStorage().unregister();
        for (Map.Entry<Location, GiftData> giftDataEntry : gifts.entrySet()) {
            Location location = giftDataEntry.getKey();
            GiftData giftData = giftDataEntry.getValue();
            giftData.getTask().cancel();
            boolean takedLoot = values.isTakedLoot();
            String name = plugin.getServer().getPlayer(giftData.getUuid()).getName();
            if (takedLoot || values.isOnCrashes()) {
                removeOnCrashes(location, name);
            }
            if (takedLoot) {
                plugin.getLootService().dropLoot(location.getWorld(), location);
            } else {
                Map<String, Integer> players = plugin.getGiftManager().getLimitGifts();
                players.put(name, players.get(name) - 1);
            }
        }
        gifts.clear();
        values.saveDatabase();
    }

    public void removeOnCrashes(Location blockLocation, String name) {
        if (!values.isOnCrashes()) {
            return;
        }
        FileConfiguration database = values.getDatabase();
        List<String> giftList = database.getStringList("gifts");
        if (giftList != null && !giftList.isEmpty()) {
            giftList.remove(blockLocation.getWorld().getName() + ":" + blockLocation.getX() + ":" + blockLocation.getY() + ":" + blockLocation.getZ() + ":" + name);
            database.set("gifts", giftList);
            try {
                database.save(new File(plugin.getDataFolder(), "storage/db.yml"));
            } catch (Exception e) {
                logger.warning("db.yml save failed. Error: " + e);
            }
        }
    }
}