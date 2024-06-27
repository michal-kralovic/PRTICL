package com.minkuh.prticl.nodes.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.data.wrappers.PaginatedResult;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * Prticl list command.<br>
 * Displays every single node stored in the config file
 * (limited to the ones made by the calling player if met with insufficient permissions).<br><br>
 * This command can be run via the console as well.
 */
public class PrticlListCommand extends PrticlCommand {
    private static final BaseMessageComponents prticlMessage = new BaseMessageComponents();
    private static PrticlNodeConfigUtil configUtil;
    private final PrticlDatabase prticlDatabase;

    public PrticlListCommand(Prticl plugin) throws SQLException {
        this.configUtil = new PrticlNodeConfigUtil(plugin);
        this.prticlDatabase = new PrticlDatabase(plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (args.length == 0 || args.length == 1)
            // TODO: Change once permissions are implemented
            return sender.isOp() ? listAllNodes(args, sender) : listOwnNodes(args, sender);

        sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
        return true;
    }

    /**
     * A utility logic method that collects every single node from the config and passes it to the method that
     * lists these nodes out.
     *
     * @param args   Player input to pass to the listing method
     * @param sender The sender of the command
     * @return TRUE if succeeded.
     */
    private boolean listAllNodes(String[] args, CommandSender sender) {
        int page;
        try {
            page = args.length == 1
                    ? Integer.parseInt(args[0])
                    : 0;
        } catch (NumberFormatException ex) {
            sender.sendMessage(prticlMessage.error(INCORRECT_PAGE_INPUT));
            return true;
        }

        PaginatedResult<PrticlNode> nodesOfPage;
        try {
            nodesOfPage = prticlDatabase.getNodeFunctions().getNodesByPage(page);
        } catch (SQLException ex) {
            sender.sendMessage(prticlMessage.error("Unexpected error while retrieving nodes from the database!"));
            return true;
        }

        return mainListLogic(nodesOfPage, sender);
    }

    /**
     * A utility logic method that collects nodes from the config that are owned by the sender player
     * and passes it to the method that lists these nodes out.
     *
     * @param args   Player input to pass to the listing method
     * @param sender The sender of the command
     * @return TRUE if succeeded.
     */
    private boolean listOwnNodes(String[] args, CommandSender sender) {
        int page;
        try {
            page = args.length == 1
                    ? Integer.parseInt(args[0])
                    : 0;
        } catch (NumberFormatException ex) {
            sender.sendMessage(prticlMessage.error(INCORRECT_PAGE_INPUT));
            return true;
        }

        PaginatedResult<PrticlNode> nodesOfPage;
        try {
            nodesOfPage = prticlDatabase.getNodeFunctions().getNodesByPageByPlayer(page, ((Player) sender).getUniqueId());
        } catch (SQLException ex) {
            sender.sendMessage(prticlMessage.error("Unexpected error while retrieving nodes from the database!"));
            return true;
        }

        return mainListLogic(nodesOfPage, sender);
    }

    /**
     * The main utility logic method that lists all the nodes out.
     *
     * @param paginatedNodes The collected nodes from a previous util method
     * @param sender         The sender of the command
     * @return TRUE if succeeded.
     */
    @SuppressWarnings("SameReturnValue")
    private static boolean mainListLogic(PaginatedResult<PrticlNode> paginatedNodes, CommandSender sender) {
        var page = paginatedNodes.getPage() == 0 ? 1 : paginatedNodes.getPage();
        int pageAmount = paginatedNodes.getTotalPages();

        if (page <= pageAmount && page > 0) {
            sender.sendMessage(prticlMessage.listNodes(paginatedNodes, page, pageAmount));
        } else {
            int noPagesOrFirstPage = pageAmount == 0 ? 0 : 1;
            sender.sendMessage(prticlMessage.error("Invalid page number! (" + noPagesOrFirstPage + " to " + pageAmount + ")"));
        }
        return true;
    }

    public static String getCommandName() {
        return LIST_COMMAND;
    }
}