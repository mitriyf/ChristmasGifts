package ru.mitriyf.christmasgifts.hook.hologram.impl;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.hook.hologram.HologramSupport;
import ru.mitriyf.christmasgifts.model.HologramData;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class HolographicDisplays implements HologramSupport {
    private final Map<Location, Hologram> holographicDisplays = new HashMap<>();
    private final ChristmasGifts plugin;
    private final Values values;
    private final Utils utils;

    public HolographicDisplays(ChristmasGifts plugin) {
        this.plugin = plugin;
        utils = plugin.getUtils();
        values = plugin.getValues();
    }

    @Override
    public void createHologram(Player player, Location hologramLocation, Location blockLocation) {
        Hologram hologram = HologramsAPI.createHologram(plugin, hologramLocation);
        hologram.setAllowPlaceholders(true);
        holographicDisplays.put(blockLocation, hologram);
        HologramData hologramData = values.getHologramDataMap().getOrDefault(utils.getLocale().player(player), values.getHologramDataMap().get(""));
        Map<Integer, String> time = hologramData.getTimeLines();
        int removeTime = values.getRemove();
        int line = 0;
        for (String text : hologramData.getLines()) {
            text = text.replace("%player%", player.getName());
            String colorized = utils.formatHologram(player, text.replace("%time%", String.valueOf(removeTime)));
            hologram.insertTextLine(line, colorized);
            line++;
        }
        if (!time.isEmpty()) {
            new BukkitRunnable() {
                int timer = removeTime;

                @Override
                public void run() {
                    timer--;
                    if (timer <= 0 || hologram.isDeleted()) {
                        cancel();
                        return;
                    }
                    for (int line : time.keySet()) {
                        String lore = utils.formatHologram(player, time.get(line).replace("%time%", String.valueOf(timer)));
                        hologram.insertTextLine(line, lore);
                    }
                }
            }.runTaskTimer(plugin, 20, 20);
        }
    }

    @Override
    public void removeHologram(Location blockLocation) {
        Hologram hologram = holographicDisplays.get(blockLocation);
        if (hologram != null) {
            hologram.delete();
            holographicDisplays.remove(blockLocation);
        }
    }

    @Override
    public void clear() {
        for (Hologram hologram : new HashSet<>(holographicDisplays.values())) {
            hologram.delete();
        }
        holographicDisplays.clear();
    }
}
