package ru.mitriyf.christmasgifts.utils.worldguard.impl;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import org.bukkit.entity.Player;
import ru.mitriyf.christmasgifts.utils.worldguard.WorldGuardMode;

public class WorldGuardMode2 implements WorldGuardMode {
    @Override
    public boolean checkRegion(ApplicableRegionSet regionSet, Player player) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (!regionSet.isMemberOfAll(localPlayer)) {
            return regionSet.isOwnerOfAll(localPlayer);
        }
        return true;
    }
}
