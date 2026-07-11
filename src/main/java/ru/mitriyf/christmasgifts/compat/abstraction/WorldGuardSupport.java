package ru.mitriyf.christmasgifts.compat.abstraction;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.World;

public interface WorldGuardSupport {
    RegionManager getRegionManager(World world);

    ApplicableRegionSet getRegionSet(RegionManager regionManager, Location blockLocation);
}
