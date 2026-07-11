package ru.mitriyf.christmasgifts.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.command.subcommand.LootSubCommands;
import ru.mitriyf.christmasgifts.listener.game.SpawnGifts;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.Map;

public final class ChristmasGiftsCommand implements CommandExecutor {
    private final LootSubCommands lootSubCommands;
    private final ChristmasGifts plugin;
    private final Values values;
    private final Utils utils;

    public ChristmasGiftsCommand(ChristmasGifts plugin) {
        this.plugin = plugin;
        utils = plugin.getUtils();
        values = plugin.getValues();
        lootSubCommands = new LootSubCommands(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("ChristmasGifts.use")) {
            utils.sendMessage(sender, values.getNoPermission());
            return false;
        }
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        if (args.length == 0 || args.length > 6) {
            sendHelp(sender);
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "loot": {
                lootSubCommands.checkLootSubCommands(args, sender, player);
                return false;
            }
            case "locale": {
                locale(sender, player);
                return false;
            }
            case "add": {
                addGift(args, sender, player);
                return false;
            }
            case "reload": {
                reload(args, sender);
                return false;
            }
            case "put": {
                putLimit(args, sender);
                return false;
            }
            case "check": {
                checkLimit(args, sender);
                return false;
            }
            default: {
                sendHelp(sender);
                return false;
            }
        }
    }

    private void locale(CommandSender sender, Player player) {
        if (player != null) {
            sender.sendMessage("§a" + utils.getLocale().player(player));
        } else {
            sender.sendMessage("§cThis command cannot be written in the console!");
        }
    }

    private void addGift(String[] args, CommandSender sender, Player player) {
        if (args.length == 1 || args.length > 3) {
            sender.sendMessage("§a/gifts add Player - §fPlace a gift near the player");
            return;
        }
        Player selectedPlayer = plugin.getServer().getPlayer(args[1]);
        if (selectedPlayer == null) {
            player.sendMessage("§cIs this player on the server?");
            return;
        }
        boolean force = args.length == 3;
        SpawnGifts spawnGifts = values.getSpawnGifts();
        if ((!spawnGifts.isLimited(selectedPlayer.getName()) && args.length == 2) || force) {
            spawnGifts.spawnFallingBlock(selectedPlayer, force);
            sender.sendMessage("§aSuccessfully!");
        } else {
            utils.sendMessage(selectedPlayer, values.getLimit_messages());
            player.sendMessage("§cThe player has already collected a certain number of gifts! :(\n§aAdd -f to the command to execute.");
        }
    }

    private void reload(String[] args, CommandSender sender) {
        if (args.length > 1) {
            sender.sendMessage("§c/gifts reload - §fReload the plugin");
            return;
        }
        values.setup(false);
        sender.sendMessage("§aSuccessfully!");
    }

    private void putLimit(String[] args, CommandSender sender) {
        if (args.length != 3) {
            sender.sendMessage("§c/gifts put PlayerName Amount - §fSet your gift limit for the player");
            return;
        }
        String name = args[1];
        if (plugin.getServer().getPlayer(name) == null) {
            sender.sendMessage("§cPlayer not found.");
            return;
        }
        try {
            int amount = Integer.parseInt(args[2]);
            plugin.getGiftManager().getLimitGifts().put(name, amount);
        } catch (Exception e) {
            sender.sendMessage("Error in args[2]: " + e);
            return;
        }
        if (values.isOnCrashes()) {
            values.saveDatabase();
        }
        sender.sendMessage("§aSuccessfully!");
    }

    private void checkLimit(String[] args, CommandSender sender) {
        if (args.length == 1 || args.length >= 3) {
            sender.sendMessage("§c/gifts check PlayerName - §fCheck player gift limit");
            return;
        }
        String name = args[1];
        if (plugin.getServer().getPlayer(name) == null) {
            sender.sendMessage("§cPlayer not found.");
            return;
        }
        Map<String, Integer> players = plugin.getGiftManager().getLimitGifts();
        Integer amount = players.get(name);
        if (amount != null) {
            sender.sendMessage("§fAmount: §a" + amount);
        } else {
            sender.sendMessage("§cNot found in database.");
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§aHelp:\n");
        sender.sendMessage("§a/gifts reload - §fReload the plugin.");
        sender.sendMessage("§a/gifts add PlayerName - §fPlace a gift near the player.");
        sender.sendMessage("§a/gifts put PlayerName Amount - §fSet your gift limit for the player.");
        sender.sendMessage("§a/gifts check PlayerName - §fCheck player gift limit.");
        sender.sendMessage("§a/gifts locale - §fCheck your client's language.");
        sender.sendMessage("§a/gifts loot - §fGet help about the loot subcommand.");
    }
}