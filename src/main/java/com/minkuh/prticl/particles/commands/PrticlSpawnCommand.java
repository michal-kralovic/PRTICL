package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.particles.PrticlNode;
import com.minkuh.prticl.particles.schedulers.PrticlScheduler;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PrticlSpawnCommand extends PrticlCommand {

    private final Plugin plugin;

    public PrticlSpawnCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (sender instanceof Player && (args.length == 2 || args.length == 3)) {
            if (EnumUtils.isValidEnum(Particle.class, args[1])) {
                PrticlNode node = new PrticlNode();

                node.setLocation(((Player) sender).getLocation());
                node.setParticleType(Particle.valueOf(args[1]));
                node.setCreator(((Player) sender).getPlayer());

                if (args.length == 3) {
                    try {
                        node.setRepeatDelay(Integer.parseInt(args[2]) * 20);
                    } catch (NumberFormatException e) {
                        messageComponents.prticlErrorMessage("Incorrect repeat delay input! (in seconds)");
                    }
                }

                sender.sendMessage(messageComponents.prticlPlayerMessage("Created: " + node));

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
