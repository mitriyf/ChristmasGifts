package ru.mitriyf.christmasgifts.service;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.model.ItemDrop;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

@Getter
public class LootService {
    private final Set<ItemDrop> grinchItems = new HashSet<>(), santaItems = new HashSet<>();
    private final String[] types = new String[]{"santa", "grinch"};
    private final ThreadLocalRandom random;
    private final ChristmasGifts plugin;
    private final Logger logger;
    private final Values values;
    private final Utils utils;

    public LootService(ChristmasGifts plugin) {
        this.plugin = plugin;
        utils = plugin.getUtils();
        logger = plugin.getLogger();
        values = plugin.getValues();
        random = plugin.getRandom();
    }

    public void setup() {
        clear();
        FileConfiguration loot = values.getLoot();
        ConfigurationSection lootSection = loot.getConfigurationSection("loot");
        if (lootSection == null) {
            lootSection = loot.createSection("loot");
        }
        for (String type : types) {
            ConfigurationSection torchSection = lootSection.getConfigurationSection(type);
            if (torchSection == null) {
                torchSection = lootSection.createSection(type);
            }
            try {
                generateItems(type, torchSection);
            } catch (Exception e) {
                logger.warning("There is no such Material. Error: " + e);
            }
        }
        tryRecovery();
    }

    private void generateItems(String type, ConfigurationSection torchSection) {
        int id = 0;
        if (type.equalsIgnoreCase("grinch")) {
            id = 1;
        }
        for (String itemName : torchSection.getKeys(false)) {
            ConfigurationSection itemSection = torchSection.getConfigurationSection(itemName);
            generateItem(id, type, itemName, itemSection);
        }
    }

    private void generateItem(int id, String type, String itemName, ConfigurationSection itemSection) {
        int amountMax = 1;
        int amountMin = 1;
        try {
            Object amountObject = itemSection.get("amount");
            if (amountObject instanceof Integer) {
                Integer amount = (Integer) amountObject;
                amountMax = amount;
                amountMin = amount;
            } else {
                String[] args = ((String) amountObject).split("-");
                amountMax = utils.formatInt(args[1]);
                amountMin = utils.formatInt(args[0]);
                if (amountMax < amountMin) {
                    logger.warning("Item-Id: " + itemName + "\nError: amountMax cannot be less than amountMin");
                    return;
                }
            }
        } catch (Exception e) {
            logger.warning("The amount of the item is incorrect. Fix it in loot.yml. Item-id: " + type + "." + itemName + "\nError: " + e);
        }
        try {
            Object itemObject = itemSection.get("item");
            ItemStack stack;
            if (itemObject instanceof String) {
                stack = new ItemStack(Material.valueOf((String) itemObject));
            } else {
                stack = new ItemStack(itemSection.getItemStack("item"));
            }
            ConfigurationSection locationSection = itemSection.getConfigurationSection("location");
            double addX = 0.5, addY = 0, addZ = 0.5;
            if (locationSection != null) {
                addX = locationSection.getDouble("addX");
                addY = locationSection.getDouble("addY");
                addZ = locationSection.getDouble("addZ");
            }
            int chance = itemSection.getInt("chance");
            ItemDrop itemDrop = new ItemDrop(type, itemName, stack, addX, addY, addZ, chance, amountMax, amountMin, random);
            if (id == 1) {
                grinchItems.add(itemDrop);
            } else {
                santaItems.add(itemDrop);
            }
        } catch (Exception e) {
            logger.warning("The item is defective. Maybe chance or Material. Item-id: " + (type == null ? "default" : type) + "." + itemName + "\nError: " + e);
        }
    }

    public void dropLoot(World world, Location location) {
        if (!values.isLoots()) {
            return;
        }
        location = location.clone();
        Set<ItemDrop> itemDrops;
        if (!values.isGrinchEnabled()) {
            itemDrops = santaItems;
        } else {
            int currentChance = random.nextInt(101);
            if (values.getChance() >= currentChance) {
                itemDrops = grinchItems;
            } else {
                itemDrops = santaItems;
            }
        }
        for (ItemDrop itemDrop : itemDrops) {
            int chance = random.nextInt(101);
            if (itemDrop.getChance() >= chance) {
                try {
                    ItemStack itemStack = itemDrop.generateItem();
                    world.dropItem(location.add(itemDrop.getAddX(), itemDrop.getAddY(), itemDrop.getAddZ()), itemStack);
                } catch (Exception e) {
                    logger.warning("The item is defective. Item-id: " + itemDrops + "." + itemDrop.getItemName() + "\nError: " + e);
                }
            }
        }
    }

    public void tryRecovery() {
        if (!values.isOnCrashes()) {
            return;
        }
        plugin.getGiftManager().close();
        FileConfiguration database = values.getDatabase();
        List<String> gifts = database.getStringList("gifts");
        if (gifts == null) {
            return;
        }
        for (String locs : gifts) {
            String[] split = locs.split(":");
            World world = plugin.getServer().getWorld(split[0]);
            Location location = new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]));
            location.getBlock().setType(Material.AIR);
            if (values.isTakedLoot()) {
                dropLoot(world, location);
            } else {
                String name = split[4];
                Map<String, Integer> players = plugin.getGiftManager().getLimitGifts();
                players.put(name, players.get(name) - 1);
            }
        }
        database.set("gifts", null);
        try {
            database.save(new File(plugin.getDataFolder(), "storage/db.yml"));
        } catch (Exception e) {
            logger.warning("db.yml save failed. Error: " + e);
        }
    }

    public void clear() {
        santaItems.clear();
        grinchItems.clear();
    }
}
