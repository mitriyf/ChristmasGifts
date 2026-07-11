package ru.mitriyf.christmasgifts.model;

import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@Getter
public class FallingData {
    private final UUID playerUuid;
    private final BukkitTask task;
    private final boolean forced;

    public FallingData(UUID playerUuid, BukkitTask task, boolean forced) {
        this.playerUuid = playerUuid;
        this.task = task;
        this.forced = forced;
    }
}
