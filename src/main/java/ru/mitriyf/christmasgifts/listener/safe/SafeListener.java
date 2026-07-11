package ru.mitriyf.christmasgifts.listener.safe;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.manager.GiftManager;
import ru.mitriyf.christmasgifts.model.GiftData;
import ru.mitriyf.christmasgifts.utils.Utils;

import java.util.Map;

public final class SafeListener implements Listener {
    private final GiftManager giftManager;
    private final ChristmasGifts plugin;
    private final Utils utils;

    public SafeListener(ChristmasGifts plugin) {
        this.plugin = plugin;
        giftManager = plugin.getGiftManager();
        utils = plugin.getUtils();
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent event) {
        Map<Location, GiftData> gifts = giftManager.getGifts();
        for (Block block : event.getBlocks()) {
            Block newBlock = block.getRelative(event.getDirection());
            if (gifts.containsKey(block.getLocation()) || gifts.containsKey(newBlock.getLocation())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent event) {
        utils.getPistonHandler().check(event);
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        checkLocation(event.getBlock().getLocation(), event);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(b -> giftManager.getGifts().containsKey(b.getLocation()));
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        checkLocation(event.getBlock().getLocation(), event);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        checkLocation(event.getBlock().getLocation(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        checkLocation(event.getBlock().getLocation(), event);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        checkLocation(event.getBlockClicked().getRelative(event.getBlockFace()).getLocation(), event);
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        Map<Location, GiftData> gifts = giftManager.getGifts();
        if (gifts.containsKey(event.getToBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent e) {
        updateValues();
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent e) {
        updateValues();
    }

    private void checkLocation(Location location, Cancellable event) {
        Map<Location, GiftData> gifts = giftManager.getGifts();
        if (gifts.containsKey(location)) {
            event.setCancelled(true);
        }
    }

    private void updateValues() {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> plugin.getValues().setup(false), 5L);
    }
}
