package ru.mitriyf.christmasgifts.hook;

import lombok.Getter;
import ru.mitriyf.christmasgifts.ChristmasGifts;
import ru.mitriyf.christmasgifts.compat.abstraction.WorldGuardSupport;
import ru.mitriyf.christmasgifts.compat.impl.v1_12.WorldGuardSupportV12;
import ru.mitriyf.christmasgifts.compat.impl.v1_13.WorldGuardSupportV13;
import ru.mitriyf.christmasgifts.hook.hologram.HologramSupport;
import ru.mitriyf.christmasgifts.hook.hologram.impl.DecentHolograms;
import ru.mitriyf.christmasgifts.hook.hologram.impl.FancyHolograms;
import ru.mitriyf.christmasgifts.hook.hologram.impl.HolographicDisplays;
import ru.mitriyf.christmasgifts.hook.placeholders.PlaceholderAPISupport;
import ru.mitriyf.christmasgifts.values.Values;

import java.util.logging.Logger;

@Getter
public class Supports {
    private final int version;
    private final Values values;
    private final Logger logger;
    private final ChristmasGifts plugin;
    private HologramSupport hologramSupport;
    private WorldGuardSupport worldGuardSupport;
    private PlaceholderAPISupport placeholderAPISupport;

    public Supports(ChristmasGifts plugin) {
        this.plugin = plugin;
        values = plugin.getValues();
        logger = plugin.getLogger();
        version = plugin.getVersion();
    }

    public void register() {
        setupPlaceholderAPI();
        setupWorldGuard();
        setupHologram();
    }

    private void setupPlaceholderAPI() {
        if (values.isPlaceholderAPI()) {
            placeholderAPISupport = new PlaceholderAPISupport(plugin);
            placeholderAPISupport.register();
        }
    }

    private void setupWorldGuard() {
        if (values.isWorldGuard()) {
            if (version > 12) {
                worldGuardSupport = new WorldGuardSupportV13();
            } else {
                worldGuardSupport = new WorldGuardSupportV12(plugin);
            }
        }
    }

    private void setupHologram() {
        String hologramType = values.getHologramType();
        if (hologramType == null) {
            hologramType = "null";
        }
        if (!plugin.getServer().getPluginManager().isPluginEnabled(hologramType) && !hologramType.contains("null")) {
            plugin.getLogger().warning("\nInstall plugins on the server: " + hologramType);
        } else {
            switch (hologramType.toLowerCase()) {
                case "holographicdisplays": {
                    hologramSupport = new HolographicDisplays(plugin);
                    break;
                }
                case "decentholograms": {
                    hologramSupport = new DecentHolograms(plugin);
                    break;
                }
                case "fancyholograms": {
                    hologramSupport = new FancyHolograms(plugin);
                    break;
                }
                default: {
                    hologramSupport = null;
                    break;
                }
            }
        }
    }

    public void unregister() {
        if (placeholderAPISupport != null) {
            placeholderAPISupport.unregister();
            placeholderAPISupport = null;
        }
        if (hologramSupport != null) {
            hologramSupport.clear();
            hologramSupport = null;
        }
        if (worldGuardSupport != null) {
            worldGuardSupport = null;
        }
    }
}