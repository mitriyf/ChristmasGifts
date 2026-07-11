package ru.mitriyf.christmasgifts.compat.impl.v1_12;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.block.Skull;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.compat.abstraction.SkullBase;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Logger;

public class SkullBaseV12 implements SkullBase {
    private final Logger logger;

    public SkullBaseV12(ChristmasGifts plugin) {
        logger = plugin.getLogger();
    }

    @Override
    public void setSkin(Skull skull, String skin) {
        try {
            GameProfile profile = new GameProfile(UUID.randomUUID(), null);
            profile.getProperties().put("textures", new Property("textures", skin));
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
        } catch (Exception e) {
            logger.warning("Error set skin item: " + e + "\nBlock: " + skull);
        }
        skull.update();
    }
}
