package ru.mitriyf.christmasgifts.utils.worldguard.impl;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.entity.Player;
import ru.mitriyf.christmasgifts.utils.worldguard.WorldGuardMode;

public class WorldGuardMode3 implements WorldGuardMode {
    @Override
    public boolean checkRegion(ApplicableRegionSet regionSet, Player player) {
        return regionSet.getRegions().isEmpty();
    }
}
