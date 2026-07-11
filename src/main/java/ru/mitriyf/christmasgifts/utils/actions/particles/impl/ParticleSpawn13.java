package ru.mitriyf.christmasgifts.utils.actions.particles.impl;

import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import ru.mitriyf.christmasgifts.utils.actions.particles.ParticleSpawn;

public class ParticleSpawn13 implements ParticleSpawn {
    @Override
    public void create(World world, int amount, double x, double y, double z, int r, int g, int b) {
        world.spawnParticle(Particle.REDSTONE, x, y, z, amount, 0, 0, 0, getDust(r, g, b));
    }

    private Particle.DustOptions getDust(int r, int g, int b) {
        return new Particle.DustOptions(Color.fromRGB(r, g, b), 1);
    }
}
