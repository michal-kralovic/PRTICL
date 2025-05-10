package com.minkuh.prticl.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.base.Command;
import com.minkuh.prticl.common.NodeSpawnManager;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.repositories.NodeRepository;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class SpawnNodeCommand extends Command {
    private final NodeRepository nodeRepository;
    private final NodeSpawnManager nodeSpawnManager;

    public SpawnNodeCommand(Prticl prticl) {
        nodeRepository = new NodeRepository(prticl.getLogger());
        nodeSpawnManager = new NodeSpawnManager(prticl);
    }

    @Override
    public String getCommandName() {
        return PrticlCommands.Names.SPAWN;
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2)
            return List.of("id/name");
        return List.of();
    }

    @Override
    public TextComponent.Builder getHelpSection() {
        return createHelpSectionForCommand(
                getCommandName(),
                "Spawns a prticl node based on a given id/name.",
                "/prticl node spawn <(id:X) or (node name)>",
                "/prticl node spawn id:1"
        );
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (!isCommandSentByPlayer(sender) || args.length != 1)
            return true;

        Optional<Node> nodeOpt;
        try {
            if (args[0].toLowerCase(Locale.ROOT).startsWith("id:")) {
                nodeOpt = nodeRepository.getById(Integer.parseInt(args[0].substring("id:".length())));
            } else {
                nodeOpt = nodeRepository.getByName(args[0]);
            }

            var node = nodeOpt.orElseThrow();

            var isAddSuccessful = nodeSpawnManager.spawnNode(node);
            if (isAddSuccessful)
                sender.sendMessage(PrticlMessages.player("Spawned '" + node.getName() + '\''));
        } catch (Exception ex) {
            sender.sendMessage(PrticlMessages.error(ex.getMessage()));
        }
        return true;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.NODE;
    }
}