package ru.mitriyf.christmasgifts.compat.impl.v1_7;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.compat.abstraction.SpawnFallingBlock;
import ru.mitriyf.christmasgifts.values.Values;

import java.lang.reflect.Method;

public class SpawnFallingBlockV7 implements SpawnFallingBlock {
    private final Class<?>[] parameters = new Class<?>[]{Location.class, Material.class, byte.class};
    private final ChristmasGifts plugin;
    private final Values values;

    public SpawnFallingBlockV7(ChristmasGifts plugin) {
        this.plugin = plugin;
        values = plugin.getValues();
    }

    @Override
    public FallingBlock spawn(String playerName, Location location) {
        try {
            World world = location.getWorld();
            Method methods = world.getClass().getMethod("spawnFallingBlock", parameters);
            Object[] args = new Object[]{location, values.getMaterial(), (byte) values.getId()};
            FallingBlock fallingBlock = (FallingBlock) methods.invoke(world, args);
            fallingBlock.setDropItem(false);
            return fallingBlock;
        } catch (Exception e) {
            plugin.getLogger().warning("Error spawnFallingBlock: " + e);
        }
        return null;
    }
}