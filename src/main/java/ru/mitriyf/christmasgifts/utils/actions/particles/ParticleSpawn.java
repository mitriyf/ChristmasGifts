package ru.mitriyf.christmasgifts.utils.actions.particles;

import org.bukkit.World;

public interface ParticleSpawn {
    void create(World world, int amount, double x, double y, double z, int r, int g, int b);
}
