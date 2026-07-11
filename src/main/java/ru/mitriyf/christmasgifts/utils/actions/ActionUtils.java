package ru.mitriyf.christmasgifts.utils.actions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.utils.Utils;

import java.util.logging.Logger;

public class ActionUtils {
    private final BukkitScheduler scheduler;
    private final ChristmasGifts plugin;
    private final Logger logger;
    private final Utils utils;

    public ActionUtils(Utils utils, ChristmasGifts plugin) {
        this.utils = utils;
        this.plugin = plugin;
        logger = plugin.getLogger();
        scheduler = plugin.getServer().getScheduler();
    }

    public void playSound(Player p, String s) {
        try {
            String[] spl = s.split(";");
            if (spl.length == 0 || spl.length > 4) {
                logger.warning("Invalid sound. [sound;volume;pitch;delay], error: " + s);
                return;
            }
            int later = spl.length == 4 ? utils.formatInt(spl[3]) : 0;
            Sound sound = Sound.valueOf(spl[0]);
            float volume = spl.length >= 2 ? utils.formatFloat(spl[1]) : 1.0F;
            float pitch = spl.length >= 3 ? utils.formatFloat(spl[2]) : 1.0F;
            scheduler.runTaskLaterAsynchronously(plugin, () -> p.playSound(p.getLocation(), sound, volume, pitch), later);
        } catch (Exception e) {
            logger.warning("Sound error: " + e);
        }
    }

    public void giveEffect(Player p, String s) {
        try {
            String[] spl = s.split(";");
            if (spl.length == 0 || spl.length > 4) {
                logger.warning("Invalid effect. [type;duration;amplifier;delay], error: " + s);
                return;
            }
            int later = spl.length == 4 ? utils.formatInt(spl[3]) : 0;
            PotionEffectType type = PotionEffectType.getByName(spl[0]);
            int duration = spl.length >= 2 ? utils.formatInt(spl[1]) : 1;
            int amplifier = spl.length >= 3 ? utils.formatInt(spl[2]) : 1;
            PotionEffect effect = new PotionEffect(type, duration, amplifier);
            scheduler.runTaskLater(plugin, () -> p.addPotionEffect(effect), later);
        } catch (Exception e) {
            logger.warning("Effect error: " + e);
        }
    }

    public void teleportPlayer(Player p, String s) {
        try {
            String[] spl = s.split(";");
            if (spl.length == 0 || spl.length > 7) {
                logger.warning("Invalid location. [world;x;y;z;yaw;pitch;delay], error: " + s);
                return;
            }
            int later = spl.length == 7 ? utils.formatInt(spl[6]) : 0;
            Server server = plugin.getServer();
            World w = server.getWorld(spl[0]);
            if (w == null) {
                w = server.getWorld("world");
            }
            double x = spl.length >= 2 ? utils.formatDouble(spl[1]) : 0;
            double y = spl.length >= 3 ? utils.formatDouble(spl[2]) : 80;
            double z = spl.length >= 4 ? utils.formatDouble(spl[3]) : 0;
            float yaw = spl.length >= 5 ? utils.formatFloat(spl[4]) : 180;
            float pitch = spl.length >= 6 ? utils.formatFloat(spl[5]) : 0;
            Location loc = new Location(w, x, y, z, yaw, pitch);
            scheduler.runTaskLater(plugin, () -> p.teleport(loc), later);
        } catch (Exception e) {
            logger.warning("Teleport error: " + e);
        }
    }

    public void createExplosion(Player p, String s) {
        try {
            String[] spl = s.split(";");
            if (spl.length == 0 || spl.length > 7) {
                logger.warning("Invalid explosion. [power;setFire;breakBlocks;delay;addX;addY;addZ], error: " + s);
                return;
            }
            int later = spl.length >= 4 ? utils.formatInt(spl[3]) : 0;
            float power = utils.formatFloat(spl[0]);
            boolean setFire = spl.length >= 2 && utils.formatBoolean(spl[1]);
            boolean breakBlocks = spl.length >= 3 && utils.formatBoolean(spl[2]);
            Location loc = p.getLocation().clone();
            double addX = spl.length >= 5 ? utils.formatDouble(spl[4]) : 0;
            double addY = spl.length >= 6 ? utils.formatDouble(spl[5]) : 0;
            double addZ = spl.length == 7 ? utils.formatDouble(spl[6]) : 0;
            loc.add(addX, addY, addZ);
            scheduler.runTaskLater(plugin, () -> p.getWorld().createExplosion(loc.getX(), loc.getY(), loc.getZ(), power, setFire, breakBlocks), later);
        } catch (Exception e) {
            logger.warning("Explosion error: " + e);
        }
    }

    @SuppressWarnings("deprecation")
    public void sendActionBar(Player p, String bar) {
        try {
            if (utils.isActionBar()) {
                logger.warning("Invalid actionbar. [message]. For 1.11+, error: " + bar);
                return;
            }
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(utils.formatString(bar)));
        } catch (Exception e) {
            logger.warning("ActionBar error: " + e);
        }
    }

    public void connect(Player p, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {
            logger.warning("A problem has been detected when sending player " + p.getName() + " to the " + server + " server");
            return;
        }
        p.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public void sendTitle(Player p, String titleS) {
        scheduler.runTaskAsynchronously(plugin, () -> {
            try {
                String[] t = titleS.split(";");
                if (t.length == 0 || t.length > 5 || utils.isTit()) {
                    logger.warning("Invalid title. [title;subtitle;int;int1;int2]. For 1.8+, error: " + utils.getTitle());
                    return;
                }
                String titleF = utils.formatString(t[0]);
                String subtitle = t.length >= 2 ? utils.formatString(t[1]) : "";
                int fadeIn = t.length >= 3 ? utils.formatInt(t[2]) : 10;
                int stay = t.length >= 4 ? utils.formatInt(t[3]) : 60;
                int fadeOut = t.length == 5 ? utils.formatInt(t[4]) : 20;
                utils.getTitle().send(p, titleF, subtitle, fadeIn, stay, fadeOut);
            } catch (Exception e) {
                logger.warning("Title error: " + e);
            }
        });

    }

    public void sendBossbar(Player p, String bossbar) {
        try {
            String[] b = bossbar.split(";");
            if (b.length == 0 || b.length > 6 || utils.isBar()) {
                logger.warning("Invalid bossbar. [message;color;type;time;style;flag]. For 1.9+, error: " + bossbar);
                return;
            }
            BarColor color = b.length >= 2 ? BarColor.valueOf(b[1].toUpperCase()) : BarColor.WHITE;
            String type = b.length >= 3 ? b[2].toLowerCase() : "time";
            long time = b.length >= 4 ? utils.formatInt(b[3]) : 5;
            long ticks = time * 20L;
            BarStyle style = b.length >= 5 ? BarStyle.valueOf(b[4].toUpperCase()) : BarStyle.SEGMENTED_6;
            BarFlag flag = b.length == 6 ? BarFlag.valueOf(b[5].toUpperCase()) : null;
            boolean update = b[0].contains("%time%");
            String text = utils.formatString(b[0].replace("%time%", time + ""));
            BossBar bossBar = flag == null ? plugin.getServer().createBossBar(text, color, style) : plugin.getServer().createBossBar(text, color, style, flag);
            bossBar.addPlayer(p);
            if (type.equalsIgnoreCase("stop")) {
                scheduler.runTaskLaterAsynchronously(plugin, () -> {
                    bossBar.removeAll();
                    bossBar.setVisible(false);
                }, ticks);
            } else {
                new BukkitRunnable() {
                    private int t = 0;

                    @Override
                    public void run() {
                        t++;
                        if (t == ticks) {
                            bossBar.removeAll();
                            bossBar.setVisible(false);
                            cancel();
                            return;
                        }
                        int left = (int) (time - Math.floor((double) t / 20));
                        if (update) {
                            bossBar.setTitle(utils.formatString(b[0].replace("%time%", left + "")));
                        }
                        bossBar.setProgress(1 - ((double) t / ticks));
                    }
                }.runTaskTimerAsynchronously(plugin, 1, 1);
            }
        } catch (Exception e) {
            logger.warning("Bossbar error: " + e);
        }
    }

    public void sendParticle(Player p, String particle) {
        scheduler.runTaskAsynchronously(plugin, () -> {
            try {
                String[] part = particle.split(";");
                if (part.length == 0 || part.length > 4 || utils.isBar()) {
                    logger.warning("Invalid particle. [type;amount;r.g.b;radius]. For 1.9+, error: " + particle);
                    return;
                }
                String type = part[0];
                int amount = part.length >= 2 ? Integer.parseInt(part[1]) : 0;
                String[] rgb = part.length <= 3 ? part[2].split("\\.") : null;
                int r = 255, g = 255, b = 255;
                if (rgb != null && rgb.length == 3) {
                    r = Integer.parseInt(rgb[0]);
                    g = Integer.parseInt(rgb[1]);
                    b = Integer.parseInt(rgb[2]);
                }
                double radius = part.length == 4 ? Double.parseDouble(part[3]) : 1;
                Location loc = p.getLocation().clone();
                loc.add(0.5, 0, 0.5);
                if (type.equalsIgnoreCase("one")) {
                    loc.add(0, 0.5, 0);
                    spawnParticle(loc, amount, loc.getX(), loc.getZ(), r, g, b);
                } else {
                    loc.add(0, 0.1, 0);
                    for (double t = 0; t <= 4 * Math.PI * radius; t += 0.1) {
                        double x = radius * Math.cos(t) + loc.getX();
                        double z = loc.getZ() + radius * Math.sin(t);
                        spawnParticle(loc, amount, x, z, r, g, b);
                    }
                }
            } catch (Exception e) {
                logger.warning("Particle error: " + e);
            }
        });
    }

    private void spawnParticle(Location location, int amount, double x, double z, int r, int g, int b) {
        utils.getParticleSpawn().create(location.getWorld(), amount, x, location.getY(), z, r, g, b);
    }

    public void dispatchPlayer(Player p, String cmd) {
        scheduler.runTaskLater(plugin, () -> p.chat(cmd), 0);
    }
}
