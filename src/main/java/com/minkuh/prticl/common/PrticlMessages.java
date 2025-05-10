package com.minkuh.prticl.common;

import com.minkuh.prticl.Prticl;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static net.kyori.adventure.text.format.TextDecoration.State.TRUE;

public class PrticlMessages {
    private static final Logger logger = Logger.getLogger("Prticl");

    private static TextComponent PRTICL_FLAIR;

    public static @NotNull TextComponent prticlFlair() {
        return PRTICL_FLAIR;
    }

    public static @NotNull TextComponent prticlMessage(String message, int color) {
        return prticlFlair().append(text().decoration(BOLD, FALSE).content(message).color(color(color)));
    }

    public static @NotNull TextComponent system(String message) {
        return prticlMessage(message, Colors.system);
    }

    public static @NotNull TextComponent player(String message) {
        return prticlMessage(message, Colors.player);
    }

    public static @NotNull TextComponent warning(String message) {
        return prticlMessage("WARNING: " + message, Colors.warning);
    }

    public static @NotNull TextComponent error(String message) {
        return prticlMessage("ERROR: " + message, Colors.error);
    }

    public static void loadConfiguration(Prticl prticl) {
        var config = prticl.getConfig();

        try {
            Colors.strong = config.getInt("colors.strong", 0xb8f2e6);
            Colors.light = config.getInt("colors.light", 0xeff7f6);
            Colors.player = config.getInt("colors.player", 0xeff7f6);
            Colors.system = config.getInt("colors.system", 0xc0fdfb);
            Colors.warning = config.getInt("colors.warning", 0xf1dea1);
            Colors.error = config.getInt("colors.error", 0xffa69e);

            PRTICL_FLAIR = text().decoration(BOLD, TRUE).content("[").color(color(Colors.strong))
                    .append(text().decoration(BOLD, FALSE).content("PRTICL").color(color(Colors.light))
                            .append(text().content("]").color(color(Colors.strong)).decoration(BOLD, TRUE))
                            .append(text(" ")))
                    .build();

            logger.info("Colors loaded from config.yml");
        } catch (Exception e) {
            logger.severe("Error loading colors from config: " + e.getMessage());
        }
    }

    public static class Colors {
        public static int strong;
        public static int light;
        public static int player;
        public static int system;
        public static int warning;
        public static int error;
    }
}