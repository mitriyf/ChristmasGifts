package ru.mitriyf.christmasgifts.compat.impl.v1_12;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.World;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.compat.abstraction.WorldGuardSupport;

import java.lang.reflect.Method;

public class WorldGuardSupportV12 implements WorldGuardSupport {
    private final Class<?>[] parameters_regions = new Class<?>[]{Location.class};
    private final ChristmasGifts plugin;
    private Method getRegionManager;

    public WorldGuardSupportV12(ChristmasGifts plugin) {
        this.plugin = plugin;
        try {
            getRegionManager = WorldGuardPlugin.inst().getClass().getMethod("getRegionManager", World.class);
        } catch (Exception e) {
            plugin.getLogger().warning("No method getRegionManager for WorldGuard was found. Error: " + e);
        }
    }

    @Override
    @SuppressWarnings("all")
    public ApplicableRegionSet getRegionSet(RegionManager regionManager, Location blockLocation) {
        try {
            Method getApplicableRegions = regionManager.getClass().getMethod("getApplicableRegions", parameters_regions);
            return (ApplicableRegionSet) getApplicableRegions.invoke(regionManager, blockLocation);
        } catch (Exception e) {
            plugin.getLogger().warning("An error occurred while retrieving getRegionSet. Error: " + e);
        }
        return null;
    }

    @Override
    public RegionManager getRegionManager(World world) {
        try {
            return (RegionManager) getRegionManager.invoke(WorldGuardPlugin.inst(), world);
        } catch (Exception e) {
            plugin.getLogger().warning("An error occurred while retrieving getRegionManager. Error: " + e);
        }
        return null;
    }
}
