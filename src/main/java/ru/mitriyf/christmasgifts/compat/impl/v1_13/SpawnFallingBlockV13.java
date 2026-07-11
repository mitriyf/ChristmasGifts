package ru.mitriyf.christmasgifts.compat.impl.v1_13;

import org.bukkit.Location;
import org.bukkit.entity.FallingBlock;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.compat.abstraction.SpawnFallingBlock;
import ru.mitriyf.christmasgifts.values.Values;

public class SpawnFallingBlockV13 implements SpawnFallingBlock {
    private final Values values;

    public SpawnFallingBlockV13(ChristmasGifts plugin) {
        values = plugin.getValues();
    }

    @Override
    public FallingBlock spawn(String playerName, Location location) {
        FallingBlock fallingBlock = location.getWorld().spawnFallingBlock(location, values.getMaterial().createBlockData());
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);
        return fallingBlock;
    }
}
