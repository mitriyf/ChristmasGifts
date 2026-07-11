package ru.mitriyf.christmasgifts.filter.world.impl;

import org.bukkit.World;
import org.bukkit.block.Biome;
import ru.mitriyf.christmasgifts.filter.world.BiomesList;
import ru.mitriyf.christmasgifts.filter.world.WorldsList;
import ru.mitriyf.christmasgifts.values.Values;

public class AllowedWorlds implements WorldsList, BiomesList {
    private final Values values;

    public AllowedWorlds(Values values) {
        this.values = values;
    }

    @Override
    public boolean notContainsWorld(World world) {
        return !values.getWorlds().contains(world);
    }

    @Override
    public boolean notContainsBiome(Biome biome) {
        return !values.getBiomes().contains(biome);
    }
}
