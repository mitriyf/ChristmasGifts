package ru.mitriyf.christmasgifts.utils.actions.titles.impl;

import org.bukkit.entity.Player;
import ru.mitriyf.christmasgifts.utils.actions.titles.Title;

public class Title11 implements Title {
    @Override
    public void send(Player p, String title, String subtitle, int i, int d, int k) {
        p.sendTitle(title, subtitle, i, d, k);
    }
}
