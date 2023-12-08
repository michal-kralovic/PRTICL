package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.particles.prticl.PrticlSpawner;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Provides a configurable module-based way of creating a PRTICL node before spawning it.
 */
public class PrticlCreateCommand extends PrticlCommand {
    private Plugin plugin;

    public PrticlCreateCommand(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender)) {
            return switch (args.length) {
                case 2 -> {
                    if (args[1].length() > 50) {
                        sender.sendMessage(messageComponents.prticlErrorMessage("Prticl node name can't be longer than 50 characters!"));
                        yield true;
                    }
                    if (args[1].isBlank()) {
                        sender.sendMessage(messageComponents.prticlErrorMessage("Prticle node name can't be empty!"));
                        yield true;
                    }

                    PrticlSpawner.createPrticl(args[1], sender.getName());
                    sender.sendMessage(messageComponents.prticlPlayerMessage("Created the node."));
                    yield true;
                }
                case 3 -> {
                    if (!EnumUtils.isValidEnum(Particle.class, args[2])) {
                        sender.sendMessage(messageComponents.prticlErrorMessage("Your input contains an incorrect particle!"));
                        yield true;
                    }

                    PrticlSpawner.createPrticl(args[1], Particle.valueOf(args[2]), sender.getName());
                    sender.sendMessage(messageComponents.prticlPlayerMessage("Created the node."));
                    yield true;
                }
                default ->  {
                    sender.sendMessage(messageComponents.prticlErrorMessage("Unexpected error!"));
                    yield false;
                }
            };
        }
        return false;
    }

    @Override
    String getCommandName() {
        return "create";
    }
}
