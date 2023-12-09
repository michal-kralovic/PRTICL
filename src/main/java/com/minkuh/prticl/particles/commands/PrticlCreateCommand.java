package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.particles.prticl.PrticlNodeBuilder;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import static com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil.saveNodeToConfig;

/**
 * Provides a configurable module-based way of creating a PRTICL node before spawning it.
 */
public class PrticlCreateCommand extends PrticlCommand {
    private Plugin plugin;
    private FileConfiguration config;
    private PrticlNodeBuilder builder;

    public PrticlCreateCommand(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.builder = new PrticlNodeBuilder(plugin);
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender)) {
            if (nameLimitationHandle(args[1], sender))
                return true;

            switch (args.length) {
                case 2 -> {
                    PrticlNode node = builder.setName(args[1])
                                                .setCreatedBy(sender.getName()).build();

                    if (!saveNodeToConfig(config, node))
                        sender.sendMessage(prticlMessage.error("Couldn't save the node to config!"));
                    sender.sendMessage(prticlMessage.player("Created the node."));
                }
                case 3 -> {
                    PrticlNode node = builder.setName(args[1])
                                                .setParticleType(Particle.valueOf(args[2]))
                                                .setCreatedBy(sender.getName()).build();

                    if (!saveNodeToConfig(config, node))
                        sender.sendMessage(prticlMessage.error("Couldn't save the node to config!"));
                    sender.sendMessage(prticlMessage.player("Created the node."));
                }
                case 4 -> {
                    try {
                        PrticlNode node = builder.setName(args[1])
                                                    .setParticleType(Particle.valueOf(args[2]))
                                                    .setRepeatDelay(Integer.parseInt(args[3]))
                                                    .setCreatedBy(sender.getName()).build();

                        if (!saveNodeToConfig(config, node))
                            sender.sendMessage(prticlMessage.error("Couldn't save the node to config!"));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(prticlMessage.error("Incorrect repeat delay! (has to be a number in ticks)"));
                        return true;
                    }
                    sender.sendMessage(prticlMessage.player("Created the node."));
                }
                case 5 -> {
                    try {
                        PrticlNode node = builder.setName(args[1])
                                                    .setParticleType(Particle.valueOf(args[2]))
                                                    .setRepeatDelay(Integer.parseInt(args[3]))
                                                    .setParticleDensity(Integer.parseInt(args[4]))
                                                    .setCreatedBy(sender.getName()).build();
                    } catch (NumberFormatException e) {
                        sender.sendMessage(prticlMessage.error("Incorrect particle density format! (has to be a number)"));
                    }
                }
                default -> sender.sendMessage(prticlMessage.error("Unexpected error!"));
            }
            return true;
        }
        return false;
    }

    private static boolean nameLimitationHandle(String arg, CommandSender sender) {
        BaseMessageComponents messageComponents1 = new BaseMessageComponents();
        boolean result = false;

        if (arg.length() > 50) {
            sender.sendMessage(messageComponents1.error("Prticl node name can't be longer than 50 characters!"));
            result = true;
        }
        if (arg.isBlank()) {
            sender.sendMessage(messageComponents1.error("Prticle node name can't be empty!"));
            result = true;
        }

        return result;
    }

    @Override
    String getCommandName() {
        return "create";
    }
}
