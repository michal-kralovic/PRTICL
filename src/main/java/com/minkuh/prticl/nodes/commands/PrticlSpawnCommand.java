package com.minkuh.prticl.nodes.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.nodes.logic.PrticlNodeValidation;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.schedulers.PrticlScheduler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * A Command for handling the spawning of PrticlNodes.
 */
public class PrticlSpawnCommand extends PrticlCommand {
    private final Prticl plugin;
    private final PrticlDatabase prticlDb;

    public PrticlSpawnCommand(Prticl plugin) throws SQLException {
        this.plugin = plugin;
        this.prticlDb = new PrticlDatabase(this.plugin);
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender) && args.length == 1) {
            PrticlNode node;

            try {
                node = PrticlNodeValidation.isArgAnId(args[0])
                        ? prticlDb.getNodeFunctions().getNodeById(Integer.parseInt(args[0].substring("id:".length())))
                        : prticlDb.getNodeFunctions().getNodeByName(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(prticlMessage.error(INCORRECT_NODE_ID_FORMAT));
                return true;
            } catch (Exception e) {
                sender.sendMessage(prticlMessage.error(e.getMessage()));
                return true;
            }

            if (node.getLocationObject().getLocation() == null)
                node.getLocationObject().setLocation(((Player) sender).getLocation());

            sender.sendMessage(prticlMessage.player("Spawned '" + node.getName() + "', ID " + node.getId() + "in world: " + node.getLocationObject().getLocation().getWorld() + "\nAvailable worlds: " + Bukkit.getServer().getWorlds()));
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlScheduler(node), 0, node.getRepeatDelay());
            return true;
        }

        sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
        return true;
    }

    public static String getCommandName() {
        return SPAWN_COMMAND;
    }
}
