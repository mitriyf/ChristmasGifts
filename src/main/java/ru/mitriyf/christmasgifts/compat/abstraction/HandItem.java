package ru.mitriyf.christmasgifts.compat.abstraction;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface HandItem {
    ItemStack getItem(Player player);
}
