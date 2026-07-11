package ru.mitriyf.christmasgifts.compat.impl.v1_8;

import org.bukkit.block.Block;
import org.bukkit.event.block.BlockPistonRetractEvent;
import ru.mitriyf.christmasgifts.compat.abstraction.PistonHandler;
import ru.mitriyf.christmasgifts.manager.GiftManager;

public class PistonHandlerV8 implements PistonHandler {
    private final GiftManager giftManager;

    public PistonHandlerV8(GiftManager giftManager) {
        this.giftManager = giftManager;
    }

    @Override
    public void check(BlockPistonRetractEvent event) {
        for (Block ok : event.getBlocks()) {
            if (giftManager.getGifts().containsKey(ok.getLocation())) {
                event.setCancelled(true);
            }
        }
    }
}
