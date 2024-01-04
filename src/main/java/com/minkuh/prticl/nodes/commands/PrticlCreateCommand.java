package com.minkuh.prticl.nodes.commands;

import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.prticl.PrticlNodeBuilder;
import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.apache.commons.lang3.EnumUtils;
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
    BaseMessageComponents prticlMessage = new BaseMessageComponents();

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

            try {
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
                                .setParticleType(Particle.valueOf(supportedParticleTypeString(args[1])))
                                .setCreatedBy(sender.getName()).build();

                        if (!configUtil.saveNodeToConfig(config, node))
                            sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                        sender.sendMessage(prticlMessage.player(CREATED_NODE));
                    }
                    // Node repeat delay
                    case 3 -> {
                        PrticlNode node = builder.setName(args[0])
                                .setParticleType(Particle.valueOf(supportedParticleTypeString(args[1])))
                                .setRepeatDelay(Integer.parseInt(args[2]))
                                .setCreatedBy(sender.getName()).build();

                        if (!configUtil.saveNodeToConfig(config, node))
                            sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                        sender.sendMessage(prticlMessage.player(CREATED_NODE));
                    }
                    // Node particle density
                    case 4 -> {
                        PrticlNode node = builder.setName(args[0])
                                .setParticleType(Particle.valueOf(supportedParticleTypeString(args[1])))
                                .setRepeatDelay(Integer.parseInt(args[2]))
                                .setParticleDensity(Integer.parseInt(args[3]))
                                .setCreatedBy(sender.getName()).build();

                        if (!configUtil.saveNodeToConfig(config, node))
                            sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                        if (Integer.parseInt(args[3]) > 25)
                            sender.sendMessage(prticlMessage.warning(HIGH_PARTICLE_DENSITY));
                        sender.sendMessage(prticlMessage.player(CREATED_NODE));
                    }
                    default -> sender.sendMessage(prticlMessage.error("Unexpected error!"));
                }
            } catch (NumberFormatException e) {
                sender.sendMessage(prticlMessage.error(INCORRECT_NUMBER_INPUT_FORMAT));
            } catch (Exception e) {
                sender.sendMessage(prticlMessage.error(e.getMessage()));
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
    private boolean nodeNameValidation(String arg, CommandSender sender) {
        boolean result = false;

        if (arg.equalsIgnoreCase("id:")) {
            sender.sendMessage(prticlMessage.error(NODE_NAME_UNAVAILABLE));
            result = true;
        }
        if (arg.length() > 50) {
            sender.sendMessage(prticlMessage.error(NODE_NAME_TOO_LONG));
            result = true;
        }
        if (nameExistsInConfig(arg)) {
            sender.sendMessage(prticlMessage.error(DUPLICATE_NODE_NAME));
            result = true;
        }
        if (arg.isBlank()) {
            sender.sendMessage(prticlMessage.error(EMPTY_NODE_NAME));
            result = true;
        }

        return result;
    }

    /**
     * A utility method to convert the input particle argument into one the code can work with.<br><br>
     * E.g.:<br>
     * - input: "minecraft:cloud", "cLoUd"<br>
     * - output (of this method): "CLOUD", "CLOUD"
     *
     * @param arg The input particle from the Player
     * @return The Particle as a support String.
     */
    private String supportedParticleTypeString(String arg) throws IllegalArgumentException {
        String[] particleWithNamespace;
        if (arg.contains(":")) {
            particleWithNamespace = arg.split(":");
            arg = particleWithNamespace[1];
        }

        if(!EnumUtils.isValidEnum(Particle.class, arg))
            throw new IllegalArgumentException("The " + Particle.class.getName() + " enum doesn't contain the input particle \"" + arg + "\"");

        return arg.toUpperCase(Locale.ROOT);
    }

    /**
     * Utility method to check for duplicate names in nodes. Ignores case-sensitivity.
     *
     * @param arg The name to check for in the list of existing nodes
     * @return TRUE if exists.
     */
    private boolean nameExistsInConfig(String arg) {
        return configUtil.getConfigNodesList()
                .stream()
                .anyMatch(
                        node -> node.getName().toLowerCase(Locale.ROOT).equals(arg.toLowerCase(Locale.ROOT))
                );
    }

    public static String getCommandName() {
        return CREATE_COMMAND;
    }
}
