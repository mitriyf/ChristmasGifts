package ru.mitriyf.christmasgifts.hook.hologram.impl;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
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
import java.util.UUID;

public class DecentHolograms implements HologramSupport {
    private final Map<Location, Hologram> decentHolograms = new HashMap<>();
    private final ChristmasGifts plugin;
    private final Values values;
    private final Utils utils;

    public DecentHolograms(ChristmasGifts plugin) {
        this.plugin = plugin;
        utils = plugin.getUtils();
        values = plugin.getValues();
    }

    @Override
    public void createHologram(Player player, Location hologramLocation, Location blockLocation) {
        String hologramName = UUID.randomUUID().toString();
        if (DHAPI.getHologram(hologramName) != null) {
            createHologram(player, hologramLocation, blockLocation);
            return;
        }
        Hologram hologram = DHAPI.createHologram(hologramName, hologramLocation);
        decentHolograms.put(blockLocation, hologram);
        HologramData hologramData = values.getHologramDataMap().getOrDefault(utils.getLocale().player(player), values.getHologramDataMap().get(""));
        Map<Integer, String> time = hologramData.getTimeLines();
        int removeTime = values.getRemove();
        for (String text : hologramData.getLines()) {
            text = text.replace("%player%", player.getName());
            String colorized = utils.formatHologram(player, text.replace("%time%", String.valueOf(removeTime)));
            DHAPI.addHologramLine(hologram, colorized);
        }
        if (!time.isEmpty()) {
            new BukkitRunnable() {
                int timer = removeTime;

                @Override
                public void run() {
                    timer--;
                    if (timer <= 0 || hologram.isDisabled()) {
                        cancel();
                        return;
                    }
                    for (int line : time.keySet()) {
                        String lore = utils.formatHologram(player, time.get(line).replace("%time%", String.valueOf(timer)));
                        DHAPI.setHologramLine(hologram, line, lore);
                    }
                }
            }.runTaskTimer(plugin, 20, 20);
        }
    }

    @Override
    public void removeHologram(Location blockLocation) {
        Hologram hologram = decentHolograms.get(blockLocation);
        if (hologram != null) {
            hologram.delete();
            decentHolograms.remove(blockLocation);
        }
    }

    @Override
    public void clear() {
        for (Hologram hologram : new HashSet<>(decentHolograms.values())) {
            hologram.delete();
        }
        decentHolograms.clear();
    }
}
