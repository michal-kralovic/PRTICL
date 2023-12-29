package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil.configNodeSectionExists;
import static com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil.loadConfigNodes;
import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * Prticl list command.
 * <br>Displays every single node stored in the config file (limited to the ones made by the calling player if met with insufficient permissions).
 */
public class PrticlListCommand extends PrticlCommand {
    private static Plugin plugin;

    public PrticlListCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (sender instanceof Player || sender instanceof ConsoleCommandSender) {
            if (!configNodeSectionExists(sender)) {
                return true;
            }

            if (sender.isOp() || sender instanceof ConsoleCommandSender)
                return adminListLogic(args, sender);
            if (!sender.isOp())
                return playerListLogic(args, sender);
        }
        sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
        return true;
    }

    private static boolean adminListLogic(String[] args, CommandSender sender) {
        Map<String, Object> allNodes = loadConfigNodes();
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

    private static boolean playerListLogic(String[] args, CommandSender sender) {
        Map<String, Object> allNodes = loadConfigNodes();
        List<String[]> listOfListableNodeData = new ArrayList<>();

        for (Map.Entry<String, Object> entry : allNodes.entrySet()) {
            MemorySection particle = (MemorySection) entry.getValue();

            if (particle.get(NODE_PARAM_OWNER).toString().equals(sender.getName())) {
                String[] nodeValues = new String[]{
                        particle.get(NODE_PARAM_ID).toString(),
                        particle.get(NODE_PARAM_OWNER).toString(),
                        particle.get(NODE_PARAM_NAME).toString(),
                        particle.get(NODE_PARAM_PARTICLE_TYPE).toString()
                };
                listOfListableNodeData.add(nodeValues);
            }
        }
        return mainListLogic(listOfListableNodeData, args, sender);
    }

    private static boolean mainListLogic(List<String[]> listOfListableNodeData, String[] args, CommandSender sender) {
        BaseMessageComponents prticlMessage = new BaseMessageComponents();
        int visibleNodeAmount = listOfListableNodeData.size();
        int pageAmount = listOfListableNodeData.size() / 10;

        if (listOfListableNodeData.size() % 10 > 0) {
            pageAmount++;
        }

        if (args.length == 1 || args[1].isBlank()) {
            int maxNode = Math.min(10, visibleNodeAmount);
            sender.sendMessage(prticlMessage.list(listOfListableNodeData.subList(0, maxNode), 1, pageAmount, visibleNodeAmount));
        } else {
            try {
                int pageNumber = Integer.parseInt(args[1]);
                if (pageNumber > pageAmount || pageNumber <= 0) {
                    sender.sendMessage(prticlMessage.error("Invalid page number! (1 - " + pageAmount + ")"));
                    return true;
                }

                int maxNode = Math.min(pageNumber * 10, visibleNodeAmount);
                sender.sendMessage(prticlMessage.list(listOfListableNodeData.subList(((pageNumber * 10) - 10), maxNode), pageNumber, pageAmount, visibleNodeAmount));
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
