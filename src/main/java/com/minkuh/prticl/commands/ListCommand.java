package com.minkuh.prticl.commands;

import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.common.PaginatedResult;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.data.database.entities.IPrticlEntity;
import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entities.Trigger;
import com.minkuh.prticl.data.database.entity_util.PlayerBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import static com.minkuh.prticl.common.PrticlConstants.INCORRECT_COMMAND_SYNTAX_OR_OTHER;
import static com.minkuh.prticl.common.PrticlConstants.LIST_COMMAND;
import static com.minkuh.prticl.data.database.PrticlDatabase.PRTICL_DATABASE_ENTITIES;

/**
 * Prticl list command.<br>
 * Displays every single entity stored in the database <br/>
 * (limited to the ones made by the calling player if met with insufficient permissions).
 */
public class ListCommand extends PrticlCommand {
    private static final PrticlMessages prticlMessage = new PrticlMessages();
    private final PrticlDatabase prticlDatabase;

    public ListCommand() {
        this.prticlDatabase = new PrticlDatabase();
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        try {
            IPrticlEntity entityToUse = null;

            for (var clazz : PRTICL_DATABASE_ENTITIES) {
                String clazzName = clazz.getClass().getSimpleName();

                if (args[0].equalsIgnoreCase(clazzName) || args[0].equalsIgnoreCase(clazzName.substring(0, 1))) {
                    entityToUse = clazz;
                    break;
                }
            }

            if (entityToUse == null) {
                sender.sendMessage(prticlMessage.error("No such entity: " + args[0]));
                return true;
            }

            return listEntities(entityToUse, args, sender);
        } catch (Exception ex) {
            sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
            return true;
        }
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            return List.of("page");
        }

        return List.of();
    }

    @Override
    public TextComponent.Builder getHelpDescription() {
        return createHelpSectionForCommand(
                ListCommand.getCommandName(),
                "Lists out all the available entities.",
                "/prticl <entity> list (page number)",
                "/prticl player list 1"
        );
    }

    private boolean listEntities(IPrticlEntity entity, String[] args, CommandSender sender) {
        boolean isSenderOp = sender.isOp();
        int page;

        try {
            if (args.length == 3) {
                page = Integer.parseInt(args[2]);
                page = page == 1 ? 0 : page;
            } else {
                page = 0;
            }

        } catch (NumberFormatException ex) {
            sender.sendMessage(prticlMessage.error("Incorrect page number!"));
            return true;
        }

        var player = PlayerBuilder.fromBukkitPlayer((Player) sender);
        PaginatedResult<IPrticlEntity> paginatedResult;
        String entityName;

        // TODO: Change once permissions are implemented
        switch (entity) {
            case Node node -> {
                paginatedResult = isSenderOp
                        ? prticlDatabase.getNodeFunctions().getByPage(page)
                        : prticlDatabase.getNodeFunctions().getByPageForPlayer(page, player);
                entityName = node.getClass().getSimpleName();
            }

            case Trigger trigger -> {
                paginatedResult = isSenderOp
                        ? prticlDatabase.getTriggerFunctions().getByPage(page)
                        : prticlDatabase.getTriggerFunctions().getByPageForPlayer(page, player);
                entityName = trigger.getClass().getSimpleName();
            }

            case com.minkuh.prticl.data.database.entities.Player sPlayer -> {
                paginatedResult = prticlDatabase.getPlayerFunctions().getByPage(page);
                entityName = sPlayer.getClass().getSimpleName();
            }

            default -> throw new UnsupportedOperationException(
                    "Listing logic for entity of type '" + entity.getClass().getSimpleName() + "' is not implemented!");
        }

        return mainListLogic(paginatedResult, entityName, sender);
    }

    private static boolean mainListLogic(@NotNull PaginatedResult<IPrticlEntity> entities, String entityName, CommandSender sender) {
        int page = entities.getPage();
        int pageAmount = entities.getTotalPages();

        if (pageAmount == 0) {
            sender.sendMessage(prticlMessage.player("No " + entityName.toLowerCase(Locale.ROOT) + "s " + "to display."));
        } else if (page <= pageAmount && page >= 0) {
            sender.sendMessage(prticlMessage.listEntities(entities));
        } else {
            sender.sendMessage(prticlMessage.error("Invalid page number! (" + 1 + " to " + pageAmount + ")"));
        }

        return true;
    }

    public static String getCommandName() {
        return LIST_COMMAND;
    }
}