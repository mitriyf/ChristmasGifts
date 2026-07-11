package ru.mitriyf.christmasgifts.storage.impl;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.block.data.BlockData;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.storage.GiftStorage;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GiftStorageVersion13 implements GiftStorage {
    private final Map<Location, BlockData> saveBlocks = new HashMap<>();
    private final Values values;
    private final Utils utils;

    public GiftStorageVersion13(ChristmasGifts plugin) {
        utils = plugin.getUtils();
        values = plugin.getValues();
    }

    public void setBlock(Location block, Block b) {
        if (saveBlocks.get(block) != null) {
            BlockData blockData = saveBlocks.get(block);
            b.setBlockData(blockData);
            saveBlocks.remove(b.getLocation());
        }
    }

    public void put(Location location, Block block) {
        saveBlocks.put(location, block.getBlockData());
        block.setType(values.getGiftMaterial());
        if (values.isSkull()) {
            utils.setSkin((Skull) block.getState(), values.getTexture());
        }
    }

    public void unregister() {
        if (saveBlocks.isEmpty()) {
            return;
        }
        for (Location loc : new HashSet<>(saveBlocks.keySet())) {
            if (saveBlocks.get(loc) != null) {
                setBlock(loc, loc.getBlock());
            }
        }
    }
}
