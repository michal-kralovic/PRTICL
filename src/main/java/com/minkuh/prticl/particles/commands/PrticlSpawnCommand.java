package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.particles.prticl.PrticlSpawner;
import com.minkuh.prticl.particles.schedulers.PrticlScheduler;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

/**
 * A Command for handling the creation of PrticlNodes.
 * <br>TODO: Separate logic into PrticlSpawner.
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
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player!");
            return true;
        }

        if (args.length == 2 || args.length == 3) {
            if (EnumUtils.isValidEnum(Particle.class, args[1])) {
                Player player = ((Player) sender).getPlayer();
                PrticlNode node = PrticlSpawner.createPrticl(Particle.valueOf(args[1]), player.getLocation(), player);

                if (args.length == 3) {
                    try {
                        node.setRepeatDelay(Integer.parseInt(args[2]));
                    } catch (NumberFormatException e) {
                        messageComponents.prticlErrorMessage("Incorrect repeat delay input! (in ticks)");
                    }
                }

                sender.sendMessage(messageComponents.prticlPlayerMessage("Created: " + node));
                config.set(plugin.getResource("config.yml").toString(), node.serialize());
                try {
                    config.save(String.valueOf(config));
                } catch (IOException e) {
                    messageComponents.prticlErrorMessage("oof.");
                }

                Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new PrticlScheduler(node), 0, node.getRepeatDelay());
            } else {
                sender.sendMessage(messageComponents.prticlErrorMessage("Invalid particle!"));
            }
            return true;
        }
        return false;
    }

    @Override
    String getCommandName() {
        return "spawn";
    }
}
