package ru.mitriyf.christmasgifts.command.subcommand;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.service.LootService;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.io.File;
import java.util.Arrays;

public class LootSubCommands {
    private final LootService lootService;
    private final ChristmasGifts plugin;
    private final Values values;
    private final Utils utils;

    public LootSubCommands(ChristmasGifts plugin) {
        this.plugin = plugin;
        utils = plugin.getUtils();
        values = plugin.getValues();
        lootService = plugin.getLootService();
    }

    public void checkLootSubCommands(String[] args, CommandSender sender, Player player) {
        if (args.length == 1) {
            sendLoot(sender);
            return;
        } else if (args.length >= 3) {
            String type = args[2];
            if (!type.equals("santa") && !type.equals("grinch")) {
                sender.sendMessage("§cWrite santa or grinch");
                return;
            }
        }
        switch (args[1].toLowerCase()) {
            case "add": {
                if (player != null) {
                    addLoot(args, sender, player);
                } else {
                    sender.sendMessage("§cThis command cannot be written in the console!");
                }
                return;
            }
            case "list": {
                listLoot(args, sender);
                return;
            }
            case "get": {
                getLoot(args, sender);
                return;
            }
            case "remove": {
                removeLoot(args, sender);
                return;
            }
            default: {
                sendLoot(sender);
            }
        }
    }

    private void addLoot(String[] args, CommandSender sender, Player player) {
        if (args.length != 6) {
            sender.sendMessage("§c/gifts loot add santa/grinch Name Amount(from-to) Chance - §fAdd an item to gifts");
            return;
        }
        ItemStack hand = utils.getHandItem().getItem(player);
        if (hand.getType() == Material.AIR) {
            player.sendMessage("§cYou don't have an item in your hand.");
            return;
        }
        String type = args[2].toLowerCase();
        String id = args[3];
        int chance = Integer.parseInt(args[5]);
        FileConfiguration loot = values.getLoot();
        ConfigurationSection lootSection = loot.getConfigurationSection("loot");
        if (lootSection == null) {
            lootSection = loot.createSection("loot");
        }
        ConfigurationSection typeSection = lootSection.getConfigurationSection(type);
        if (typeSection == null) {
            typeSection = lootSection.createSection(type);
        }
        ConfigurationSection itemSection = typeSection.getConfigurationSection(id);
        if (itemSection == null) {
            itemSection = typeSection.createSection(id);
        }
        itemSection.set("item", hand);
        String[] s = args[4].split("-");
        if (s.length == 2) {
            itemSection.set("amount", args[4]);
        } else if (s.length == 1) {
            try {
                itemSection.set("amount", Integer.parseInt(args[4]));
            } catch (Exception e) {
                sender.sendMessage("Error in args[3]: " + e);
                return;
            }
        } else {
            sender.sendMessage("Error in args[3]");
            return;
        }
        itemSection.set("chance", chance);
        try {
            loot.save(new File(plugin.getDataFolder(), "storage/loot.yml"));
        } catch (Exception e) {
            sender.sendMessage("Save loot.yml error: " + e);
        }
        if (values.isLoots()) {
            lootService.setup();
        }
        sender.sendMessage("§aSuccessfully!");
    }

    private void listLoot(String[] args, CommandSender sender) {
        if (args.length != 3) {
            sender.sendMessage("§c/gifts loot list santa/grinch - §fGet a list of items");
            return;
        }
        String type = args[2].toLowerCase();
        FileConfiguration loot = values.getLoot();
        ConfigurationSection lootSection = loot.getConfigurationSection("loot");
        if (lootSection == null) {
            lootSection = loot.createSection("loot");
        }
        ConfigurationSection typeSection = lootSection.getConfigurationSection(type);
        if (typeSection == null) {
            typeSection = lootSection.createSection(type);
        }
        Object[] list = typeSection.getKeys(false).toArray();
        sender.sendMessage("§a" + list.length + " items found (" + type + "):\n" + Arrays.toString(list));
    }

    private void getLoot(String[] args, CommandSender sender) {
        if (args.length != 4) {
            sender.sendMessage("§c/gifts loot get santa/grinch Name - §fGet the item's ItemStack");
            return;
        }
        String type = args[2].toLowerCase();
        String id = args[3];
        FileConfiguration loot = values.getLoot();
        ConfigurationSection lootSection = loot.getConfigurationSection("loot");
        if (lootSection == null) {
            lootSection = loot.createSection("loot");
        }
        ConfigurationSection typeSection = lootSection.getConfigurationSection(type);
        if (typeSection == null) {
            typeSection = lootSection.createSection(type);
        }
        ConfigurationSection itemSection = typeSection.getConfigurationSection(id);
        if (itemSection == null) {
            sender.sendMessage("§cNot found.");
            return;
        }
        ItemStack stack = itemSection.getItemStack("item");
        if (stack == null) {
            String materialName = itemSection.getString("item");
            if (materialName == null) {
                sender.sendMessage("§cThe item type is broken..");
                return;
            }
            stack = new ItemStack(Material.valueOf(materialName.toUpperCase()));
        }
        sender.sendMessage("§aItemStack " + id + " item:\n" + stack + "\nItemMeta:\n" + stack.getItemMeta());
    }

    private void removeLoot(String[] args, CommandSender sender) {
        if (args.length != 4) {
            sender.sendMessage("§c/gifts loot remove santa/grinch Name - §fRemove an item from gifts");
            return;
        }
        String type = args[2].toLowerCase();
        String id = args[3];
        FileConfiguration loot = values.getLoot();
        ConfigurationSection lootSection = loot.getConfigurationSection("loot");
        if (lootSection == null) {
            lootSection = loot.createSection("loot");
        }
        ConfigurationSection typeSection = lootSection.getConfigurationSection(type);
        if (typeSection == null) {
            typeSection = lootSection.createSection(type);
        }
        ConfigurationSection itemSection = typeSection.getConfigurationSection(id);
        if (itemSection == null || itemSection.get("item") == null) {
            sender.sendMessage("§cNot found.");
            return;
        } else {
            typeSection.set(id, null);
        }
        try {
            loot.save(new File(plugin.getDataFolder(), "storage/loot.yml"));
        } catch (Exception e) {
            sender.sendMessage("Save loot.yml error: " + e);
        }
        if (values.isLoots()) {
            lootService.setup();
        }
        sender.sendMessage("§aSuccessfully!");
    }

    private void sendLoot(CommandSender sender) {
        sender.sendMessage("§aHelp from the loot subcommand:\n");
        sender.sendMessage("§a/gifts loot add santa/grinch Name Amount(from-to) Chance - §fAdd an item to gifts");
        sender.sendMessage("§a/gifts loot list santa/grinch - §fGet a list of items");
        sender.sendMessage("§a/gifts loot get santa/grinch Name - §fGet the item's ItemStack");
        sender.sendMessage("§a/gifts loot remove santa/grinch Name - §fRemove an item from gifts");
    }
}
