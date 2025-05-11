package com.minkuh.prticl.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.base.Command;
import com.minkuh.prticl.commands.base.CommandCommons;
import com.minkuh.prticl.common.NodeSpawnManager;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.common.PrticlUtil;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.repositories.NodeRepository;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.Validate;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditNodePropertyCommand extends Command {
    private final NodeRepository nodeRepository;
    private final NodeSpawnManager spawnManager;

    public EditNodePropertyCommand(Prticl prticl) {
        this.nodeRepository = new NodeRepository(prticl.getLogger());
        this.spawnManager = new NodeSpawnManager(prticl);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        try {
            var nodeOpt = args[0].toLowerCase(Locale.ROOT).startsWith("id:")
                    ? nodeRepository.getById(Integer.parseInt(args[0].substring("id:".length())))
                    : nodeRepository.getByName(args[0]);

            Validate.isTrue(nodeOpt.isPresent(), "Node '" + args[0] + "' does not exist");

            var node = nodeOpt.get();

            var nodePropertyEnum = Enum.valueOf(NodeProperties.class, args[1].toUpperCase(Locale.ROOT));

            var newValue = args[2];

            Node spawnedNode = null;
            if (NodeSpawnManager.SpawnedNodes.hasTask(node.getId())) {
                spawnedNode = NodeSpawnManager.SpawnedNodes.getNode(node.getId());
            }

            switch (nodePropertyEnum) {
                case NAME -> {
                    CommandCommons.isNodeNameValid(args[2], nodeRepository);
                    node.setName(newValue);

                    if (spawnedNode != null)
                        spawnedNode.setName(newValue);
                }
                case REPEAT_DELAY -> {
                    var parsed = Integer.parseInt(newValue);
                    node.setRepeatDelay(parsed);

                    spawnManager.despawnNode(node, false);
                    spawnManager.spawnNode(node);
                }
                case REPEAT_COUNT -> {
                    var parsed = Integer.parseInt(newValue);
                    node.setRepeatCount(parsed);

                    if (spawnedNode != null)
                        spawnedNode.setRepeatCount(parsed);
                }
                case PARTICLE_DENSITY -> {
                    var parsed = Integer.parseInt(newValue);
                    node.setParticleDensity(parsed);

                    if (spawnedNode != null)
                        spawnedNode.setParticleDensity(parsed);
                }
                case PARTICLE_TYPE -> {
                    var particleType = Particle.valueOf(PrticlUtil.getSupportedParticle(newValue)).toString();
                    node.setParticleType(particleType);

                    if (spawnedNode != null)
                        spawnedNode.setParticleType(particleType);
                }
                case X -> {
                    var coord = CommandCommons.getCoordinate(newValue, node.getX());
                    node.setX(coord);

                    if (spawnedNode != null)
                        spawnedNode.setX(coord);
                }
                case Y -> {
                    var coord = CommandCommons.getCoordinate(newValue, node.getY());
                    node.setY(coord);

                    if (spawnedNode != null)
                        spawnedNode.setY(coord);
                }
                case Z -> {
                    var coord = CommandCommons.getCoordinate(newValue, node.getZ());
                    node.setZ(coord);

                    if (spawnedNode != null)
                        spawnedNode.setZ(coord);
                }
            }

            nodeRepository.updateNode(node);

            sender.sendMessage(PrticlMessages.player("Node successfully updated."));

            return true;
        } catch (Exception ex) {
            sender.sendMessage(PrticlMessages.error(ex.getMessage()));
            return true;
        }
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return switch (args.length) {
            case 2 -> List.of("id/name");
            case 3 -> {
                List<String> completions = new ArrayList<>();

                var editableNodeProperties = PrticlUtil.getNodeProperties(true).stream().map(Enum::toString).map(String::toLowerCase).toList();
                StringUtil.copyPartialMatches(
                        args[2],
                        editableNodeProperties,
                        completions
                );

                yield completions;
            }
            case 4 -> {
                if (args[2].equalsIgnoreCase(NodeProperties.PARTICLE_TYPE.name()))
                    yield PrticlUtil.sortedParticles(args[3]);
                else
                    yield List.of("new value");
            }
            default -> List.of();
        };
    }

    @Override
    public TextComponent.Builder getHelpSection() {
        return createHelpSectionForCommand(
                getCommandName(),
                "Edits a property of a node",
                "/prticl n edit <(id:X) or (node name)> <property> <new value>",
                "/prticl n edit id:1 name MyNewNodeName"
        );
    }

    @Override
    public String getCommandName() {
        return PrticlCommands.Names.EDIT;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.NODE;
    }
}