package ru.mitriyf.christmasgifts.hook.hologram;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface HologramSupport {
    void createHologram(Player player, Location hologramLocation, Location blockLocation);

    void removeHologram(Location blockLocation);

    void clear();
}
