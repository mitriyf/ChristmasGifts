package ru.mitriyf.christmasgifts.model;

import lombok.Getter;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

@Getter
public class GiftData {
    private final BukkitTask task;
    private final UUID uuid;

    public GiftData(UUID uuid, BukkitTask task) {
        this.task = task;
        this.uuid = uuid;
    }
}
