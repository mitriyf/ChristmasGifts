package ru.mitriyf.christmasgifts.compat.impl.v1_8;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.mitriyf.christmasgifts.compat.abstraction.HandItem;

@SuppressWarnings("deprecation")
public class HandItemV8 implements HandItem {
    @Override
    public ItemStack getItem(Player player) {
        return player.getInventory().getItemInHand();
    }
}
