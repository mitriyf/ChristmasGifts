package ru.mitriyf.christmasgifts.compat.abstraction;

import org.bukkit.event.block.BlockPistonRetractEvent;

public interface PistonHandler {
    void check(BlockPistonRetractEvent event);
}
