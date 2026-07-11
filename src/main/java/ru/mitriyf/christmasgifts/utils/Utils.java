package ru.mitriyf.christmasgifts.utils;

import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.compat.abstraction.HandItem;
import ru.mitriyf.christmasgifts.compat.abstraction.PistonHandler;
import ru.mitriyf.christmasgifts.compat.abstraction.SkullBase;
import ru.mitriyf.christmasgifts.compat.abstraction.SpawnFallingBlock;
import ru.mitriyf.christmasgifts.compat.impl.v1_12.SkullBaseV12;
import ru.mitriyf.christmasgifts.compat.impl.v1_13.SkullBaseV13;
import ru.mitriyf.christmasgifts.compat.impl.v1_13.SpawnFallingBlockV13;
import ru.mitriyf.christmasgifts.compat.impl.v1_7.PistonHandlerV7;
import ru.mitriyf.christmasgifts.compat.impl.v1_7.SkullOwnerV7;
import ru.mitriyf.christmasgifts.compat.impl.v1_7.SpawnFallingBlockV7;
import ru.mitriyf.christmasgifts.compat.impl.v1_8.HandItemV8;
import ru.mitriyf.christmasgifts.compat.impl.v1_8.PistonHandlerV8;
import ru.mitriyf.christmasgifts.compat.impl.v1_8.SpawnFallingBlockV8;
import ru.mitriyf.christmasgifts.compat.impl.v1_9.HandItemV9;
import ru.mitriyf.christmasgifts.manager.GiftManager;
import ru.mitriyf.christmasgifts.storage.GiftStorage;
import ru.mitriyf.christmasgifts.storage.impl.GiftStorageVersion12;
import ru.mitriyf.christmasgifts.storage.impl.GiftStorageVersion13;
import ru.mitriyf.christmasgifts.utils.actions.Action;
import ru.mitriyf.christmasgifts.utils.actions.ActionType;
import ru.mitriyf.christmasgifts.utils.actions.ActionUtils;
import ru.mitriyf.christmasgifts.utils.actions.particles.ParticleSpawn;
import ru.mitriyf.christmasgifts.utils.actions.particles.impl.ParticleSpawn13;
import ru.mitriyf.christmasgifts.utils.actions.particles.impl.ParticleSpawn9;
import ru.mitriyf.christmasgifts.utils.actions.titles.Title;
import ru.mitriyf.christmasgifts.utils.actions.titles.impl.Title10;
import ru.mitriyf.christmasgifts.utils.actions.titles.impl.Title11;
import ru.mitriyf.christmasgifts.utils.common.CommonUtils;
import ru.mitriyf.christmasgifts.utils.locales.Locale;
import ru.mitriyf.christmasgifts.utils.locales.impl.Locale12;
import ru.mitriyf.christmasgifts.utils.locales.impl.Locale13;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Getter
public class Utils {
    private final Values values;
    private final Logger logger;
    private final CountDownLatch latch;
    private final ChristmasGifts plugin;
    private final CommonUtils commonUtils;
    private final ActionUtils actionUtils;
    private final Set<Integer> tasks = new HashSet<>();
    private SkullBase skullBase;
    private SkullOwnerV7 skullOwner;
    private boolean actionBar = false, bar = false, tit = false;
    private SpawnFallingBlock spawnFallingBlock;
    private PistonHandler pistonHandler;
    private ParticleSpawn particleSpawn;
    private GiftStorage giftStorage;
    private HandItem handItem;
    private Locale locale;
    private Title title;

    public Utils(ChristmasGifts plugin, Values values) {
        this.plugin = plugin;
        this.values = values;
        logger = plugin.getLogger();
        latch = new CountDownLatch(1);
        actionUtils = new ActionUtils(this, plugin);
        commonUtils = new CommonUtils(this, plugin);
    }

    public void setup() {
        skullOwner = new SkullOwnerV7();
        int version = plugin.getVersion();
        boolean minVersion = version < 13;
        if (minVersion) {
            giftStorage = new GiftStorageVersion12(plugin);
            skullBase = new SkullBaseV12(plugin);
            locale = new Locale12();
        } else {
            spawnFallingBlock = new SpawnFallingBlockV13(plugin);
            giftStorage = new GiftStorageVersion13(plugin);
            skullBase = new SkullBaseV13(plugin);
            particleSpawn = new ParticleSpawn13();
            locale = new Locale13();
        }
        minVersion = minVersion && version < 11;
        if (minVersion) {
            actionBar = true;
            title = new Title10();
        } else {
            title = new Title11();
        }
        minVersion = minVersion && version < 9;
        if (minVersion) {
            bar = true;
            handItem = new HandItemV8();
        } else {
            particleSpawn = new ParticleSpawn9();
            handItem = new HandItemV9();
        }
        minVersion = minVersion && version < 8;
        GiftManager giftManager = plugin.getGiftManager();
        if (minVersion) {
            spawnFallingBlock = new SpawnFallingBlockV7(plugin);
            pistonHandler = new PistonHandlerV7(giftManager);
            skullBase = skullOwner;
            tit = true;
        } else {
            pistonHandler = new PistonHandlerV8(giftManager);
            if (spawnFallingBlock == null) {
                spawnFallingBlock = new SpawnFallingBlockV8(plugin);
            }
        }
    }

    public void sendMessage(CommandSender sender, Map<String, List<Action>> actions) {
        new BukkitRunnable() {
            @Override
            public void run() {
                tasks.add(getTaskId());
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    for (Action action : actions.getOrDefault(locale.player(p), actions.get(""))) {
                        if (!tasks.contains(getTaskId())) {
                            return;
                        }
                        sendPlayer(p, action);
                    }
                    return;
                }
                for (Action action : actions.get("")) {
                    if (!tasks.contains(getTaskId())) {
                        return;
                    }
                    sendSender(sender, action);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void sendPlayer(Player p, Action action) {
        ActionType type = action.getType();
        String context = action.getContext().replace("%player%", p.getName()).replace("%world%", p.getWorld().getName());
        if (values.isPlaceholderAPI()) {
            context = PlaceholderAPI.setPlaceholders(p, context);
        }
        switch (type) {
            case PLAYER: {
                actionUtils.dispatchPlayer(p, context);
                break;
            }
            case TELEPORT: {
                actionUtils.teleportPlayer(p, context);
                break;
            }
            case CONSOLE: {
                commonUtils.dispatchConsole(context);
                break;
            }
            case ACTIONBAR: {
                actionUtils.sendActionBar(p, context);
                break;
            }
            case CONNECT: {
                actionUtils.connect(p, context);
                break;
            }
            case BOSSBAR: {
                actionUtils.sendBossbar(p, context);
                break;
            }
            case PARTICLE: {
                actionUtils.sendParticle(p, context);
                break;
            }
            case BROADCAST: {
                commonUtils.broadcast(context);
                break;
            }
            case TITLE: {
                actionUtils.sendTitle(p, context);
                break;
            }
            case SOUND: {
                actionUtils.playSound(p, context);
                break;
            }
            case EFFECT: {
                actionUtils.giveEffect(p, context);
                break;
            }
            case EXPLOSION: {
                actionUtils.createExplosion(p, context);
                break;
            }
            case LOG: {
                log(context);
                break;
            }
            case DELAY: {
                try {
                    if (latch.await(Integer.parseInt(context) * 50L, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                } catch (Exception ignored) {
                }
                break;
            }
            default: {
                sendMessage(p, context);
                break;
            }
        }
    }

    private void sendSender(CommandSender sender, Action action) {
        ActionType type = action.getType();
        String context = action.getContext();
        switch (type) {
            case CONSOLE: {
                commonUtils.dispatchConsole(context);
                break;
            }
            case BROADCAST: {
                commonUtils.broadcast(context);
                break;
            }
            case LOG: {
                log(context);
                break;
            }
            case DELAY: {
                try {
                    if (latch.await(Integer.parseInt(context) * 50L, TimeUnit.MILLISECONDS)) {
                        break;
                    }
                } catch (Exception ignored) {
                }
                break;
            }
            case PLAYER:
            case TITLE:
            case ACTIONBAR:
            case BOSSBAR:
            case PARTICLE:
            case EFFECT:
            case TELEPORT:
            case SOUND:
            case CONNECT:
            case EXPLOSION:
                break;
            default:
                sendMessage(sender, context);
                break;
        }
    }

    public void setSkin(Skull skull, String skin) {
        if (skin != null) {
            if (skin.length() > 16) {
                skullBase.setSkin(skull, skin);
            } else {
                skullOwner.setSkin(skull, skin);
            }
        }
    }

    public String formatHologram(Player player, String context) {
        if (values.isPlaceholderAPI()) {
            context = PlaceholderAPI.setPlaceholders(player, context);
        }
        return formatString(context);
    }

    private void sendMessage(CommandSender sender, String text) {
        sender.sendMessage(formatString(text));
    }

    private void sendMessage(Player player, String text) {
        player.sendMessage(formatString(text));
    }

    public String formatString(String s) {
        return values.getColorizer().colorize(s);
    }

    public Float formatFloat(String s) {
        return Float.parseFloat(s);
    }

    public int formatInt(String s) {
        return Integer.parseInt(s);
    }

    public double formatDouble(String s) {
        return Double.parseDouble(s);
    }

    public boolean formatBoolean(String s) {
        return Boolean.parseBoolean(s);
    }

    private void log(String log) {
        logger.info(log);
    }
}
