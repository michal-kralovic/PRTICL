package com.minkuh.prticl.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.base.Command;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.data.repositories.NodeRepository;
import com.minkuh.prticl.data.repositories.TriggerRepository;
import net.kyori.adventure.text.TextComponent;
import org.apache.commons.lang3.Validate;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Locale;

public class UnlinkNodeToTriggerCommand extends Command {
    private final TriggerRepository triggerRepository;
    private final NodeRepository nodeRepository;

    public UnlinkNodeToTriggerCommand(Prticl prticl) {
        this.triggerRepository = new TriggerRepository(prticl.getLogger());
        this.nodeRepository = new NodeRepository(prticl.getLogger());
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        try {
            var triggerOpt = args[0].toLowerCase(Locale.ROOT).startsWith("id:")
                    ? triggerRepository.getById(Integer.parseInt(args[0].substring("id:".length())), false)
                    : triggerRepository.getByName(args[0], false);

            var nodeOpt = args[2].toLowerCase(Locale.ROOT).startsWith("id:")
                    ? nodeRepository.getById(Integer.parseInt(args[2].substring("id:".length())))
                    : nodeRepository.getByName(args[2]);

            Validate.isTrue(triggerOpt.isPresent(), "This trigger does not exist!");
            Validate.isTrue(nodeOpt.isPresent(), "This node does not exist!");

            var linkExists = triggerRepository.doesLinkAlreadyExist(nodeOpt.get().getId(), triggerOpt.get().getId());
            Validate.isTrue(linkExists.isPresent(), "Failed to check whether this link already exists!");
            Validate.isTrue(linkExists.get(), "A link between these two doesn't exist!");

            triggerRepository.removeNodeTriggerLink(nodeOpt.get().getId(), triggerOpt.get().getId());

            sender.sendMessage(PrticlMessages.player("Successfully unlinked the node and the trigger!"));

            return true;
        } catch (Exception ex) {
            sender.sendMessage(PrticlMessages.error(ex.getMessage()));
            return true;
        }
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return switch (args.length) {
            case 2, 4 -> List.of("id/name");
            case 3 -> List.of(PrticlCommands.Names.NODE);
            default -> List.of();
        };
    }

    @Override
    public TextComponent.Builder getHelpSection() {
        return createHelpSectionForCommand(
                getCommandName(),
                "Removes a specific node-trigger link.",
                "/prticl trigger unlink <(id:X) or (trigger name)> node <(id:X) or (node name)>",
                ""
        );
    }

    @Override
    public String getCommandName() {
        return PrticlCommands.Names.UNLINK;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.TRIGGER;
    }
}