package com.minkuh.prticl.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.common.wrappers.PaginatedResult;
import com.minkuh.prticl.common.PrticlNode;
import com.minkuh.prticl.common.message.PrticlMessages;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;

import static com.minkuh.prticl.common.resources.PrticlConstants.*;

/**
 * Prticl list command.<br>
 * Displays every single node stored in the config file
 * (limited to the ones made by the calling player if met with insufficient permissions).<br><br>
 * This command can be run via the console as well.
 */
public class PrticlListCommand extends PrticlCommand {
    private static final PrticlMessages prticlMessage = new PrticlMessages();
    private final PrticlDatabase prticlDatabase;

    public PrticlListCommand(Prticl plugin) throws SQLException {
        this.prticlDatabase = new PrticlDatabase(plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (args.length == 0 || args.length == 1)
            return listNodes(args, sender);

        sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
        return true;
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
        return listEntryOfNodeHelp(
                PrticlListCommand.getCommandName(),
                "Lists out all the available prticl nodes.",
                "/prticl node list <page number>",
                "/prticl node list 1"
        );
    }

    /**
     * A utility logic method that collects every single node from the config and passes it to the method that
     * lists these nodes out. Contains conditional logic for OP player checking.
     *
     * @param args   Player input to pass to the listing method
     * @param sender The sender of the command
     * @return TRUE if succeeded.
     */
    private boolean listNodes(String[] args, CommandSender sender) {
        int page;
        try {
            if (args.length == 1) {
                page = Integer.parseInt(args[0]);
                page = page == 1 ? 0 : page;
            } else {
                page = 0;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(prticlMessage.error(INCORRECT_PAGE_INPUT));
            return true;
        }

        PaginatedResult<PrticlNode> nodePage;
        try {
            // TODO: Change once permissions are implemented
            nodePage = sender.isOp()
                    ? prticlDatabase.getNodeFunctions().getNodesByPage(page)
                    : prticlDatabase.getNodeFunctions().getNodesByPageByPlayer(page, ((Player) sender).getUniqueId());
        } catch (SQLException ex) {
            sender.sendMessage(prticlMessage.error(ex.getMessage()));
            return true;
        }

        return mainListLogic(nodePage, sender);
    }

    /**
     * The main utility logic method that lists all the nodes out.
     *
     * @param nodes  The page of nodes from the DB
     * @param sender The sender of the command
     * @return TRUE if succeeded.
     */
    @SuppressWarnings("SameReturnValue")
    private static boolean mainListLogic(PaginatedResult<PrticlNode> nodes, CommandSender sender) {
        int page = nodes.getPage();
        int pageAmount = nodes.getTotalPages();

        if (page <= pageAmount && page >= 0) {
            sender.sendMessage(prticlMessage.listNodes(nodes));
        } else {
            sender.sendMessage(prticlMessage.error("Invalid page number! (" + (pageAmount == 0 ? 0 : 1) + " to " + pageAmount + ")"));
        }
        return true;
    }

    public static String getCommandName() {
        return LIST_COMMAND;
    }
}