package com.minkuh.prticl.nodes.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.schedulers.PrticlScheduler;
import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import com.minkuh.prticl.systemutil.exceptions.NodeNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * A Command for handling the spawning of PrticlNodes.
 */
public class PrticlSpawnCommand extends PrticlCommand {
    private final Prticl plugin;
    private final FileConfiguration config;
    private PrticlNodeConfigUtil configUtil;
    private PrticlDatabase prticlDb;

    public PrticlSpawnCommand(Prticl plugin) throws SQLException {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.configUtil = new PrticlNodeConfigUtil(plugin);
        this.prticlDb = new PrticlDatabase(this.plugin);
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        boolean isPlayerCreated = false;
        boolean isPlayerInDatabase = prticlDb.getPlayerScripts().isPlayerInDatabase((Player) sender);

        if (!isPlayerInDatabase)
            isPlayerCreated = prticlDb.getPlayerScripts().createPlayer((Player) sender);

        isPlayerInDatabase = prticlDb.getPlayerScripts().isPlayerInDatabase((Player) sender);

        sender.sendMessage(prticlMessage.player("isPlayerCreated: " + isPlayerCreated + "\nisPlayerInDatabase: " + isPlayerInDatabase));

        return isPlayerCreated && isPlayerInDatabase;


//        if (isCommandSentByPlayer(sender) && args.length == 1) {
//            PrticlNode node;
//
//            try {
//                plugin.reloadConfig();
//                node = isAnId(args[0])
//                        ? configUtil.getNodeFromConfigById(config, Integer.parseInt(args[0].substring(3)))
//                        : configUtil.getNodeFromConfigByName(args[0]);
//            } catch (NodeNotFoundException e) {
//                sender.sendMessage(prticlMessage.error(NODE_WITH_ARGUMENT_NOT_FOUND));
//                return true;
//            } catch (NumberFormatException e) {
//                sender.sendMessage(prticlMessage.error(INCORRECT_NODE_ID_FORMAT));
//                return true;
//            } catch (Exception e) {
//                sender.sendMessage(prticlMessage.error(e.getMessage()));
//                return true;
//            }
//
//            if (node.getLocation() == null)
//                node.setLocation(((Player) sender).getLocation());
//
//            sender.sendMessage(prticlMessage.player("Spawned '" + node.getName() + "', ID " + node.getId()));
//            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlScheduler(node), 0, node.getRepeatDelay());
//            return true;
//        }
//
//        sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
//        return true;
    }

    public static String getCommandName() {
        return SPAWN_COMMAND;
    }

    private static boolean isAnId(String arg) {
        return arg.startsWith("id:");
    }
}
