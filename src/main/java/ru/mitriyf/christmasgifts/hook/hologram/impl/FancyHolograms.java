package ru.mitriyf.christmasgifts.hook.hologram.impl;

import de.oliver.fancyholograms.api.FancyHologramsPlugin;
import de.oliver.fancyholograms.api.HologramManager;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.hook.hologram.HologramSupport;
import ru.mitriyf.christmasgifts.model.HologramData;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.*;

public class FancyHolograms implements HologramSupport {
    private final Map<Location, Hologram> fancyHolograms = new HashMap<>();
    private final HologramManager hologramManager;
    private final ChristmasGifts plugin;
    private final Values values;
    private final Utils utils;

    public FancyHolograms(ChristmasGifts plugin) {
        this.plugin = plugin;
        utils = plugin.getUtils();
        values = plugin.getValues();
        hologramManager = FancyHologramsPlugin.get().getHologramManager();
    }

    @Override
    public void createHologram(Player player, Location hologramLocation, Location blockLocation) {
        String hologramName = UUID.randomUUID().toString();
        if (hologramManager.getHologram(hologramName).orElse(null) != null) {
            createHologram(player, hologramLocation, blockLocation);
            return;
        }
        TextHologramData textHologramData = new TextHologramData(hologramName, hologramLocation);
        textHologramData.setText(new ArrayList<>());
        HologramData hologramData = values.getHologramDataMap().getOrDefault(utils.getLocale().player(player), values.getHologramDataMap().get(""));
        Map<Integer, String> time = hologramData.getTimeLines();
        int removeTime = values.getRemove();
        for (String text : hologramData.getLines()) {
            text = text.replace("%player%", player.getName());
            String colorized = utils.formatHologram(player, text.replace("%time%", String.valueOf(removeTime)));
            textHologramData.addLine(colorized);
        }
        textHologramData.setPersistent(false);
        Hologram hologram = hologramManager.create(textHologramData);
        hologramManager.addHologram(hologram);
        fancyHolograms.put(blockLocation, hologram);
        if (!time.isEmpty()) {
            new BukkitRunnable() {
                int timer = removeTime;

                @Override
                public void run() {
                    timer--;
                    if (timer <= 0) {
                        cancel();
                        return;
                    }
                    for (int line : time.keySet()) {
                        String lore = utils.formatHologram(player, time.get(line).replace("%time%", String.valueOf(timer)));
                        List<String> stringList = textHologramData.getText();
                        stringList.set(line, lore);
                        textHologramData.setText(stringList);
                        hologram.queueUpdate();
                    }
                }
            }.runTaskTimer(plugin, 20, 20);
        }
    }

    @Override
    public void removeHologram(Location blockLocation) {
        Hologram hologram = fancyHolograms.get(blockLocation);
        if (hologram != null) {
            hologramManager.removeHologram(hologram);
            fancyHolograms.remove(blockLocation);
        }
    }

    @Override
    public void clear() {
        for (Hologram hologram : new HashSet<>(fancyHolograms.values())) {
            hologramManager.removeHologram(hologram);
        }
        fancyHolograms.clear();
    }
}
