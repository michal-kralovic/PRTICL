package com.minkuh.prticl.nodes.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.data.commandargs.PrticlCreateCommandArguments;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.nodes.prticl.PrticlLocationObjectBuilder;
import com.minkuh.prticl.nodes.prticl.PrticlNodeBuilder;
import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;
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
    private PrticlNodeBuilder nodeBuilder;
    private static PrticlNodeConfigUtil configUtil;
    BaseMessageComponents prticlMessage = new BaseMessageComponents();
    private PrticlDatabase prticlDatabase;

    public PrticlCreateCommand(Prticl plugin) throws SQLException {
        this.plugin = plugin;
        this.nodeBuilder = new PrticlNodeBuilder(plugin);
        this.configUtil = new PrticlNodeConfigUtil(plugin);
        this.prticlDatabase = new PrticlDatabase(plugin);
    }

    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (isCommandSentByPlayer(sender)) {
            if (isNodeNameCompatible(args[0], sender))
                return true;

            PrticlCreateCommandArguments cmdArgsObject = turnIntoCommandArgumentsObject(args);
            try {
                if (cmdArgsObject.getName() != null)
                    nodeBuilder.setName(cmdArgsObject.getName());

                if (cmdArgsObject.getParticleType() != null)
                    nodeBuilder.setParticleType(cmdArgsObject.getParticleType());

                if (cmdArgsObject.getRepeatDelay() != null)
                    nodeBuilder.setRepeatDelay(cmdArgsObject.getRepeatDelay());

                if (cmdArgsObject.getParticleDensity() != null) {
                    if (cmdArgsObject.getParticleDensity() > 25)
                        sender.sendMessage(prticlMessage.warning(HIGH_PARTICLE_DENSITY));

                    nodeBuilder.setParticleDensity(cmdArgsObject.getParticleDensity());
                }

                Location playerLocation = ((Player) sender).getLocation();
                if (!prticlDatabase.getLocationFunctions().isLocationInDatabase(playerLocation))
                    prticlDatabase.getLocationFunctions().createLocation(playerLocation);

                int dbLocationId = prticlDatabase.getLocationFunctions().getLocationId(playerLocation);

                nodeBuilder.setLocationObject(new PrticlLocationObjectBuilder().withLocation(playerLocation).withId(dbLocationId).build());
                nodeBuilder.setCreatedBy(sender.getName());

                if (!prticlDatabase.getNodeFunctions().addNodeToDatabase((Player) sender, nodeBuilder.build())) {
                    sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_CONFIG));
                    return true;
                }

                sender.sendMessage(prticlMessage.player(CREATED_NODE));
            } catch (NumberFormatException e) {
                sender.sendMessage(prticlMessage.error(INCORRECT_NUMBER_INPUT_FORMAT));
            } catch (Exception e) {
                sender.sendMessage(prticlMessage.error("Unexpected error!\nError: " + e.getMessage()));
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
    private boolean isNodeNameCompatible(String arg, CommandSender sender) {
        if (arg.toLowerCase(Locale.ROOT).startsWith("id:".toLowerCase(Locale.ROOT))) {
            sender.sendMessage(prticlMessage.error(NODE_NAME_UNAVAILABLE));
            return true;
        }
        if (arg.length() > 50) {
            sender.sendMessage(prticlMessage.error(NODE_NAME_TOO_LONG));
            return true;
        }
        try {
            if (nameExistsInConfig(arg)) {
                sender.sendMessage(prticlMessage.error(DUPLICATE_NODE_NAME));
                return true;
            }
        } catch (SQLException e) {
            sender.sendMessage(prticlMessage.error("An SQL error occurred!"));
            return true;
        }
        if (arg.isBlank()) {
            sender.sendMessage(prticlMessage.error(EMPTY_NODE_NAME));
            return true;
        }
        return false;
    }

    /**
     * Utility method to check for duplicate names in nodes. Ignores case-sensitivity.
     *
     * @param nodeName The name to check for in the list of existing nodes
     * @return TRUE if exists.
     */
    private boolean nameExistsInConfig(String nodeName) throws SQLException {
        return prticlDatabase.getNodeFunctions().getNodesList()
                .stream()
                .anyMatch(node -> node.toLowerCase(Locale.ROOT).equals(nodeName.toLowerCase(Locale.ROOT)));
    }

    /**
     * Utility method to create an object with variables for easier command arguments manipulation.
     *
     * @param args The arguments of the executed command
     * @return A new PrticlCreateCommandArguments object with the arguments usable via variables.
     */
    private static PrticlCreateCommandArguments turnIntoCommandArgumentsObject(String[] args) {
        return new PrticlCreateCommandArguments(args);
    }

    public static String getCommandName() {
        return CREATE_COMMAND;
    }
}
