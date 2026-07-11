package ru.mitriyf.christmasgifts.utils.colors.impl;

import ru.mitriyf.christmasgifts.utils.colors.Colorizer;

public class LegacyColorizer implements Colorizer {
    @Override
    public String colorize(String message) {
        if (message == null || message.isEmpty()) {
            return message;
        }
        final char[] chars = message.toCharArray();
        final int length = chars.length;
        final StringBuilder builder = new StringBuilder(length + 32);
        char[] hex = null;
        int start = 0, end;
        loop:
        for (int i = 0; i < length - 1; ) {
            final char ch = chars[i];
            char altChar = '&';
            if (ch == altChar) {
                final char nextChar = chars[++i];
                char colorChar = '§';
                if (nextChar == '#') {
                    if (i + 6 >= length) {
                        break;
                    }
                    if (hex == null) {
                        hex = new char[14];
                        hex[0] = colorChar;
                        hex[1] = 'x';
                    }
                    end = i - 1;
                    for (int j = 0, hexI = 1; j < 6; j++) {
                        final char hexChar = chars[++i];
                        if (!isHexCharacter(hexChar)) {
                            continue loop;
                        }
                        hex[++hexI] = colorChar;
                        hex[++hexI] = hexChar;
                    }
                    builder.append(chars, start, end - start).append(hex);
                    start = i + 1;
                } else {
                    if (isColorCharacter(nextChar)) {
                        chars[i - 1] = colorChar;
                        chars[i] |= 0x20;
                    }
                }
            }
            ++i;
        }
        builder.append(chars, start, length - start);
        return builder.toString();
    }

    private boolean isHexCharacter(final char ch) {
        switch (ch) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case 'a':
            case 'A':
            case 'b':
            case 'B':
            case 'c':
            case 'C':
            case 'd':
            case 'D':
            case 'e':
            case 'E':
            case 'f':
            case 'F': {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    private boolean isColorCharacter(final char ch) {
        if (isHexCharacter(ch)) {
            return true;
        }
        switch (ch) {
            case 'r':
            case 'R':
            case 'k':
            case 'K':
            case 'l':
            case 'L':
            case 'm':
            case 'M':
            case 'n':
            case 'N':
            case 'o':
            case 'O':
            case 'x':
            case 'X': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}