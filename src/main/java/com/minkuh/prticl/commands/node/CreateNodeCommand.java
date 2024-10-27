package com.minkuh.prticl.commands.node;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.PrticlCommand;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.data.caches.NodeChunkLocationsCache;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entity_util.EntityValidation;
import com.minkuh.prticl.data.database.entity_util.NodeBuilder;
import com.minkuh.prticl.data.database.entity_util.PlayerBuilder;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

import static com.minkuh.prticl.common.PrticlConstants.*;

public class CreateNodeCommand extends PrticlCommand {
    private final PrticlMessages prticlMessage = new PrticlMessages();
    private final PrticlDatabase prticlDatabase;

    public CreateNodeCommand(Prticl plugin) {
        this.prticlDatabase = new PrticlDatabase(plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        //#region Validation
        if (args.length < 1)
            sender.sendMessage(prticlMessage.error("Invalid argument amount!"));

        if (!isCommandSentByPlayer(sender)) {
            sender.sendMessage(prticlMessage.error(PLAYER_COMMAND_ONLY));
            return true;
        }

        if (!EntityValidation.isNodeNameValid(prticlDatabase, args[0], sender))
            return true;
        //#endregion

        try {
            var bukkitPlayer = (org.bukkit.entity.Player) sender;
            var worldUUID = bukkitPlayer.getLocation().getWorld().getUID();

            if (args.length == 1) {
                spawnDefaultNode(args, sender, bukkitPlayer, worldUUID);
                return true;
            }

            var nodeBuilder = new NodeBuilder();

            nodeBuilder.setName(args[0]);
            nodeBuilder.setParticleType((Particle.valueOf(getSupportedParticle(args[1]))).toString());
            if (args.length >= 3) {
                nodeBuilder.setRepeatDelay(Integer.parseInt(args[2]));
            }

            if (args.length >= 4) {
                nodeBuilder.setRepeatCount(Integer.parseInt(args[3]));
            }

            if (args.length >= 5) {
                var particleDensity = Integer.parseInt(args[4]);

                nodeBuilder.setParticleDensity(particleDensity);

                if (particleDensity > 25) {
                    sender.sendMessage(prticlMessage.warning(HIGH_PARTICLE_DENSITY));
                }
            }

            if (args.length >= 8) {
                nodeBuilder.setLocation(
                        worldUUID,
                        Integer.parseInt(args[5]), // x
                        Integer.parseInt(args[6]), // y
                        Integer.parseInt(args[7])  // z
                );
            } else {
                nodeBuilder.setLocation(worldUUID, bukkitPlayer.getLocation());
            }

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

    private void spawnDefaultNode(String[] args, CommandSender sender, Player bukkitPlayer, UUID worldUUID) {
        var node = new Node();
        node.setDefaults();
        node.setName(args[0]);

        node.setPlayer(PlayerBuilder.fromBukkitPlayer(bukkitPlayer));
        node.setWorldUUID(worldUUID);
        node.setX(bukkitPlayer.getX());
        node.setY(bukkitPlayer.getY());
        node.setZ(bukkitPlayer.getZ());

        try {
            prticlDatabase.getNodeFunctions().add(node);
            NodeChunkLocationsCache.getInstance().add(node);
        } catch (Exception ex) {
            sender.sendMessage(prticlMessage.error("Unexpected error!\nError: " + ex.getMessage()));
            return;
        }

        sender.sendMessage(prticlMessage.player(CREATED_NODE));
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

    public static String getCommandName() {
        return CREATE_COMMAND;
    }

    /**
     * A utility method to strip the input particle argument of its namespace if necessary.<br><br>
     * E.g.:<br>
     * - input: "minecraft:cloud", "cLoUd"<br>
     * - output (of this method): "CLOUD", "CLOUD"
     *
     * @param arg The input particle from the Player
     * @return The Particle as a support String.
     */
    private String getSupportedParticle(String arg) throws IllegalArgumentException {
        arg = arg.contains(":") ? arg.split(":")[1].toUpperCase(Locale.ROOT) : arg.toUpperCase(Locale.ROOT);

        if (!EnumUtils.isValidEnum(Particle.class, arg))
            throw new IllegalArgumentException("The " + Particle.class.getName() + " enum doesn't contain the input particle \"" + arg + "\"");

        return arg.toUpperCase(Locale.ROOT);
    }
}