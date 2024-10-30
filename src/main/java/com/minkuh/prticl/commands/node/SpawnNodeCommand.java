package com.minkuh.prticl.commands.node;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.PrticlCommand;
import com.minkuh.prticl.common.PrticlSpawner;
import com.minkuh.prticl.data.caches.CacheManager;
import com.minkuh.prticl.data.caches.NodeChunkLocationsCache;
import com.minkuh.prticl.data.caches.SpawnedNodesCache;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.functions.PrticlNodeFunctions;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static com.minkuh.prticl.common.PrticlConstants.*;

/**
 * A Command for handling the spawning of PrticlNodes.
 */
public class SpawnNodeCommand extends PrticlCommand {
    private final Prticl plugin;
    private final PrticlSpawner spawner;

    private final PrticlNodeFunctions nodeFunctions;

    public SpawnNodeCommand(Prticl plugin) {
        this.plugin = plugin;
        this.nodeFunctions = new PrticlDatabase().getNodeFunctions();
        this.spawner = new PrticlSpawner(plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender) && args.length == 1) {
            var nodeOpt = getNodeFromCache(args[0], sender);

            if (nodeOpt.isEmpty()) {
                var argStrippedOfIdIfPresent = args[0].startsWith("id:") ? args[0].substring("id:".length()) : args[0];
                sender.sendMessage(prticlMessage.warning("Node with id/name: '" + argStrippedOfIdIfPresent + "' doesn't exist"));
                return true;
            }

            var node = nodeOpt.get();

            // TODO: Figure out a way to NOT trigger onChunkLoad during a call to isInCache
            if (!NodeChunkLocationsCache.getInstance().isInCache(node)) {
                NodeChunkLocationsCache.getInstance().add(node);
            }

            if (SpawnedNodesCache.getInstance().isInCache(node)) {
                sender.sendMessage(prticlMessage.warning("Can't spawn an already spawned node!"));
                return true;
            }

            try {
                nodeFunctions.setEnabled(node, true);
                spawner.spawnNode(node);
                CacheManager.spawnInAllCaches(node);
            } catch (Exception ex) {
                plugin.getLogger().severe("Encountered an issue when spawning a node!\nIssue: " + ex.getMessage());
                performRollback(node);
            }

            sender.sendMessage(prticlMessage.player("Spawned '" + node.getName() + '\''));
            return true;
        }

        sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
        return true;
    }

    @Override
    public List<String> getTabCompletion(String @NotNull [] args) {
        if (args.length == 2) return List.of(NODE_PARAM_ID + '/' + NODE_PARAM_NAME);
        return List.of();
    }

    @Override
    public TextComponent.Builder getHelpDescription() {
        return createHelpSectionForCommand(
                SpawnNodeCommand.getCommandName(),
                "Spawns a prticl node based on a given id/name.",
                "/prticl node spawn <(id:X) or (node name)>",
                "/prticl node spawn id:1"
        );
    }

    private Optional<Node> getNodeFromCache(String arg, CommandSender sender) {
        try {
            return arg.startsWith("id:")
                    ? NodeChunkLocationsCache.getInstance().getNodeById(Integer.parseInt(arg.substring("id:".length())))
                    : NodeChunkLocationsCache.getInstance().getNodeByName(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(prticlMessage.error(INCORRECT_NODE_ID_FORMAT));
        } catch (Exception e) {
            sender.sendMessage(prticlMessage.error(e.getMessage()));
        }

        return Optional.empty();
    }

    private void performRollback(Node node) {
        boolean rolledAnythingBack = false;
        plugin.getLogger().warning("Attempting to roll back side-effects...");

        try {
            // Disable if enabled
            if (nodeFunctions.isNodeEnabled(node)) {
                plugin.getLogger().warning("Node is enabled in the database. Reverting...");
                nodeFunctions.setEnabled(node, false);
                rolledAnythingBack = true;
            }

            // Despawn if spawned
            if (node.isSpawned()) {
                plugin.getLogger().warning("Node is spawned. Despawning...");
                node.setSpawned(false);
                rolledAnythingBack = true;
            }

            // Remove from the cache if present
            if (SpawnedNodesCache.getInstance().isInCache(node)) {
                plugin.getLogger().warning("Node is in the cache. Removing...");
                SpawnedNodesCache.getInstance().remove(node);
                rolledAnythingBack = true;
            }

            plugin.getLogger().warning(rolledAnythingBack ? "Rollback successful!" : "Nothing to roll back. Done!");
        } catch (Exception exc) {
            plugin.getLogger().severe("Encountered an issue during node spawn rollback!\nIssue: " + exc.getMessage());
        }
    }

    public static String getCommandName() {
        return SPAWN_COMMAND;
    }
}