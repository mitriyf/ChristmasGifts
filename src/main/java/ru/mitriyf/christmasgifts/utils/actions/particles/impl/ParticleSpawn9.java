package ru.mitriyf.christmasgifts.utils.actions.particles.impl;

import org.bukkit.Particle;
import org.bukkit.World;
import ru.mitriyf.christmasgifts.utils.actions.particles.ParticleSpawn;

public class ParticleSpawn9 implements ParticleSpawn {
    @Override
    public void create(World world, int amount, double x, double y, double z, int r, int g, int b) {
        world.spawnParticle(Particle.REDSTONE, x, y, z, 0, getColor(r), getColor(g), getColor(b), 1);
    }

    private double getColor(int color) {
        return (double) (color + 1) / 255;
    }
}
