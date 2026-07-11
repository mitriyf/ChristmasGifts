package ru.mitriyf.christmasgifts.listener.safe;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.manager.GiftManager;

public final class SafeListenerVersion8 implements Listener {
    private final GiftManager giftManager;

    public SafeListenerVersion8(ChristmasGifts plugin) {
        giftManager = plugin.getGiftManager();
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> giftManager.getGifts().containsKey(block.getLocation()));
    }
}
