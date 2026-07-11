package ru.mitriyf.christmasgifts.values;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.filter.world.BiomesList;
import ru.mitriyf.christmasgifts.filter.world.WorldsList;
import ru.mitriyf.christmasgifts.filter.world.impl.AllowedWorlds;
import ru.mitriyf.christmasgifts.filter.world.impl.BlockedWorlds;
import ru.mitriyf.christmasgifts.hook.Supports;
import ru.mitriyf.christmasgifts.listener.game.FallGifts;
import ru.mitriyf.christmasgifts.listener.game.SpawnGifts;
import ru.mitriyf.christmasgifts.listener.game.UseGifts;
import ru.mitriyf.christmasgifts.listener.safe.SafeListener;
import ru.mitriyf.christmasgifts.listener.safe.SafeListenerVersion8;
import ru.mitriyf.christmasgifts.model.HologramData;
import ru.mitriyf.christmasgifts.model.LocationData;
import ru.mitriyf.christmasgifts.updater.Updater;
import ru.mitriyf.christmasgifts.utils.actions.Action;
import ru.mitriyf.christmasgifts.utils.actions.ActionType;
import ru.mitriyf.christmasgifts.utils.colors.Colorizer;
import ru.mitriyf.christmasgifts.utils.colors.impl.LegacyColorizer;
import ru.mitriyf.christmasgifts.utils.colors.impl.MiniMessageColorizer;
import ru.mitriyf.christmasgifts.utils.worldguard.WorldGuardMode;
import ru.mitriyf.christmasgifts.utils.worldguard.impl.WorldGuardMode1;
import ru.mitriyf.christmasgifts.utils.worldguard.impl.WorldGuardMode2;
import ru.mitriyf.christmasgifts.utils.worldguard.impl.WorldGuardMode3;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public class Values {
    private final Logger logger;
    private final Updater updater;
    private final ChristmasGifts plugin;
    private final PluginManager pluginManager;
    private final Set<Biome> biomes = new HashSet<>();
    private final Set<World> worlds = new HashSet<>();
    private final File dataFolder, dbFile, configFile, lootFile;
    private final Map<String, List<Action>> stopGifts = new HashMap<>();
    private final String[] lcs = new String[]{"de_DE", "en_US", "ru_RU"};
    private final Map<String, List<Action>> successful = new HashMap<>();
    private final Map<String, List<Action>> startGifts = new HashMap<>();
    private final Map<String, LocationData> addLocation = new HashMap<>();
    private final Map<String, List<Action>> noPermission = new HashMap<>();
    private final Map<String, List<Action>> limit_messages = new HashMap<>();
    private final Map<String, HologramData> hologramDataMap = new HashMap<>();
    private final Pattern actionPattern = Pattern.compile("\\[(\\w+)] ?(.*)");
    private boolean locale, onCrashes, autoGive, placeholderAPI, worldGuard, limit, takedLoot, grinchEnabled, fly, shift, firework, loots, miniMessage, updaterEnabled, required, release, skull;
    private int spawnMode, allProcent, procent, wg, id, timeLived, max, chance, remove, every, modeInt, giftId;
    private SafeListenerVersion8 safeListenerVersion8;
    private FileConfiguration loot, config, database;
    private double locationHeight, locationDistance;
    private String texture, type, hologramType;
    private ConfigurationSection settings;
    private WorldGuardMode worldGuardMode;
    private SafeListener safeListener;
    private Material giftMaterial;
    private SpawnGifts spawnGifts;
    private WorldsList worldType;
    private BiomesList biomeType;
    private FallGifts fallGifts;
    private Colorizer colorizer;
    private Material material;
    private UseGifts useGifts;

    public Values(ChristmasGifts plugin) {
        this.plugin = plugin;
        logger = plugin.getLogger();
        dataFolder = plugin.getDataFolder();
        updater = new Updater(plugin, this);
        configFile = new File(dataFolder, "config.yml");
        dbFile = new File(dataFolder, "storage/db.yml");
        pluginManager = plugin.getServer().getPluginManager();
        lootFile = new File(dataFolder, "storage/loot.yml");
        try {
            Class.forName("net.kyori.adventure.text.minimessage.MiniMessage");
            miniMessage = true;
        } catch (Exception e) {
            miniMessage = false;
        }
    }

    public void setup(boolean onlineUpdates) {
        getConfigurations();
        updater.checkUpdates(onlineUpdates);
        loadConfigurations();
        clear();
        setupSettings();
        setupLocales();
        setupListeners();
        plugin.getSupports().register();
        if (loots) {
            plugin.getLootService().setup();
        }
    }

    private void getConfigurations() {
        saveConfig("config", configFile);
        saveConfig("storage/db", dbFile);
        saveConfig("storage/loot", lootFile);
        loadConfigurations();
        if (settings == null) {
            return;
        }
        ConfigurationSection updater = settings.getConfigurationSection("updater");
        if (updater == null) {
            return;
        }
        updaterEnabled = updater.getBoolean("enabled");
        ConfigurationSection updaterSettings = updater.getConfigurationSection("settings");
        if (updaterSettings == null) {
            return;
        }
        required = updaterSettings.getBoolean("required");
        release = updaterSettings.getBoolean("release");
    }

    private void loadConfigurations() {
        loot = YamlConfiguration.loadConfiguration(lootFile);
        saveDatabase();
        loadDatabase();
        config = YamlConfiguration.loadConfiguration(configFile);
        settings = config.getConfigurationSection("settings");
    }

    private void setupListeners() {
        ConfigurationSection specialSettings = settings.getConfigurationSection("special");
        autoGive = specialSettings.getBoolean("autoGive");
        if (!autoGive) {
            if (safeListener == null) {
                safeListener = new SafeListener(plugin);
                pluginManager.registerEvents(safeListener, plugin);
            }
            boolean version8 = plugin.getVersion() >= 8;
            if (version8 && safeListenerVersion8 == null) {
                safeListenerVersion8 = new SafeListenerVersion8(plugin);
                pluginManager.registerEvents(safeListenerVersion8, plugin);
            }
            if (useGifts == null) {
                useGifts = new UseGifts(plugin);
                pluginManager.registerEvents(useGifts, plugin);
            }
        }
        ConfigurationSection gift = settings.getConfigurationSection("gift");
        ConfigurationSection mode = gift.getConfigurationSection("spawn.mode");
        if (spawnGifts == null) {
            spawnGifts = new SpawnGifts(plugin);
        }
        spawnGifts.stopTasks();
        modeInt = mode.getInt("enabled");
        if (modeInt == 1) {
            spawnGifts.setDisabledMode(false);
            pluginManager.registerEvents(spawnGifts, plugin);
        } else if (modeInt == 2) {
            spawnGifts.setDisabledMode(true);
            ConfigurationSection pathMode = mode.getConfigurationSection("2");
            if (!pathMode.getString("people").equalsIgnoreCase("null")) {
                spawnGifts.createEveryTimeTask(pathMode.getInt("people"), pathMode.getInt("every"));
            } else {
                logger.info("How many people is null, the mode 2 is disabled.");
            }
        } else {
            logger.info("You have disabled the spawn mode.");
        }
        if (fallGifts == null) {
            fallGifts = new FallGifts(plugin);
            pluginManager.registerEvents(fallGifts, plugin);
        }
        onCrashes = specialSettings.getBoolean("onCrashes");
    }

    private void setupSettings() {
        ConfigurationSection settings = config.getConfigurationSection("settings");
        String translate = settings.getString("translate").toLowerCase();
        if (miniMessage && translate.equalsIgnoreCase("minimessage")) {
            colorizer = new MiniMessageColorizer();
        } else {
            colorizer = new LegacyColorizer();
        }
        locale = settings.getBoolean("locales");
        ConfigurationSection supports = settings.getConfigurationSection("supports");
        checkSupports(supports);
        ConfigurationSection functions = settings.getConfigurationSection("functions");
        setupSettingsBiomes(functions);
        setupSettingsWorlds(functions);
        ConfigurationSection gift = settings.getConfigurationSection("gift");
        setupGiftSettings(gift);
        ConfigurationSection loot = settings.getConfigurationSection("loot");
        takedLoot = loot.getBoolean("taked");
        loots = loot.getBoolean("enabled");
        ConfigurationSection grinch = settings.getConfigurationSection("grinch");
        grinchEnabled = grinch.getBoolean("enabled");
        chance = grinch.getInt("chance");
    }

    private void setupGiftSettings(ConfigurationSection gift) {
        max = gift.getInt("max");
        limit = gift.getBoolean("limit");
        setupMaterialGift(gift);
        texture = gift.getString("texture");
        remove = gift.getInt("remove");
        ConfigurationSection spawn = gift.getConfigurationSection("spawn");
        type = spawn.getString("type");
        id = spawn.getInt("id");
        setupDefaultSettings();
        firework = spawn.getBoolean("firework");
        timeLived = spawn.getInt("timeLived");
        ConfigurationSection searchLocation = spawn.getConfigurationSection("searchLocation");
        locationHeight = searchLocation.getDouble("height");
        locationDistance = searchLocation.getDouble("distance");
        ConfigurationSection mode = spawn.getConfigurationSection("mode");
        spawnMode = spawn.getInt("enabled");
        ConfigurationSection modeOne = mode.getConfigurationSection("1");
        allProcent = modeOne.getInt("fullChance");
        procent = modeOne.getInt("chance");
        every = modeOne.getInt("every");
        wg = spawn.getInt("worldGuardSupport", 3);
        setupWg();
        ConfigurationSection blocked = spawn.getConfigurationSection("blocked");
        fly = blocked.getBoolean("fly");
        shift = blocked.getBoolean("shift");
    }

    private void setupMaterialGift(ConfigurationSection gift) {
        String materialGiftString = gift.getString("material");
        int version = plugin.getVersion();
        Material skullMaterial;
        if (version > 12) {
            skullMaterial = Material.PLAYER_HEAD;
        } else {
            skullMaterial = Material.valueOf("SKULL");
        }
        giftMaterial = Material.REDSTONE_BLOCK;
        String giftIdString = gift.getString("id");
        if (giftIdString == null || giftIdString.equalsIgnoreCase("null")) {
            giftId = 1;
        } else {
            giftId = gift.getInt("id");
        }
        skull = false;
        if (materialGiftString == null || materialGiftString.equalsIgnoreCase("null")) {
            if (plugin.getVersion() > 7) {
                giftMaterial = skullMaterial;
                skull = true;
            }
        } else {
            try {
                giftMaterial = Material.valueOf(materialGiftString.toUpperCase());
                if (giftMaterial == skullMaterial) {
                    skull = true;
                }
            } catch (Exception e) {
                giftMaterial = Material.REDSTONE_BLOCK;
                logger.warning("An error occurred when installing the gift material: " + e);
            }
        }
    }

    private void setupWg() {
        if (wg == 3) {
            worldGuardMode = new WorldGuardMode3();
        } else if (wg == 2) {
            worldGuardMode = new WorldGuardMode2();
        } else {
            worldGuardMode = new WorldGuardMode1();
        }
    }

    private void setupDefaultSettings() {
        if (type != null && !type.contains("null")) {
            material = Material.getMaterial(type);
        } else {
            int version = plugin.getVersion();
            if (version > 13) {
                material = Material.getMaterial("BARREL");
            } else if (version == 13) {
                material = Material.OAK_PLANKS;
            } else {
                material = Material.valueOf("WOOD");
            }
        }
    }

    private void checkSupports(ConfigurationSection supports) {
        hologramType = supports.getString("hologramType");
        placeholderAPI = supports.getBoolean("placeholderAPI");
        if (placeholderAPI && plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") == null) {
            logger.warning("The PlaceholderAPI was not detected. This feature will be disabled.");
            placeholderAPI = false;
        }
        worldGuard = supports.getBoolean("worldGuard");
        if (worldGuard && plugin.getServer().getPluginManager().getPlugin("WorldGuard") == null) {
            logger.warning("The WorldGuard was not detected. This feature will be disabled.");
            worldGuard = false;
        }
    }

    private void setupSettingsBiomes(ConfigurationSection functions) {
        ConfigurationSection biomesSection = functions.getConfigurationSection("biomes");
        biomeType = biomesSection.getString("type").equals("allowed") ? new AllowedWorlds(this) : new BlockedWorlds(this);
        List<String> biomesList = biomesSection.getStringList("list");
        for (String biomeString : biomesList) {
            if (biomeString.isEmpty() || biomeString.equals("no")) {
                continue;
            }
            try {
                Biome biome = Biome.valueOf(biomeString.toUpperCase());
                biomes.add(biome);
            } catch (IllegalArgumentException e) {
                if (!biomeString.startsWith("badlAnds") && !biomeString.endsWith("badlAnds") && !biomeString.endsWith("savAnna") && !biomeString.startsWith("modifiEd") && !biomeString.startsWith("wOoded") && !biomeString.endsWith("hIlls") && !biomeString.endsWith("mountAins")) {
                    logger.warning("Error (IllegalArgumentException) in biomes.list " + biomeString + ": " + e);
                }
            } catch (Exception e) {
                logger.warning("Error in biomes.list " + biomeString + ": " + e);
            }
        }
    }

    private void setupSettingsWorlds(ConfigurationSection functions) {
        ConfigurationSection worldsSection = functions.getConfigurationSection("worlds");
        worldType = worldsSection.getString("type").equals("allowed") ? new AllowedWorlds(this) : new BlockedWorlds(this);
        List<String> worldsList = worldsSection.getStringList("list");
        for (String worldString : worldsList) {
            if (worldString.isEmpty() || worldString.equals("no")) {
                continue;
            }
            try {
                World world = plugin.getServer().getWorld(worldString);
                worlds.add(world);
            } catch (Exception e) {
                logger.warning("Error in worlds.list " + worldString + ": " + e);
            }
        }
    }

    private void setupLocales() {
        Map<String, FileConfiguration> locales = new HashMap<>();
        locales.put("", config);
        if (locale) {
            File file = new File(dataFolder, "locales");
            if (!file.exists()) {
                for (String s : lcs) {
                    plugin.saveResource("locales/" + s + ".yml", false);
                }
            }
            File[] dir = file.listFiles();
            if (dir == null) {
                logger.warning("Locales are empty.");
            } else {
                for (File f : dir) {
                    if (f.isFile()) {
                        String name = f.getName();
                        locales.put(name.substring(0, name.indexOf(".")).toLowerCase(), YamlConfiguration.loadConfiguration(f));
                    }
                }
            }
        }
        for (Map.Entry<String, FileConfiguration> entry : locales.entrySet()) {
            FileConfiguration config = entry.getValue();
            ConfigurationSection messages = config.getConfigurationSection("actions");
            String name = entry.getKey();
            noPermission.put(name, getActionList(messages.getStringList("command.noPermission")));
            ConfigurationSection holograms = config.getConfigurationSection("settings.holograms");
            List<String> lines = holograms.getStringList("lines");
            Map<Integer, String> time = new HashMap<>();
            int line = 0;
            for (String text : lines) {
                if (text.contains("%time%")) {
                    time.put(line, text);
                }
            }
            hologramDataMap.put(name, new HologramData(lines, time));
            ConfigurationSection hologramLocation = holograms.getConfigurationSection("location");
            addLocation.put(name, new LocationData(hologramLocation.getDouble("addX"), hologramLocation.getDouble("addY"), hologramLocation.getDouble("addZ")));
            ConfigurationSection gift = messages.getConfigurationSection("gift");
            startGifts.put(name, getActionList(gift.getStringList("spawn")));
            stopGifts.put(name, getActionList(gift.getStringList("loss")));
            successful.put(name, getActionList(gift.getStringList("success")));
            limit_messages.put(name, getActionList(gift.getStringList("limit")));
        }
    }

    public void deleteDirectory(File f) {
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    delete(file);
                }
            }
            delete(f);
        }
    }

    private Action fromString(String str) {
        Matcher matcher = actionPattern.matcher(str);
        if (!matcher.matches()) {
            return new Action(ActionType.MESSAGE, str);
        }
        ActionType type;
        try {
            type = ActionType.valueOf(matcher.group(1).toUpperCase());
        } catch (IllegalArgumentException e) {
            type = ActionType.MESSAGE;
            return new Action(type, str);
        }
        return new Action(type, matcher.group(2).trim());
    }

    private void saveConfig(String configName, File file) {
        if (file.exists()) {
            return;
        }
        String resource = configName + ".yml";
        try {
            plugin.saveResource(resource, true);
        } catch (Exception e) {
            logger.warning("Error save configurations. Error: " + e);
        }
    }

    private void clear() {
        Supports supports = plugin.getSupports();
        if (supports != null) {
            supports.unregister();
        }
        hologramDataMap.clear();
        worlds.clear();
        biomes.clear();
        for (Map<String, List<Action>> map : Arrays.asList(noPermission, stopGifts, successful, startGifts, limit_messages)) {
            map.clear();
        }
    }

    private void loadDatabase() {
        database = YamlConfiguration.loadConfiguration(dbFile);
        ConfigurationSection players = database.getConfigurationSection("players");
        if (players != null) {
            for (String playerName : players.getKeys(false)) {
                plugin.getGiftManager().getLimitGifts().put(playerName, players.getInt(playerName));
            }
        }
    }

    public void saveDatabase() {
        if (database != null) {
            ConfigurationSection players = database.getConfigurationSection("players");
            if (players == null) {
                players = database.createSection("players");
            }
            for (Map.Entry<String, Integer> entry : plugin.getGiftManager().getLimitGifts().entrySet()) {
                players.set(entry.getKey(), entry.getValue());
            }
            try {
                database.save(dbFile);
            } catch (Exception e) {
                logger.warning("Save db.yml error: " + e);
            }
        }
    }

    public void backupConfig(String parentPath, File file, String oldVersion) throws Exception {
        File copied = new File(dataFolder, parentPath + "backups/" + file.getName() + "-" + oldVersion + ".backup");
        Path copiedPath = copied.toPath();
        Files.createDirectories(copied.getParentFile().toPath());
        Files.deleteIfExists(copiedPath);
        Files.copy(file.toPath(), copiedPath);
    }

    public List<Action> getActionList(List<String> actionStrings) {
        ImmutableList.Builder<Action> actionListBuilder = ImmutableList.builder();
        for (String actionString : actionStrings) {
            actionListBuilder.add(fromString(actionString));
        }
        return actionListBuilder.build();
    }

    public void delete(File f) {
        try {
            Files.delete(f.toPath());
        } catch (Exception ignored) {
        }
    }
}
