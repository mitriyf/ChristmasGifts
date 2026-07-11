package ru.mitriyf.christmasgifts.hook.placeholders;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.manager.GiftManager;
import ru.mitriyf.christmasgifts.model.GiftData;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.Map;

public final class PlaceholderAPISupport extends PlaceholderExpansion {
    private final GiftManager giftManager;
    private final ChristmasGifts plugin;
    private final Values values;

    public PlaceholderAPISupport(ChristmasGifts plugin) {
        this.plugin = plugin;
        values = plugin.getValues();
        giftManager = plugin.getGiftManager();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String ind) {
        String[] args = ind.split("_");
        if (args.length >= 1) {
            String booleanTrue = PlaceholderAPIPlugin.booleanTrue();
            String booleanFalse = PlaceholderAPIPlugin.booleanFalse();
            if (args[0].equalsIgnoreCase("active")) {
                if (args.length == 2) {
                    player = plugin.getServer().getPlayer(args[1]);
                    if (player == null) {
                        return null;
                    }
                }
                for (GiftData giftData : giftManager.getGifts().values()) {
                    if (giftData.getUuid().equals(player.getUniqueId())) {
                        return booleanTrue;
                    }
                }
                return booleanFalse;
            } else if (args[0].equalsIgnoreCase("limit")) {
                Map<String, Integer> players = giftManager.getLimitGifts();
                if (players == null) {
                    return booleanFalse;
                }
                if (args.length == 2) {
                    player = plugin.getServer().getPlayer(args[1]);
                    if (player == null) {
                        return null;
                    }
                }
                String name = player.getName();
                if (values.getMax() > players.getOrDefault(name, 0)) {
                    return booleanFalse;
                }
                return booleanTrue;
            }
        }
        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "ChristmasGifts";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mitriyf";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getConfigVersion();
    }
}
