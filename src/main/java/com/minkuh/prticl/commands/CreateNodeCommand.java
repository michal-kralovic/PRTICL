package com.minkuh.prticl.commands;

import com.minkuh.prticl.commands.base.Command;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.repositories.NodeRepository;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;
import java.util.logging.Logger;

import static com.minkuh.prticl.data.entities.Player.fromBukkitPlayer;

public class CreateNodeCommand extends Command {
    private final List<String> PARTICLE_NAMES;
    private final NodeRepository nodeRepository;

    public CreateNodeCommand(Logger logger) {
        this.PARTICLE_NAMES = Arrays.stream(Particle.values())
                .map(p -> "minecraft:" + p.name().toLowerCase())
                .toList();

        nodeRepository = new NodeRepository(logger);
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return switch (args.length) {
            case 2 -> List.of("name");
            case 3 -> sortedParticles(args[2]);
            case 4 -> List.of("repeat-delay");
            case 5 -> List.of("repeat-count");
            case 6 -> List.of("particle-density");
            case 7 -> List.of("x y z");
            case 8 -> List.of("y z");
            case 9 -> List.of("z");
            default -> List.of();
        };
    }

    @Override
    public TextComponent.Builder getHelpSection() {
        return createHelpSectionForCommand(
                getCommandName(),
                "Creates a new prticl node.",
                "/prticl node create <name> <particle type> <repeat delay> <density> <(x) (y) (z)>",
                "/prticl node create my_node minecraft:cloud 5 5 ~ ~ ~"
        );
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (args.length < 1)
            sender.sendMessage(PrticlMessages.error("Invalid amount of arguments!"));

        if (!isCommandSentByPlayer(sender)) {
            return true;
        }

        try {
            var player = (org.bukkit.entity.Player) sender;
            var worldUUID = player.getLocation().getWorld().getUID();

            var node = buildNode(args, sender, worldUUID, player, args.length == 1);

            var nameValidationError = isNodeNameValid(node.getName());
            if (!nameValidationError.isEmpty()) {
                sender.sendMessage(PrticlMessages.error(nameValidationError));
                return true;
            }

            nodeRepository.add(node);

            sender.sendMessage(PrticlMessages.player("Created the node."));
        } catch (NumberFormatException ex) {
            sender.sendMessage(PrticlMessages.error("Incorrect number input format!"));
        } catch (Exception ex) {
            sender.sendMessage(PrticlMessages.error("Unexpected error!\nError: " + ex.getMessage()));
        }

        return true;
    }

    @Override
    public String getCommandName() {
        return PrticlCommands.Names.CREATE;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.NODE;
    }

    private Node buildNode(String[] args, CommandSender sender, UUID worldUUID, Player player, boolean isDefault) {
        var node = new Node();

        node.setName(args[0]);

        if (isDefault) {
            node.setRepeatCount(0);
            node.setParticleType("HEART");
            node.setParticleDensity(10);
            node.setPlayer(fromBukkitPlayer(player));
            node.setWorldUUID(worldUUID);
            node.setX(player.getX());
            node.setY(player.getY());
            node.setZ(player.getZ());
        } else {
            node.setParticleType((Particle.valueOf(getSupportedParticle(args[1]))).toString());
            if (args.length >= 3) {
                node.setRepeatCount(Integer.parseInt(args[2]));
            }
            if (args.length >= 4) {
                node.setRepeatCount(Integer.parseInt(args[3]));
            }
            if (args.length >= 5) {
                var particleDensity = Integer.parseInt(args[4]);
                node.setParticleDensity(particleDensity);
                if (particleDensity > 25) {
                    sender.sendMessage(PrticlMessages.warning("Particle density >25 can lag the server if overused. Proceed with care!"));
                }
            }
            if (args.length >= 8) {
                node.setWorldUUID(worldUUID);
                if (args.length >= 9) {
                    node.setX(Integer.parseInt(args[5]));
                    node.setY(Integer.parseInt(args[6]));
                    node.setZ(Integer.parseInt(args[7]));
                } else {
                    var loc = player.getLocation();
                    node.setX(loc.x());
                    node.setY(loc.y());
                    node.setZ(loc.z());
                }
            }

            node.setPlayer(fromBukkitPlayer(player));
        }

        return node;
    }

    private String isNodeNameValid(String name) {
        var output = "";

        Validate.notNull(name);

        if (name.length() > 50)
            output = "The node name can't be over 50 characters in length!";

        if (name.isBlank())
            output = "The node name can't be blank!";

        if (name.toLowerCase(Locale.ROOT).startsWith("id:"))
            output = "The node name can't start with 'id:'!";

        if (!nodeRepository.isNodeNameUnique(name))
            output = "A node with this name already exists!";

        return output;
    }

    private List<String> sortedParticles(String arg) {
        final List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(arg, PARTICLE_NAMES, completions);
        Collections.sort(completions);

        return completions;
    }

    private String getSupportedParticle(String arg) throws IllegalArgumentException {
        arg = arg.contains(":") ? arg.split(":")[1].toUpperCase(Locale.ROOT) : arg.toUpperCase(Locale.ROOT);

        if (!EnumUtils.isValidEnum(Particle.class, arg))
            throw new IllegalArgumentException("The " + Particle.class.getName() + " enum doesn't contain the input particle \"" + arg + "\"");

        return arg.toUpperCase(Locale.ROOT);
    }
}