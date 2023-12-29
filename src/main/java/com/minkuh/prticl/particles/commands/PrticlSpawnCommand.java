package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.particles.schedulers.PrticlScheduler;
import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import com.minkuh.prticl.systemutil.exceptions.NodeNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.INCORRECT_COMMAND_SYNTAX_OR_OTHER;
import static com.minkuh.prticl.systemutil.resources.PrticlStrings.SPAWN_COMMAND;

/**
 * A Command for handling the creation of PrticlNodes.
 */
public class PrticlSpawnCommand extends PrticlCommand {
    private final Plugin plugin;
    private final FileConfiguration config;
    private final PrticlNodeConfigUtil configUtil;

    public PrticlSpawnCommand(Plugin plugin) {
        this.plugin = plugin;
        this.configUtil = new PrticlNodeConfigUtil(plugin);
        this.config = plugin.getConfig();
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender) && args.length == 2) {
            PrticlNode node;

            try {
                plugin.reloadConfig();
                node = PrticlNodeConfigUtil.getNodeFromConfigById(config, Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                sender.sendMessage(prticlMessage.error("Incorrect node ID format!"));
                return true;
            } catch (NodeNotFoundException e) {
                sender.sendMessage(prticlMessage.error("Couldn't find a node with ID " + args[1] + "!"));
                return true;
            } catch (Exception e) {
                sender.sendMessage(prticlMessage.error(e.getMessage()));
                return true;
            }

            if (node.getLocation() == null) {
                node.setLocation(((Player) sender).getLocation());
            }

            sender.sendMessage(prticlMessage.player("Spawned '" + node.getName() + "', ID " + node.getId()));
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
