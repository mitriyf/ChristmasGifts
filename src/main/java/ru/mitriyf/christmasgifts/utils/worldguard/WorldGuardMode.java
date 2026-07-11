package ru.mitriyf.christmasgifts.utils.worldguard;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.entity.Player;

public interface WorldGuardMode {
    boolean checkRegion(ApplicableRegionSet regionSet, Player player);
}
