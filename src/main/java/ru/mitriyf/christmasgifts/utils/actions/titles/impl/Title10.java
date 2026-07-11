package ru.mitriyf.christmasgifts.utils.actions.titles.impl;

import org.bukkit.entity.Player;
import ru.mitriyf.christmasgifts.utils.actions.titles.Title;

public class Title10 implements Title {
    @Override
    @SuppressWarnings("deprecation")
    public void send(Player p, String title, String subtitle, int i, int d, int k) {
        p.sendTitle(title, subtitle);
    }
}
