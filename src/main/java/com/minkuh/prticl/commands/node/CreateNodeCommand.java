package com.minkuh.prticl.commands.node;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.PrticlCommand;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.common.wrappers.command_args.PrticlCreateCommandArguments;
import com.minkuh.prticl.data.caches.NodeChunkLocationsCache;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entity_util.EntityValidation;
import com.minkuh.prticl.data.database.entity_util.NodeBuilder;
import com.minkuh.prticl.data.database.entity_util.PlayerBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.minkuh.prticl.common.PrticlConstants.*;

/**
 * Provides a configurable modular way of creating a PRTICL node before spawning it.<br><br>
 * In-game usage: <b>/prticl node create (name) (particle) (repeat delay in ticks) (particle density) (x) (y) (z)</b><br><i>
 * - where nothing except for the name is necessary to specify.</i><br><br>
 * Example: <b>/prticl node create leaf_blower CHERRY_LEAVES 5 5</b>
 */
public class CreateNodeCommand extends PrticlCommand {
    private final NodeBuilder nodeBuilder;
    private final PrticlMessages prticlMessage = new PrticlMessages();
    private final PrticlDatabase prticlDatabase;

    public CreateNodeCommand(Prticl plugin) {
        this.nodeBuilder = new NodeBuilder();
        this.prticlDatabase = new PrticlDatabase(plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (!isCommandSentByPlayer(sender)) {
            sender.sendMessage(prticlMessage.error(PLAYER_COMMAND_ONLY));
            return true;
        }

        if (!EntityValidation.isNodeNameValid(prticlDatabase, args[0], sender)) return true;

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

            if (cmdArgsObject.getRepeatCount() != null) {
                nodeBuilder.setRepeatCount(cmdArgsObject.getRepeatCount());
            }

            if (cmdArgsObject.getParticleDensity() != null) {
                if (cmdArgsObject.getParticleDensity() > 25)
                    sender.sendMessage(prticlMessage.warning(HIGH_PARTICLE_DENSITY));

                nodeBuilder.setParticleDensity(cmdArgsObject.getParticleDensity());
            }

            var bukkitPlayer = (org.bukkit.entity.Player) sender;
            var worldUUID = bukkitPlayer.getLocation().getWorld().getUID();

            nodeBuilder.setLocation(
                    worldUUID,
                    bukkitPlayer.getLocation()
            );

            nodeBuilder.setPlayer(PlayerBuilder.fromBukkitPlayer(bukkitPlayer));
            Node node = nodeBuilder.build();


            try {
                prticlDatabase.getNodeFunctions().add(node);
                NodeChunkLocationsCache.getInstance().add(node);
            } catch (Exception ex) {
                sender.sendMessage(prticlMessage.error("Unexpected error!\nError: " + ex.getMessage()));
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
                marker.add("repeat_count");
                yield marker;
            }
            case 6 -> {
                marker.add(NODE_PARAM_PARTICLE_DENSITY);
                yield marker;
            }
            case 7 -> {
                marker.add("x y z");
                yield marker;
            }
            case 8 -> {
                marker.add("y z");
                yield marker;
            }
            case 9 -> {
                marker.add("z");
                yield marker;
            }
            default -> List.of();
        };
    }

    @Override
    public TextComponent.Builder getHelpDescription() {
        return createHelpSectionForCommand(
                CreateNodeCommand.getCommandName(),
                "Creates a new prticl node.",
                "/prticl node create <name> <particle type> <repeat delay> <density> <(x) (y) (z)>",
                "/prticl node create my_node minecraft:cloud 5 5 ~ ~ ~"
        );
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

    public static String getCommandName() {
        return CREATE_COMMAND;
    }
}