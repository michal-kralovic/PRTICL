package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.particles.prticl.PrticlNodeBuilder;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import static com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil.saveNodeToConfig;
import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * Provides a configurable module-based way of creating a PRTICL node before spawning it.<br><br>
 * In-game usage: <b>/prticl create (name) (particle) (repeat delay in ticks) (particle density) (x) (y) (z)</b><br><i>
 * - where nothing except for the name is necessary to specify.</i><br><br>
 * Example: <b>/prticl create leaf_blower CHERRY_LEAVES 5 5</b>
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
                        sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                    sender.sendMessage(prticlMessage.player(CREATED_NODE));
                }
                case 3 -> {
                    PrticlNode node = builder.setName(args[1])
                                                .setParticleType(Particle.valueOf(args[2]))
                                                .setCreatedBy(sender.getName()).build();

                    if (!saveNodeToConfig(config, node))
                        sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                    sender.sendMessage(prticlMessage.player(CREATED_NODE));
                }
                case 4 -> {
                    try {
                        PrticlNode node = builder.setName(args[1])
                                                    .setParticleType(Particle.valueOf(args[2]))
                                                    .setRepeatDelay(Integer.parseInt(args[3]))
                                                    .setCreatedBy(sender.getName()).build();

                        if (!saveNodeToConfig(config, node))
                            sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(prticlMessage.error("Incorrect repeat delay format! (has to be a number in ticks)"));
                        return true;
                    }
                    sender.sendMessage(prticlMessage.player(CREATED_NODE));
                }
                case 5 -> {
                    try {
                        PrticlNode node = builder.setName(args[1])
                                                    .setParticleType(Particle.valueOf(args[2]))
                                                    .setRepeatDelay(Integer.parseInt(args[3]))
                                                    .setParticleDensity(Integer.parseInt(args[4]))
                                                    .setCreatedBy(sender.getName()).build();

                        if (!saveNodeToConfig(config, node))
                            sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(prticlMessage.error("Incorrect particle density format! (has to be a number)"));
                        return true;
                    }
                    sender.sendMessage(prticlMessage.player(CREATED_NODE));
                }
                default -> sender.sendMessage(prticlMessage.error("Unexpected error!"));
            }
            return true;
        }
        sender.sendMessage(prticlMessage.error(INCORRECT_COMMAND_SYNTAX_OR_OTHER));
        return true;
    }

    /**
     * Utility method to block the player from entering a name that's longer than 50 characters, or an empty one.
     * @param arg The name to be checked
     * @param sender The sender that sent the command
     * @return TRUE if handled.
     */
    private static boolean nameLimitationHandle(String arg, CommandSender sender) {
        BaseMessageComponents messageComponents1 = new BaseMessageComponents();
        boolean result = false;

        if (arg.length() > 50) {
            sender.sendMessage(messageComponents1.error("Prticl node name can't be longer than 50 characters!"));
            result = true;
        }
        if (arg.isBlank()) {
            sender.sendMessage(messageComponents1.error("Prticl node name can't be empty!"));
            result = true;
        }

        return result;
    }

    public static String getCommandName() {
        return CREATE_COMMAND;
    }
}
