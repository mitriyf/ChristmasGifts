package ru.mitriyf.christmasgifts.storage.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.storage.GiftStorage;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public class GiftStorageVersion12 implements GiftStorage {
    private final Map<Location, Map.Entry<Material, Byte>> saveBlocks = new HashMap<>();
    private final Class<?>[] parameters = new Class<?>[]{byte.class};
    private final Values values;
    private final Logger logger;
    private final Utils utils;

    public GiftStorageVersion12(ChristmasGifts plugin) {
        utils = plugin.getUtils();
        values = plugin.getValues();
        logger = plugin.getLogger();
    }

    public void setBlock(Location location, Block block) {
        Map.Entry<Material, Byte> savedBlock = saveBlocks.get(location);
        if (savedBlock == null) {
            return;
        }
        Material mt = savedBlock.getKey();
        block.setType(mt);
        try {
            Object[] args = new Object[]{savedBlock.getValue()};
            block.getClass().getMethod("setData", parameters).invoke(block, args);
        } catch (Exception e) {
            logger.warning("An error occurred when executing setBlock. Error: " + e);
        }
        block.getState().update();
        saveBlocks.remove(location);
    }

    public void put(Location location, Block block) {
        try {
            Method method = ((Object) block).getClass().getMethod("getData");
            saveBlocks.put(location, new AbstractMap.SimpleEntry<>(block.getType(), (Byte) method.invoke(block)));
        } catch (Exception e) {
            logger.warning("An error occurred when executing setData. Error: " + e);
        }
        block.setType(values.getGiftMaterial());
        if (values.isSkull()) {
            Skull skull = (Skull) block.getState();
            skull.setSkullType(SkullType.PLAYER);
            skull.setRawData((byte) values.getGiftId());
            utils.setSkin(skull, values.getTexture());
        }
    }

    public void unregister() {
        if (saveBlocks.isEmpty()) {
            return;
        }
        for (Location loc : new HashSet<>(saveBlocks.keySet())) {
            setBlock(loc, loc.getBlock());
        }
    }
}
