package ru.mitriyf.christmasgifts.compat.impl.v1_9;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mitriyf.christmasgifts.compat.abstraction.HandItem;

public class HandItemV9 implements HandItem {
    @Override
    public ItemStack getItem(Player player) {
        return player.getInventory().getItemInMainHand();
    }
}
