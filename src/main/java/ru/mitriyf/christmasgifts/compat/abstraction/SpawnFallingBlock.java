package ru.mitriyf.christmasgifts.compat.abstraction;

import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;

public interface SpawnFallingBlock {
    FallingBlock spawn(String playerName, Location location);
}
