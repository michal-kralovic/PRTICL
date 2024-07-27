package com.minkuh.prticl.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.PrticlNode;
import com.minkuh.prticl.data.caches.SpawnedNodesCache;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.schedulers.PrticlSpawner;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;

import static com.minkuh.prticl.common.resources.PrticlConstants.*;

/**
 * A Command for handling the spawning of PrticlNodes.
 */
public class PrticlSpawnCommand extends PrticlCommand {
    private final Prticl plugin;
    private final PrticlDatabase prticlDb;
    private final PrticlSpawner spawner;

    public PrticlSpawnCommand(Prticl plugin) throws SQLException {
        this.plugin = plugin;
        this.prticlDb = new PrticlDatabase(this.plugin);
        this.spawner = new PrticlSpawner(plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender) && args.length == 1) {
            PrticlNode node = getNodeFromDatabase(args[0], sender);

            if (node == null)
                return true;

            if (node.getLocationObject().getLocation() == null)
                node.getLocationObject().setLocation(((Player) sender).getLocation());

            try {
                prticlDb.getNodeFunctions().setEnabled(node, true);
            } catch (SQLException e) {
                sender.sendMessage(prticlMessage.warning("Failed to toggle node's 'isEnabled'! (continuing...)"));
            }

            spawner.spawnNode(node);
            SpawnedNodesCache.getInstance().addToCache(node);

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

    private PrticlNode getNodeFromDatabase(String arg, CommandSender sender) {
        try {
            return arg.startsWith("id:")
                    ? prticlDb.getNodeFunctions().getNodeById(Integer.parseInt(arg.substring("id:".length())))
                    : prticlDb.getNodeFunctions().getNodeByName(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(prticlMessage.error(INCORRECT_NODE_ID_FORMAT));
        } catch (Exception e) {
            sender.sendMessage(prticlMessage.error(e.getMessage()));
        }

        return null;
    }

    public static String getCommandName() {
        return SPAWN_COMMAND;
    }
}