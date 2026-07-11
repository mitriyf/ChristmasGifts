package ru.mitriyf.christmasgifts.utils.worldguard.impl;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.entity.Player;
import ru.mitriyf.christmasgifts.utils.worldguard.WorldGuardMode;

public class WorldGuardMode1 implements WorldGuardMode {
    @Override
    public boolean checkRegion(ApplicableRegionSet regionSet, Player player) {
        return true;
    }
}
