package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.particles.schedulers.PrticlScheduler;
import com.minkuh.prticl.systemutil.exceptions.NodeNotFoundException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.rmi.StubNotFoundException;

import static com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil.getNodeFromConfigById;

/**
 * A Command for handling the creation of PrticlNodes.
 */
public class PrticlSpawnCommand extends PrticlCommand {
    private final Plugin plugin;
    private final FileConfiguration config;

    public PrticlSpawnCommand(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender) && args.length == 2) {
            PrticlNode node = new PrticlNode();

            try {
                node = getNodeFromConfigById(config, Integer.parseInt(args[1]));
            } catch (NumberFormatException e) {
                sender.sendMessage(prticlMessage.error("Incorrect node ID format!"));
                return true;
            } catch (NodeNotFoundException e) {
                sender.sendMessage(prticlMessage.error("Couldn't find a node with ID " + node.getId() + "!"));
                return true;
            } catch (Exception e) {
                sender.sendMessage(prticlMessage.error(e.getMessage()));
            }

            if (node.getLocation() == null) {
                node.setLocation(((Player) sender).getLocation());
            }

            sender.sendMessage(prticlMessage.player("Spawned " + node.getName() + ", ID " + node.getId()));
            Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlScheduler(node), 0, node.getRepeatDelay());
            return true;
        }
        return false;
    }

    @Override
    String getCommandName() {
        return "spawn";
    }
}
