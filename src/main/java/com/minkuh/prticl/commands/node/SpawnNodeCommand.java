package com.minkuh.prticl.commands.node;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.PrticlCommand;
import com.minkuh.prticl.data.caches.SpawnedNodesCache;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.schedulers.PrticlSpawner;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static com.minkuh.prticl.common.resources.PrticlConstants.*;

/**
 * A Command for handling the spawning of PrticlNodes.
 */
public class SpawnNodeCommand extends PrticlCommand {
    private final Prticl plugin;
    private final PrticlDatabase prticlDb;
    private final PrticlSpawner spawner;

    public SpawnNodeCommand(Prticl plugin) throws SQLException {
        this.plugin = plugin;
        this.prticlDb = new PrticlDatabase(this.plugin);
        this.spawner = new PrticlSpawner(plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender) && args.length == 1) {
            var nodeOpt = getNodeFromDatabase(args[0], sender);

            if (nodeOpt.isEmpty()) {
                var argStrippedOfIdIfPresent = args[0].startsWith("id:") ? args[0].substring("id:".length()) : args[0];
                sender.sendMessage(prticlMessage.warning("Unable to find a node in cache using id/name: " + argStrippedOfIdIfPresent));
                return true;
            }
            var node = nodeOpt.get();

            try {
                prticlDb.getNodeFunctions().setEnabled(node, true);
                spawner.spawnNode(node);
                SpawnedNodesCache.getInstance().addToCache(node);
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
        return listEntryOfNodeHelp(
                SpawnNodeCommand.getCommandName(),
                "Spawns a prticl node based on a given id/name.",
                "/prticl node spawn <(id:X) or (node name)>",
                "/prticl node spawn id:1"
        );
    }

    private Optional<Node> getNodeFromDatabase(String arg, CommandSender sender) {
        try {
            return arg.startsWith("id:")
                    ? prticlDb.getNodeFunctions().getById(Integer.parseInt(arg.substring("id:".length())))
                    : prticlDb.getNodeFunctions().getByName(arg);
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
            if (prticlDb.getNodeFunctions().isNodeEnabled(node)) {
                plugin.getLogger().warning("Node is enabled in the database. Reverting...");
                prticlDb.getNodeFunctions().setEnabled(node, false);
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