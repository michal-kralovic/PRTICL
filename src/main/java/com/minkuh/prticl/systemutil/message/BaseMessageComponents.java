package com.minkuh.prticl.systemutil.message;

import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static net.kyori.adventure.text.format.TextDecoration.State.TRUE;

public class BaseMessageComponents {
    private static final TextComponent PRTICL_FLAIR = text().decoration(BOLD, TRUE).content("[").color(color(MessageColors.prticlStrong))
            .append(text().decoration(BOLD, FALSE).content("PRTICL").color(color(MessageColors.prticlLight))
                    .append(text().content("]").color(color(MessageColors.prticlStrong)).decoration(BOLD, TRUE))
                    .append(text(" ")))
            .build();

    public @NotNull TextComponent prticlFlair() {
        return PRTICL_FLAIR;
    }

    public @NotNull TextComponent prticlMessage(String message, int color) {
        return prticlFlair().append(text().decoration(BOLD, FALSE).content(message).color(color(color)));
    }

    public @NotNull TextComponent system(String message) {
        return prticlMessage(message, MessageColors.system);
    }

    public @NotNull TextComponent player(String message) {
        return prticlMessage(message, MessageColors.player);
    }

    public @NotNull TextComponent warning(String message) {
        return prticlMessage("WARNING: " + message, MessageColors.warning);
    }

    public @NotNull TextComponent error(String message) {
        return prticlMessage("ERROR: " + message, MessageColors.error);
    }

    /**
     * Creates a list of nodes message based on the given list and other inputs.
     *
     * @param content     The list to show
     * @param currentPage The specified page
     * @param totalPages  The total amount of pages
     * @param nodeAmount  The total amount of nodes
     * @return TextComponent output, the message to be sent to the player.
     */
    public @NotNull TextComponent listNodes(List<String[]> content, int currentPage, int totalPages, int nodeAmount) {
        String nodesMessageEnding = nodeAmount == 1 ? " node" : " nodes";
        totalPages = totalPages == 0 ? 1 : totalPages;
        TextComponent output = player("List of nodes — Page " + currentPage + " / " + totalPages);

        for (String[] entry : content)
            output = (TextComponent) output.decoration(BOLD, false).appendNewline().append(listEntryOfNode(entry));

        output = (TextComponent) output.appendNewline().append(player("List of nodes — " + nodeAmount + nodesMessageEnding + " total"));
        return output;
    }

    /**
     * Builds a list entry based on the passed data.
     *
     * @param nodeData The necessary data to build this entry
     * @return The TextComponent to use in a list.
     */
    private @NotNull TextComponent listEntryOfNode(String[] nodeData) {
        return text().content("- ")
                .append(text().content("ID: ").color(color(MessageColors.system)))
                .append(text().content(nodeData[0] + ", ").color(color(MessageColors.prticlLight)))
                .append(text().content("Owner: ").color(color(MessageColors.system)))
                .append(text().content(nodeData[1] + ", ").color(color(MessageColors.prticlLight)))
                .append(text().content("Node name: ").color(color(MessageColors.system)))
                .append(text().content(nodeData[2] + ", ").color(color(MessageColors.prticlLight)))
                .append(text().content("Node type: ").color(color(MessageColors.system)))
                .append(text().content(nodeData[3].toLowerCase(Locale.ROOT)).color(color(MessageColors.prticlLight)))
                .build();
    }
}