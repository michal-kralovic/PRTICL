package com.minkuh.prticl.systemutil.message;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import static com.minkuh.prticl.systemutil.message.MessageColors.*;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static net.kyori.adventure.text.format.TextDecoration.State.TRUE;

public class BaseMessageComponents {
    public @NotNull TextComponent prticlDash() {
        return text().content("[").color(color(prticlStrong)).decoration(BOLD, TRUE)
                .append(text().decoration(BOLD, FALSE).content("PRTICL").color(color(prticlLight))
                        .append(text().content("]").color(color(prticlStrong)).decoration(BOLD, TRUE))
                            .append(text(" ")))
                .build();
    }

    public @NotNull TextComponent prticlMessage(String message, int color) {
        return prticlDash().append(text().decoration(BOLD, FALSE).content(message).color(color(color)));
    }

    public @NotNull TextComponent system(String message) {
        return prticlMessage(message, system);
    }

    public @NotNull TextComponent player(String message) {
        return prticlMessage(message, player);
    }

    public @NotNull TextComponent warning(String message) {
        return prticlMessage("WARNING: " + message, warning);
    }

    public @NotNull TextComponent error(String message) {
        return prticlMessage("ERROR: " + message, error);
    }
}
