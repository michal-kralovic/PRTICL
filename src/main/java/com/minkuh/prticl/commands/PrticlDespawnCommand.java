package com.minkuh.prticl.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.PrticlNode;
import com.minkuh.prticl.data.caches.SpawnedNodesCache;
import com.minkuh.prticl.data.database.PrticlDatabase;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static com.minkuh.prticl.common.resources.PrticlConstants.*;

/**
 * A Command for handling the despawning of PrticlNodes.
 */
public class PrticlDespawnCommand extends PrticlCommand {
    private final Prticl plugin;
    private final PrticlDatabase prticlDb;

    public PrticlDespawnCommand(Prticl plugin) throws SQLException {
        this.plugin = plugin;
        this.prticlDb = new PrticlDatabase(this.plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender) && args.length == 1) {
            if (!SpawnedNodesCache.getInstance().any()) {
                sender.sendMessage(prticlMessage.player("There are no spawned nodes."));
                return true;
            }

            var nodeOpt = getNodeFromCache(args[0]);
            if (nodeOpt.isEmpty()) {
                var argStrippedOfIdIfPresent = args[0].startsWith("id:") ? args[0].substring("id:".length()) : args[0];
                sender.sendMessage(prticlMessage.error("Unable to find node in cache using id/name: " + argStrippedOfIdIfPresent));
                return true;
            }
            var node = nodeOpt.get();

            try {
                prticlDb.getNodeFunctions().setEnabled(node, false);
                SpawnedNodesCache.getInstance().remove(node);
            } catch (SQLException e) {
                sender.sendMessage(prticlMessage.warning("Couldn't set the spawned node's isEnabled value to false! (continuing...)"));
            }

            sender.sendMessage(prticlMessage.player("Despawned '" + node.getName() + '\''));
            return true;
        }

        sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
        return true;
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            List<String> spawnedNodeNames = SpawnedNodesCache.getInstance().getAll().stream().map(PrticlNode::getName).toList();
            var sortedNodeNames = new ArrayList<String>();

            StringUtil.copyPartialMatches(args[1], spawnedNodeNames, sortedNodeNames);

            return sortedNodeNames;
        }

        return List.of();
    }

    private Optional<PrticlNode> getNodeFromCache(String arg) {
        try {
            return arg.toLowerCase(Locale.ROOT).startsWith("id:")
                    ? SpawnedNodesCache.getInstance().get(Integer.parseInt(arg.substring("id:".length())))
                    : SpawnedNodesCache.getInstance().get(arg);
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static String getCommandName() {
        return DESPAWN_COMMAND;
    }
}