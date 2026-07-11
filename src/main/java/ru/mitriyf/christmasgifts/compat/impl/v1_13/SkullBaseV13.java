package ru.mitriyf.christmasgifts.compat.impl.v1_13;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.block.Skull;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.compat.abstraction.SkullBase;

import java.util.UUID;

public class SkullBaseV13 implements SkullBase {
    private final ChristmasGifts plugin;

    public SkullBaseV13(ChristmasGifts plugin) {
        this.plugin = plugin;
    }

    @Override
    public void setSkin(Skull skull, String skin) {
        PlayerProfile profile = plugin.getServer().createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", skin));
        skull.setPlayerProfile(profile);
        skull.update();
    }
}
