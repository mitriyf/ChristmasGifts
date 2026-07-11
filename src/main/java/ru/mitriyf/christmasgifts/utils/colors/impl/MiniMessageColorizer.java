package ru.mitriyf.christmasgifts.utils.colors.impl;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import ru.mitriyf.christmasgifts.utils.colors.Colorizer;

public class MiniMessageColorizer implements Colorizer {
    @Override
    public String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        Component component = MiniMessage.miniMessage().deserialize(message);
        return LegacyComponentSerializer.builder().character('§').hexColors().useUnusualXRepeatedCharacterHexFormat().build().serialize(component).replace("&", "§");
    }
}
