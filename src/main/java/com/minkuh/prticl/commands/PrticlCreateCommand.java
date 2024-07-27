package com.minkuh.prticl.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.PrticlLocationObjectBuilder;
import com.minkuh.prticl.common.PrticlNode;
import com.minkuh.prticl.common.PrticlNodeBuilder;
import com.minkuh.prticl.common.message.PrticlMessages;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.common.wrappers.command_args.PrticlCreateCommandArguments;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.SQLException;
import java.util.*;

import static com.minkuh.prticl.common.resources.PrticlConstants.*;

/**
 * Provides a configurable modular way of creating a PRTICL node before spawning it.<br><br>
 * In-game usage: <b>/prticl node create (name) (particle) (repeat delay in ticks) (particle density) (x) (y) (z)</b><br><i>
 * - where nothing except for the name is necessary to specify.</i><br><br>
 * Example: <b>/prticl node create leaf_blower CHERRY_LEAVES 5 5</b>
 */
public class PrticlCreateCommand extends PrticlCommand {
    private final PrticlNodeBuilder nodeBuilder;
    PrticlMessages prticlMessage = new PrticlMessages();
    private final PrticlDatabase prticlDatabase;

    public PrticlCreateCommand(Prticl plugin) throws SQLException {
        this.nodeBuilder = new PrticlNodeBuilder();
        this.prticlDatabase = new PrticlDatabase(plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (!isCommandSentByPlayer(sender)) {
            sender.sendMessage(prticlMessage.error(PLAYER_COMMAND_ONLY));
            return true;
        }

        if (!isNodeNameCompatible(args[0], sender))
            return true;

        try {
            PrticlCreateCommandArguments cmdArgsObject = cmdArgsObjectify(args);
            if (cmdArgsObject.getName() != null) {
                nodeBuilder.setName(cmdArgsObject.getName());
            }

            if (cmdArgsObject.getParticleType() != null) {
                nodeBuilder.setParticleType(cmdArgsObject.getParticleType());
            }

            if (cmdArgsObject.getRepeatDelay() != null) {
                nodeBuilder.setRepeatDelay(cmdArgsObject.getRepeatDelay());
            }

            if (cmdArgsObject.getParticleDensity() != null) {
                if (cmdArgsObject.getParticleDensity() > 25)
                    sender.sendMessage(prticlMessage.warning(HIGH_PARTICLE_DENSITY));

                nodeBuilder.setParticleDensity(cmdArgsObject.getParticleDensity());
            }


            Location playerLocation = ((Player) sender).getLocation();
            int dbLocationId = prticlDatabase.getLocationFunctions().createLocation(playerLocation);
            nodeBuilder.setLocationObject(new PrticlLocationObjectBuilder().withLocation(playerLocation).withId(dbLocationId).build());
            nodeBuilder.setCreatedBy(sender.getName());


            PrticlNode node = nodeBuilder.build();
            if (!prticlDatabase.getNodeFunctions().addNodeToDatabase((Player) sender, node)) {
                sender.sendMessage(prticlMessage.error(FAILED_SAVE_TO_DATABASE));
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

    private static final List<String> marker = new ArrayList<>();
    @Override
    public List<String> getTabCompletion(String[] args) {
        marker.clear();

        return switch (args.length) {
            case 2 -> {
                marker.add(NODE_PARAM_NAME);
                yield marker;
            }
            case 3 -> sortedParticles(args[2]);
            case 4 -> {
                marker.add(NODE_PARAM_REPEAT_DELAY);
                yield marker;
            }
            case 5 -> {
                marker.add(NODE_PARAM_PARTICLE_DENSITY);
                yield marker;
            }
            case 6 -> {
                marker.add("x y z");
                yield marker;
            }
            case 7 -> {
                marker.add("y z");
                yield marker;
            }
            case 8 -> {
                marker.add("z");
                yield marker;
            }
            default -> List.of();
        };
    }

    private static final List<String> PARTICLE_NAMES = Arrays.stream(Particle.values())
            .map(p -> "minecraft:" + p.name().toLowerCase())
            .toList();

    private static List<String> sortedParticles(String arg) {
        final List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(arg, PARTICLE_NAMES, completions);
        Collections.sort(completions);

        return completions;
    }

    /**
     * Utility method to create an object with variables for easier command arguments manipulation.
     *
     * @param args The arguments of the executed command
     * @return A new PrticlCreateCommandArguments object with the arguments usable via variables.
     */
    private static PrticlCreateCommandArguments cmdArgsObjectify(String[] args) {
        return new PrticlCreateCommandArguments(args);
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
     * @return TRUE if compatbiel.
     */
    private boolean isNodeNameCompatible(String arg, CommandSender sender) {
        PrticlMessages messages = new PrticlMessages();

        if (arg.toLowerCase(Locale.ROOT).startsWith("id:".toLowerCase(Locale.ROOT))) {
            sender.sendMessage(messages.error(NODE_NAME_UNAVAILABLE));
            return false;
        }

        if (arg.length() > 50) {
            sender.sendMessage(messages.error(NODE_NAME_TOO_LONG));
            return false;
        }

        try {
            if (nameExistsInDatabase(arg)) {
                sender.sendMessage(messages.error(DUPLICATE_NODE_NAME));
                return false;
            }
        } catch (SQLException e) {
            sender.sendMessage(messages.error("An SQL error occurred!"));
            return false;
        }

        if (arg.isBlank()) {
            sender.sendMessage(messages.error(EMPTY_NODE_NAME));
            return false;
        }

        return true;
    }

    /**
     * Utility method to check for duplicate names in nodes. Ignores case-sensitivity.
     *
     * @param nodeName The name to check for in the list of existing nodes
     * @return TRUE if exists.
     */
    private boolean nameExistsInDatabase(String nodeName) throws SQLException {
        return prticlDatabase.getNodeFunctions().getNodeNames()
                .stream()
                .anyMatch(node -> node.toLowerCase(Locale.ROOT).equals(nodeName.toLowerCase(Locale.ROOT)));
    }

    public static String getCommandName() {
        return CREATE_COMMAND;
    }
}