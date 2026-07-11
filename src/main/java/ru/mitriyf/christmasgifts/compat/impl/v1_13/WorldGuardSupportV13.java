package ru.mitriyf.christmasgifts.compat.impl.v1_13;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.World;
import ru.mitriyf.christmasgifts.compat.abstraction.WorldGuardSupport;

public class WorldGuardSupportV13 implements WorldGuardSupport {
    @Override
    public ApplicableRegionSet getRegionSet(RegionManager regionManager, Location blockLocation) {
        BlockVector3 vec = BlockVector3.at(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ());
        return regionManager.getApplicableRegions(vec);
    }

    @Override
    public RegionManager getRegionManager(World world) {
        return WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(world));
    }
}
