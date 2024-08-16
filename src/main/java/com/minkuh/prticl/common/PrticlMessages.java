package com.minkuh.prticl.common;

import com.minkuh.prticl.common.wrappers.PaginatedResult;
import com.minkuh.prticl.data.database.entities.IPrticlEntity;
import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entities.Player;
import com.minkuh.prticl.data.database.entities.Trigger;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.stream.Collectors;

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
     * Creates a list of entities based on the passed list.
     *
     * @param paginatedResult The list to show
     * @return TextComponent output, the message to be sent to the player.
     */
    public @NotNull TextComponent listEntities(PaginatedResult<IPrticlEntity> paginatedResult) {
        var entityName = paginatedResult.getList().getFirst().getClass().getSimpleName().toLowerCase(Locale.ROOT);
        var pluralEntityName = entityName + 's';
        int prettyPage = paginatedResult.getTotalPages() > 0 && paginatedResult.getPage() == 0 ? 1 : paginatedResult.getPage();

        TextComponent output = player("List of " + pluralEntityName + " — Page " + prettyPage + " / " + paginatedResult.getTotalPages());

        for (var entry : paginatedResult.getList())
            output = (TextComponent) output.decoration(BOLD, false).appendNewline().append(listEntryOfNode(entry));

        output = (TextComponent) output.appendNewline().append(player("List of " + pluralEntityName + " — " + paginatedResult.getTotalCount() + " total"));

        return output;
    }

    private @NotNull TextComponent listEntryOfNode(@NotNull IPrticlEntity entity) {
        return switch (entity) {
            case Node node -> text().content("- ")
                    .append(buildListEntry("ID", node.getId()))
                    .append(buildListEntry("Name", node.getName()))
                    .append(buildListEntry("Owner", node.getPlayer().getUsername()))
                    .append(buildListEntry("World", node.getWorldName()))
                    .append(buildListEntry("Enabled", node.isEnabled()))
                    .build();

            case Trigger trigger -> text().content("- ")
                    .append(buildListEntry("ID", trigger.getId()))
                    .append(buildListEntry("Name", trigger.getName()))
                    .append(buildListEntry("Owner", trigger.getPlayer().getUsername()))
                    .append(buildListEntry("Block", trigger.getBlockName()))
                    .append(buildHoverListEntry("Nodes", trigger.getNodes().stream().map(node -> String.valueOf(node.getId())).collect(Collectors.joining(", "))))
                    .build();

            case Player player -> text().content("- ")
                    .append(buildListEntry("ID", player.getId()))
                    .append(buildListEntry("Username", player.getUsername()))
                    .append(buildListEntry("UUID", player.getUUID()))
                    .build();

            default -> throw new UnsupportedOperationException(
                    "Listing logic for entity of type '" + entity.getClass().getSimpleName() + "' is not implemented!");
        };
    }

    private TextComponent buildListEntry(String property, Object value) {
        return text().append(text().content(property + ": ").color(color(MessageColors.system)))
                .append(text().content(String.valueOf(value)).color(color(MessageColors.prticlLight)))
                .append(text().content(", "))
                .build();
    }

    private TextComponent buildHoverListEntry(String property, Object value) {
        return text().append(text().content(property + ": ").color(color(MessageColors.system)))
                .append(text().content("[HOVER]").hoverEvent(HoverEvent.showText(text().content(String.valueOf(value)))).color(color(MessageColors.prticlLight)))
                .append(text().content(", "))
                .build();
    }
}