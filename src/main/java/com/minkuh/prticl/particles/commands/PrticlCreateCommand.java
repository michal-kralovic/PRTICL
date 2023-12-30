package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.particles.prticl.PrticlNodeBuilder;
import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * Provides a configurable modular way of creating a PRTICL node before spawning it.<br><br>
 * In-game usage: <b>/prticl node create (name) (particle) (repeat delay in ticks) (particle density) (x) (y) (z)</b><br><i>
 * - where nothing except for the name is necessary to specify.</i><br><br>
 * Example: <b>/prticl node create leaf_blower CHERRY_LEAVES 5 5</b>
 */
public class PrticlCreateCommand extends PrticlCommand {
    private Plugin plugin;
    private FileConfiguration config;
    private PrticlNodeBuilder builder;
    private static PrticlNodeConfigUtil configUtil;

    public PrticlCreateCommand(Plugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.builder = new PrticlNodeBuilder(plugin);
        this.configUtil = new PrticlNodeConfigUtil(plugin);
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender)) {
            if (nodeNameValidation(args[0], sender))
                return true;

            switch (args.length) {
                // Node name
                case 1 -> {
                    PrticlNode node = builder.setName(args[0])
                            .setCreatedBy(sender.getName()).build();

                    if (!configUtil.saveNodeToConfig(config, node))
                        sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                    sender.sendMessage(prticlMessage.player(CREATED_NODE));
                }
                // Node particle type
                case 2 -> {
                    PrticlNode node = builder.setName(args[0])
                            .setParticleType(Particle.valueOf(args[1]))
                            .setCreatedBy(sender.getName()).build();

                    if (!configUtil.saveNodeToConfig(config, node))
                        sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                    sender.sendMessage(prticlMessage.player(CREATED_NODE));
                }
                // Node repeat delay
                case 3 -> {
                    try {
                        PrticlNode node = builder.setName(args[0])
                                .setParticleType(Particle.valueOf(args[1]))
                                .setRepeatDelay(Integer.parseInt(args[2]))
                                .setCreatedBy(sender.getName()).build();

                        if (!configUtil.saveNodeToConfig(config, node))
                            sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(prticlMessage.error("Incorrect repeat delay format! (expected a valid integer number (ticks))"));
                        return true;
                    }
                    sender.sendMessage(prticlMessage.player(CREATED_NODE));
                }
                // Node particle density
                case 4 -> {
                    try {
                        PrticlNode node = builder.setName(args[0])
                                .setParticleType(Particle.valueOf(args[1]))
                                .setRepeatDelay(Integer.parseInt(args[2]))
                                .setParticleDensity(Integer.parseInt(args[3]))
                                .setCreatedBy(sender.getName()).build();

                        if (!configUtil.saveNodeToConfig(config, node))
                            sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                        if (Integer.parseInt(args[3]) > 25)
                            sender.sendMessage(prticlMessage.warning(HIGH_PARTICLE_DENSITY));
                    } catch (NumberFormatException e) {
                        sender.sendMessage(prticlMessage.error("Incorrect particle density format! (expected a valid integer number)"));
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
     * Utility method to block the player from entering a name that's: <br>
     * - "id:" <br>
     * - over 50 characters in length <br>
     * - already taken by another node <br>
     * - blank
     *
     * @param arg    The name to be checked
     * @param sender The sender that sent the command
     * @return TRUE if handled.
     */
    private static boolean nodeNameValidation(String arg, CommandSender sender) {
        BaseMessageComponents messageComponents1 = new BaseMessageComponents();
        boolean result = false;

        if (arg.equalsIgnoreCase("id:")) {
            sender.sendMessage(messageComponents1.error(NODE_NAME_UNAVAILABLE));
            result = true;
        }
        if (arg.length() > 50) {
            sender.sendMessage(messageComponents1.error(NODE_NAME_TOO_LONG));
            result = true;
        }
        if (nameExistsInConfig(arg)) {
            sender.sendMessage(messageComponents1.error(DUPLICATE_NODE_NAME));
            result = true;
        }
        if (arg.isBlank()) {
            sender.sendMessage(messageComponents1.error(EMPTY_NODE_NAME));
            result = true;
        }

        return result;
    }

    /**
     * Utility method to check for duplicate names in nodes. Ignores case-sensitivity.
     *
     * @param arg The name to check for in the list of existing nodes
     * @return TRUE if exists.
     */
    private static boolean nameExistsInConfig(String arg) {
        return configUtil.getConfigNodesList().stream().anyMatch(node -> node.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT)));
    }

    public static String getCommandName() {
        return CREATE_COMMAND;
    }
}
