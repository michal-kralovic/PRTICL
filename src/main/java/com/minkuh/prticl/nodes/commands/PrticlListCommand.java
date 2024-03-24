package com.minkuh.prticl.nodes.commands;

import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * Prticl list command.<br>
 * Displays every single node stored in the config file
 * (limited to the ones made by the calling player if met with insufficient permissions).<br><br>
 * This command can be run via the console as well.
 */
public class PrticlListCommand extends PrticlCommand {
    private static final BaseMessageComponents prticlMessage = new BaseMessageComponents();
    private static Plugin plugin;
    private static PrticlNodeConfigUtil configUtil;

    public PrticlListCommand(Plugin plugin) {
        this.plugin = plugin;
        this.configUtil = new PrticlNodeConfigUtil(plugin);
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (!configUtil.configNodeSectionExists(sender))
            return true;

        if (args.length == 0 || args.length == 1)
            // TODO: Change once permissions are implemented
            return sender.isOp() ? listAllNodes(args, sender) : listPlayerOwnedNodes(args, sender);

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
    private static boolean listAllNodes(String[] args, CommandSender sender) {
        Map<String, Object> allNodes = configUtil.getConfigNodes();
        if (allNodes.isEmpty()) {
            sender.sendMessage(prticlMessage.system("No nodes found."));
            return true;
        }
        List<String[]> listOfListableNodeData = new ArrayList<>();

        for (Map.Entry<String, Object> entry : allNodes.entrySet()) {
            MemorySection particle = (MemorySection) entry.getValue();

            String[] nodeValues = new String[]{
                    particle.get(NODE_PARAM_ID).toString(),
                    particle.get(NODE_PARAM_OWNER).toString(),
                    particle.get(NODE_PARAM_NAME).toString(),
                    particle.get(NODE_PARAM_PARTICLE_TYPE).toString()
            };

            listOfListableNodeData.add(nodeValues);
        }
        return mainListLogic(listOfListableNodeData, args, sender);
    }

    /**
     * A utility logic method that collects nodes from the config that are owned by the sender player
     * and passes it to the method that lists these nodes out.
     *
     * @param args   Player input to pass to the listing method
     * @param sender The sender of the command
     * @return TRUE if succeeded.
     */
    private static boolean listPlayerOwnedNodes(String[] args, CommandSender sender) {
        Map<String, Object> allNodes = configUtil.getConfigNodes();
        if (allNodes.isEmpty()) {
            sender.sendMessage(prticlMessage.system("No nodes found."));
            return true;
        }
        List<String[]> listOfListableNodeData = new ArrayList<>();

        for (Map.Entry<String, Object> entry : allNodes.entrySet()) {
            MemorySection node = (MemorySection) entry.getValue();

            if (node.get(NODE_PARAM_OWNER).toString().equals(sender.getName())) {
                String[] nodeValues = new String[]{
                        node.get(NODE_PARAM_ID).toString(),
                        node.get(NODE_PARAM_OWNER).toString(),
                        node.get(NODE_PARAM_NAME).toString(),
                        node.get(NODE_PARAM_PARTICLE_TYPE).toString()
                };
                listOfListableNodeData.add(nodeValues);
            }
        }

        return mainListLogic(listOfListableNodeData, args, sender);
    }

    /**
     * The main utility logic method that lists all the nodes out.
     *
     * @param listOfListableNodeData The collected nodes from a previous util method
     * @param args                   Player input
     * @param sender                 The sender of the command
     * @return TRUE if succeeded.
     */
    private static boolean mainListLogic(List<String[]> listOfListableNodeData, String[] args, CommandSender sender) {
        int visibleNodeAmount = listOfListableNodeData.size();
        int pageAmount = listOfListableNodeData.size() / 10;

        if (listOfListableNodeData.size() % 10 > 0) {
            pageAmount++;
        }

        if (args.length == 0) { // show the first page if no page number arg present
            int maxNode = Math.min(10, visibleNodeAmount);
            sender.sendMessage(prticlMessage.list(listOfListableNodeData.subList(0, maxNode), 1, pageAmount, visibleNodeAmount));
        } else {
            try {
                int pageNumber = Integer.parseInt(args[0]);
                if (pageNumber <= pageAmount && pageNumber > 0) {
                    int maxNode = Math.min(pageNumber * 10, visibleNodeAmount);
                    sender.sendMessage(prticlMessage.list(listOfListableNodeData.subList(((pageNumber * 10) - 10), maxNode), pageNumber, pageAmount, visibleNodeAmount));
                } else {
                    int noPagesOrFirstPage = pageAmount == 0 ? 0 : 1;
                    sender.sendMessage(prticlMessage.error("Invalid page number! (" + noPagesOrFirstPage + " to " + pageAmount + ")"));
                    return true;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(prticlMessage.error(INCORRECT_PAGE_INPUT));
            }
        }
        return true;
    }

    public static String getCommandName() {
        return LIST_COMMAND;
    }
}
