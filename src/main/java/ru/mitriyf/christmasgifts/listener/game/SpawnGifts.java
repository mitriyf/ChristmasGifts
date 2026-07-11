package ru.mitriyf.christmasgifts.listener.game;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.model.FallingData;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public final class SpawnGifts implements Listener {
    @Getter
    private final Map<UUID, FallingData> fallingDataMap = new HashMap<>();
    private final Set<BukkitTask> tasks = new HashSet<>();
    private final Set<UUID> time = new HashSet<>();
    private final ThreadLocalRandom random;
    private final ChristmasGifts plugin;
    private final Values values;
    private final Utils utils;
    @Setter
    private boolean disabledMode;

    public SpawnGifts(ChristmasGifts plugin) {
        this.plugin = plugin;
        utils = plugin.getUtils();
        values = plugin.getValues();
        random = plugin.getRandom();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (disabledMode) {
            return;
        }
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Location location = event.getTo();
        Location fromLocation = event.getFrom();
        if (checkCoords(fromLocation, location) || time.contains(uuid)) {
            return;
        }
        if (values.getProcent() >= random.nextInt(values.getAllProcent())) {
            spawnFallingBlock(player, false);
        } else {
            time.add(uuid);
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> time.remove(uuid), values.getEvery());
        }
    }

    public void createEveryTimeTask(int people, int seconds) {
        if (people == -1) {
            tasks.add(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    spawnFallingBlock(player, false);
                }
            }, seconds * 20L, seconds * 20L));
        } else {
            tasks.add(new BukkitRunnable() {
                @Override
                public void run() {
                    Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
                    if (onlinePlayers.size() >= people) {
                        List<Player> players = new ArrayList<>(onlinePlayers);
                        Player player = players.get(random.nextInt(players.size()));
                        for (int i = 0; i < people; i++) {
                            spawnFallingBlock(player, false);
                        }
                    }
                }
            }.runTaskTimer(plugin, seconds * 20L, seconds * 20L));
        }
    }

    public void spawnFallingBlock(Player player, boolean forced) {
        String playerName = player.getName();
        Location playerLocation = player.getLocation();
        Location location = searchLocation(playerLocation, playerLocation.getYaw());
        if (!forced && (checkRules(location.getWorld(), location.getBlock().getBiome()) || isLimited(playerName) || (values.isFly() && player.isFlying()) || (values.isShift() && player.isSneaking()))) {
            return;
        }
        FallingBlock fallingBlock = utils.getSpawnFallingBlock().spawn(playerName, location);
        int ticksLived = values.getTimeLived() * 20;
        fallingBlock.setTicksLived(ticksLived);
        UUID uuid = fallingBlock.getUniqueId();
        fallingDataMap.put(uuid, new FallingData(player.getUniqueId(), plugin.getServer().getScheduler().runTaskLater(plugin, () -> fallingDataMap.remove(uuid), ticksLived), forced));
    }

    private Location searchLocation(Location location, float yaw) {
        yaw = (yaw % 360 + 360) % 360;
        double x = 0, z = 0;
        double distance = values.getLocationDistance();
        if (yaw >= 316 || yaw <= 45) {
            z = z + distance;
        } else if (226 <= yaw && yaw <= 315) {
            x = x + distance;
        } else if (46 <= yaw && 135 >= yaw) {
            x = x - distance;
        } else if (136 <= yaw && yaw <= 225) {
            z = z - distance;
        }
        return location.clone().add(x, values.getLocationHeight(), z);
    }

    public void stopTasks() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
        tasks.clear();
    }

    public boolean isLimited(String playerName) {
        return values.isLimit() && values.getMax() <= plugin.getGiftManager().getLimitGifts().getOrDefault(playerName, 0);
    }

    private boolean checkCoords(Location fromLocation, Location location) {
        return fromLocation.getX() == location.getX() && fromLocation.getY() == location.getY() && fromLocation.getZ() == location.getZ();
    }

    private boolean checkRules(World world, Biome biome) {
        return values.getWorldType().notContainsWorld(world) || values.getBiomeType().notContainsBiome(biome);
    }
}
