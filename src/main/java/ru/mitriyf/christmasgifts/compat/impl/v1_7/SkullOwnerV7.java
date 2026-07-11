package ru.mitriyf.christmasgifts.compat.impl.v1_7;

import org.bukkit.block.Skull;
import ru.mitriyf.christmasgifts.compat.abstraction.SkullBase;

public class SkullOwnerV7 implements SkullBase {
    @Override
    @SuppressWarnings("deprecation")
    public void setSkin(Skull skull, String skin) {
        skull.setRawData((byte) 1);
        if (skin.length() > 16) {
            skull.setOwner("defib");
        } else {
            skull.setOwner(skin);
        }
        skull.update();
    }
}
