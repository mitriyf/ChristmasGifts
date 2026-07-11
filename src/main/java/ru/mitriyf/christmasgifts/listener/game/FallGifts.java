package ru.mitriyf.christmasgifts.listener.game;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.compat.abstraction.WorldGuardSupport;
import ru.mitriyf.christmasgifts.hook.Supports;
import ru.mitriyf.christmasgifts.hook.hologram.HologramSupport;
import ru.mitriyf.christmasgifts.manager.GiftManager;
import ru.mitriyf.christmasgifts.model.FallingData;
import ru.mitriyf.christmasgifts.model.GiftData;
import ru.mitriyf.christmasgifts.model.LocationData;
import ru.mitriyf.christmasgifts.storage.GiftStorage;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class FallGifts implements Listener {
    private final GiftManager giftManager;
    private final GiftStorage giftStorage;
    private final ChristmasGifts plugin;
    private final Supports supports;
    private final Values values;
    private final Utils utils;

    public FallGifts(ChristmasGifts plugin) {
        this.plugin = plugin;
        utils = plugin.getUtils();
        values = plugin.getValues();
        supports = plugin.getSupports();
        giftStorage = utils.getGiftStorage();
        giftManager = plugin.getGiftManager();
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof FallingBlock)) {
            return;
        }
        UUID entityUuid = entity.getUniqueId();
        SpawnGifts spawnGifts = values.getSpawnGifts();
        Map<UUID, FallingData> fallingDataMap = spawnGifts.getFallingDataMap();
        FallingData fallingData = fallingDataMap.remove(entityUuid);
        if (fallingData == null) {
            return;
        }
        fallingData.getTask().cancel();
        event.setCancelled(true);
        Player player = plugin.getServer().getPlayer(fallingData.getPlayerUuid());
        if (player == null || !player.isOnline()) {
            return;
        }
        String playerName = player.getName();
        if (!fallingData.isForced() && spawnGifts.isLimited(playerName)) {
            return;
        }
        World world = entity.getWorld();
        Block block = entity.getLocation().getBlock();
        Location blockLocation = block.getLocation();
        if (values.isWorldGuard() && !checkWorldGuard(blockLocation, player)) {
            return;
        }
        if (values.isFirework()) {
            utils.getCommonUtils().spawnFirework(blockLocation);
        }
        giftManager.getGifts().put(blockLocation, new GiftData(player.getUniqueId(), createTask(world, block, blockLocation, player)));
        String locale = utils.getLocale().player(player);
        boolean autoGive = values.isAutoGive();
        startValues(world, blockLocation, player, playerName, autoGive);
        if (autoGive) {
            return;
        }
        giftStorage.put(blockLocation, block);
        HologramSupport hologramSupport = supports.getHologramSupport();
        if (hologramSupport != null) {
            Map<String, LocationData> locationDataMap = values.getAddLocation();
            LocationData addLocation = locationDataMap.getOrDefault(locale, locationDataMap.get(""));
            hologramSupport.createHologram(player, blockLocation.clone().add(addLocation.getAddX(), addLocation.getAddY(), addLocation.getAddZ()), blockLocation);
        }
    }

    private boolean checkWorldGuard(Location blockLocation, Player player) {
        WorldGuardSupport worldGuardSupport = supports.getWorldGuardSupport();
        RegionManager regionManager = worldGuardSupport.getRegionManager(blockLocation.getWorld());
        ApplicableRegionSet regionSet = worldGuardSupport.getRegionSet(regionManager, blockLocation);
        return values.getWorldGuardMode().checkRegion(regionSet, player);
    }

    private void startValues(World world, Location blockLocation, Player player, String playerName, boolean autoGive) {
        if (values.isLimit()) {
            Map<String, Integer> players = giftManager.getLimitGifts();
            players.put(playerName, players.getOrDefault(playerName, 0) + 1);
            if (values.isOnCrashes()) {
                values.saveDatabase();
            }
        }
        utils.sendMessage(player, values.getStartGifts());
        if (autoGive) {
            plugin.getLootService().dropLoot(world, blockLocation);
            giftManager.getGifts().remove(blockLocation);
            return;
        }
        if (values.isOnCrashes()) {
            FileConfiguration database = values.getDatabase();
            List<String> giftsCopy = new ArrayList<>();
            List<String> gifts = database.getStringList("gifts");
            if (gifts != null) {
                giftsCopy.addAll(gifts);
            }
            giftsCopy.add(blockLocation.getWorld().getName() + ":" + blockLocation.getX() + ":" + blockLocation.getY() + ":" + blockLocation.getZ() + ":" + playerName);
            database.set("gifts", giftsCopy);
            try {
                database.save(new File(plugin.getDataFolder(), "storage/db.yml"));
            } catch (Exception e) {
                plugin.getLogger().warning("Function isOnCrashes error: " + e);
            }
        }
    }

    private BukkitTask createTask(World world, Block block, Location blockLocation, Player player) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                Map<Location, GiftData> giftDataMap = giftManager.getGifts();
                if (giftDataMap.containsKey(blockLocation)) {
                    String playerName = player.getName();
                    if (values.isTakedLoot()) {
                        plugin.getLootService().dropLoot(world, blockLocation);
                    } else {
                        Map<String, Integer> players = giftManager.getLimitGifts();
                        players.put(playerName, players.get(playerName) - 1);
                        if (values.isOnCrashes()) {
                            values.saveDatabase();
                        }
                    }
                    giftStorage.setBlock(blockLocation, block);
                    giftDataMap.remove(blockLocation);
                    HologramSupport hologramSupport = supports.getHologramSupport();
                    if (hologramSupport != null) {
                        hologramSupport.removeHologram(blockLocation);
                    }
                    giftManager.removeOnCrashes(blockLocation, playerName);
                    utils.sendMessage(player, values.getStopGifts());
                }
                cancel();
            }
        }.runTaskLater(plugin, values.getRemove() * 20L);
    }
}
