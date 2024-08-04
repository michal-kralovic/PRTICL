package com.minkuh.prticl.common.message;

import com.minkuh.prticl.common.wrappers.PaginatedResult;
import com.minkuh.prticl.data.entities.Node;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;
import static net.kyori.adventure.text.format.TextDecoration.State.FALSE;
import static net.kyori.adventure.text.format.TextDecoration.State.TRUE;

public class PrticlMessages {
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
     * @param nodePage     The list to show
     * @return TextComponent output, the message to be sent to the player.
     */
    public @NotNull TextComponent listNodes(PaginatedResult<Node> nodePage) {
        TextComponent output = player("List of nodes — Page " + nodePage.getPage() + " / " + nodePage.getTotalPages());

        for (var entry : nodePage.getList())
            output = (TextComponent) output.decoration(BOLD, false).appendNewline().append(listEntryOfNode(entry));

        int nodeCount = nodePage.getList().size();
        String nodeAmount = nodeCount + (nodeCount == 1 ? " node" : " nodes");
        output = (TextComponent) output.appendNewline().append(player("List of nodes — " + nodeAmount + " total"));

        return output;
    }

    /**
     * Builds a list entry based on the passed data.
     *
     * @param node The node to build this entry with
     * @return The TextComponent to use in a list.
     */
    @Contract("_ -> new")
    private @NotNull TextComponent listEntryOfNode(@NotNull Node node) {
        return text().content("- ")
                .append(text().content("ID: ").color(color(MessageColors.system)))
                .append(text().content(node.getId() + ", ").color(color(MessageColors.prticlLight)))
                .append(text().content("Owner: ").color(color(MessageColors.system)))
                .append(text().content(node.getPlayer().getUsername() + ", ").color(color(MessageColors.prticlLight)))
                .append(text().content("Node name: ").color(color(MessageColors.system)))
                .append(text().content(node.getName() + ", ").color(color(MessageColors.prticlLight)))
                .append(text().content("Node type: ").color(color(MessageColors.system)))
                .append(text().content(node.getParticleType().toString().toLowerCase(Locale.ROOT)).color(color(MessageColors.prticlLight)))
                .build();
    }
}