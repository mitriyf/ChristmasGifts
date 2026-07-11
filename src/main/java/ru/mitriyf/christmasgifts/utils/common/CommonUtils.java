package ru.mitriyf.christmasgifts.utils.common;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitScheduler;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.utils.Utils;

public class CommonUtils {
    private final BukkitScheduler scheduler;
    private final ChristmasGifts plugin;
    private final Utils utils;

    public CommonUtils(Utils utils, ChristmasGifts plugin) {
        this.utils = utils;
        this.plugin = plugin;
        scheduler = plugin.getServer().getScheduler();
    }

    public void spawnFirework(Location location) {
        location = location.clone().add(0.5, 0.5, 0.5);
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().flicker(false).trail(true).with(FireworkEffect.Type.STAR).withColor(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE).build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(0);
        firework.setFireworkMeta(fireworkMeta);
    }

    public void broadcast(String message) {
        String text = utils.formatString(message);
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(text);
        }
    }

    public void dispatchConsole(String cmd) {
        scheduler.runTaskLater(plugin, () -> plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd), 0);
    }
}
