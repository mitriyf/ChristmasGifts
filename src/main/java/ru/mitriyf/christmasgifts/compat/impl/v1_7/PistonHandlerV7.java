package ru.mitriyf.christmasgifts.compat.impl.v1_7;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonRetractEvent;
import ru.mitriyf.christmasgifts.compat.abstraction.PistonHandler;
import ru.mitriyf.christmasgifts.manager.GiftManager;

@SuppressWarnings("deprecation")
public class PistonHandlerV7 implements PistonHandler {
    private final GiftManager giftManager;

    public PistonHandlerV7(GiftManager giftManager) {
        this.giftManager = giftManager;
    }

    @Override
    public void check(BlockPistonRetractEvent event) {
        Block block = event.getRetractLocation().getBlock();
        if (check(block.getLocation())) {
            event.setCancelled(true);
            return;
        }
        Block newBlock = event.getBlock().getRelative(event.getDirection());
        if (check(newBlock.getLocation())) {
            event.setCancelled(true);
        }
    }

    private boolean check(Location location) {
        return giftManager.getGifts().containsKey(location);
    }
}
