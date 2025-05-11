package com.minkuh.prticl.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.base.Command;
import com.minkuh.prticl.common.NodeSpawnManager;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.repositories.NodeRepository;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class DespawnNodeCommand extends Command {
    private final NodeRepository nodeRepository;
    private final NodeSpawnManager spawnManager;

    public DespawnNodeCommand(Prticl prticl) {
        this.nodeRepository = new NodeRepository(prticl.getLogger());
        this.spawnManager = new NodeSpawnManager(prticl);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (!isCommandSentByPlayer(sender) || args.length != 1)
            return true;

        if (NodeSpawnManager.SpawnedNodes.getTaskCount() == 0) {
            sender.sendMessage(PrticlMessages.player("There are no spawned nodes."));
            return true;
        }

        Optional<Node> nodeOpt;
        if (args[0].toLowerCase(Locale.ROOT).startsWith("id:")) {
            nodeOpt = nodeRepository.getById(Integer.parseInt(args[0].substring("id:".length())));
        } else {
            nodeOpt = nodeRepository.getByName(args[0]);
        }

        if (nodeOpt.isEmpty()) {
            sender.sendMessage(PrticlMessages.error("Node '" + args[0] + "' does not exist!"));
            return true;
        }

        var node = nodeOpt.get();

        if (!node.isSpawned()) {
            sender.sendMessage(PrticlMessages.error("Node '" + args[0] + "' is not spawned!"));
            return true;
        }

        spawnManager.despawnNode(node, true);

        sender.sendMessage(PrticlMessages.player("Despawned '" + node.getName() + '\''));
        return true;
    }

    @Override
    public String getCommandName() {
        return PrticlCommands.Names.DESPAWN;
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            List<String> spawnedNodeNames = NodeSpawnManager.SpawnedNodes.getAllNodes().values().stream().map(Node::getName).toList();
            var sortedNodeNames = new ArrayList<String>();

            StringUtil.copyPartialMatches(args[1], spawnedNodeNames, sortedNodeNames);

            return sortedNodeNames;
        }

        return List.of();
    }

    @Override
    public TextComponent.Builder getHelpSection() {
        return createHelpSectionForCommand(
                getCommandName(),
                "Despawns a spawned prticl node.",
                "/prticl node despawn <(id:X) or (node name)>",
                "/prticl node despawn foo"
        );
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.NODE;
    }
}