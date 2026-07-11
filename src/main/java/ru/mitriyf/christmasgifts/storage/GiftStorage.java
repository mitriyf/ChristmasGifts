package ru.mitriyf.christmasgifts.storage;

import org.bukkit.Location;
import org.bukkit.block.Block;

public interface GiftStorage {
    void setBlock(Location location, Block block);

    void put(Location location, Block block);

    void unregister();
}
