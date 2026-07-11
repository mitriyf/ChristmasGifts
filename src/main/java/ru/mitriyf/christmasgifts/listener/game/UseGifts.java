package ru.mitriyf.christmasgifts.listener.game;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.hook.Supports;
import ru.mitriyf.christmasgifts.hook.hologram.HologramSupport;
import ru.mitriyf.christmasgifts.manager.GiftManager;
import ru.mitriyf.christmasgifts.model.GiftData;
import ru.mitriyf.christmasgifts.service.LootService;
import ru.mitriyf.christmasgifts.storage.GiftStorage;
import ru.mitriyf.christmasgifts.utils.Utils;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.Map;

public final class UseGifts implements Listener {
    private final GiftManager giftManager;
    private final GiftStorage giftStorage;
    private final LootService lootService;
    private final Supports supports;
    private final Values values;
    private final Utils utils;

    public UseGifts(ChristmasGifts plugin) {
        utils = plugin.getUtils();
        values = plugin.getValues();
        supports = plugin.getSupports();
        giftStorage = utils.getGiftStorage();
        giftManager = plugin.getGiftManager();
        lootService = plugin.getLootService();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock()) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        Location location = block.getLocation();
        Map<Location, GiftData> gifts = giftManager.getGifts();
        GiftData giftData = gifts.get(location);
        if (giftData == null || !giftData.getUuid().equals(player.getUniqueId())) {
            return;
        }
        HologramSupport hologramSupport = supports.getHologramSupport();
        if (hologramSupport != null) {
            hologramSupport.removeHologram(location);
        }
        giftStorage.setBlock(location, block);
        giftManager.removeOnCrashes(location, player.getName());
        lootService.dropLoot(block.getWorld(), location);
        utils.sendMessage(player, values.getSuccessful());
        gifts.remove(location).getTask().cancel();
    }
}