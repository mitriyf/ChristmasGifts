package ru.mitriyf.christmasgifts;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import ru.mitriyf.christmasgifts.command.ChristmasGiftsCommand;
import ru.mitriyf.christmasgifts.hook.Supports;
import ru.mitriyf.christmasgifts.manager.GiftManager;
import ru.mitriyf.christmasgifts.service.LootService;
import ru.mitriyf.christmasgifts.updater.Updater;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public final class ChristmasGifts extends JavaPlugin {
    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final String configVersion = getDescription().getVersion();
    private GiftManager giftManager;
    private LootService lootService;
    private Supports supports;
    private int version = 13;
    private Updater updater;
    private Values values;
    private Utils utils;

    @Override
    public void onEnable() {
        getLogger().info("Support: https://vk.com/jdevs");
        tryGetServerVersion();
        values = new Values(this);
        supports = new Supports(this);
        updater = new Updater(this, values);
        utils = new Utils(this, values);
        giftManager = new GiftManager(this);
        utils.setup();
        lootService = new LootService(this);
        getCommand("christmasgifts").setExecutor(new ChristmasGiftsCommand(this));
        values.setup(true);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {
        getLogger().info("To get support for the plugin, write to the discussion from where you got it or write here: https://vk.com/jdevs");
        giftManager.close();
        supports.unregister();
    }

    private void tryGetServerVersion() {
        try {
            String[] serverVersion = getServer().getBukkitVersion().split("-")[0].split("\\.");
            String subVersion = serverVersion[1];
            if (Integer.parseInt(serverVersion[0]) > 1) {
                version = 26;
            } else if (subVersion.length() >= 2) {
                version = Integer.parseInt(subVersion.substring(0, 2));
            } else {
                version = Integer.parseInt(subVersion);
            }
        } catch (Exception e) {
            getLogger().info("Version check failed. Default set version 26. Error: " + e);
            version = 26;
        }
    }
}