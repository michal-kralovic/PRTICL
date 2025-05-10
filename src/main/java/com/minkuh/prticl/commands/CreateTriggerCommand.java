package com.minkuh.prticl.commands;

import com.minkuh.prticl.commands.base.Command;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.data.entities.Trigger;
import com.minkuh.prticl.data.repositories.TriggerRepository;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.block.data.Powerable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class CreateTriggerCommand extends Command {
    private final TriggerRepository triggerRepository;

    public CreateTriggerCommand(Logger logger) {
        this.triggerRepository = new TriggerRepository(logger);
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return switch (args.length) {
            case 2 -> List.of(PrticlCommands.Names.NODE);
            default -> List.of();
        };
    }

    @Override
    public TextComponent.Builder getHelpSection() {
        return createHelpSectionForCommand(
                getCommandName(),
                "Binds a trigger to the interactable block (e.g. lever, button, pressure plate, etc.) you're looking at.",
                "/prticl trigger create <name>",
                "/prticl trigger create my_trigger"
        );
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (!isCommandSentByPlayer(sender))
            return true;

        if (args.length != 1) {
            sender.sendMessage(PrticlMessages.error("Incorrect parameter amount! Only need a name."));
            return true;
        }

        var nameValidationError = isTriggerNameValid(args[0]);
        if (!nameValidationError.isEmpty()) {
            sender.sendMessage(PrticlMessages.error(nameValidationError));
            return true;
        }

        var targetedBlock = ((Player) sender).getTargetBlockExact(5);
        if (targetedBlock == null) {
            sender.sendMessage(PrticlMessages.error("Not looking at any block!"));
            return true;
        }

        boolean isBlockInteractable = targetedBlock.getBlockData() instanceof Powerable;
        if (!isBlockInteractable) {
            sender.sendMessage(PrticlMessages.error("Can't bind trigger to block: " + targetedBlock.getType().name() + ". Block is not interactable!"));
            return true;
        }

        var trigger = new Trigger();
        trigger.setName(args[0]);
        trigger.setBlockName(targetedBlock.getType().name());
        trigger.setX(targetedBlock.getX());
        trigger.setY(targetedBlock.getY());
        trigger.setZ(targetedBlock.getZ());
        trigger.setWorldUUID(targetedBlock.getWorld().getUID());
        trigger.setPlayer(com.minkuh.prticl.data.entities.Player.fromBukkitPlayer((Player) sender));

        triggerRepository.add(trigger);

        sender.sendMessage(PrticlMessages.player("Created the trigger."));
        return true;
    }

    @Override
    public String getCommandName() {
        return PrticlCommands.Names.CREATE;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.TRIGGER;
    }

    private String isTriggerNameValid(String name) {
        var output = new StringBuilder();

        if (name.length() > 50) {
            output.append("The trigger name can't be over 50 characters in length!");
            output.append('\n');
        }

        if (name.isBlank()) {
            output.append("The trigger name can't be blank!");
            output.append('\n');
        }

        if (name.toLowerCase(Locale.ROOT).startsWith("id:")) {
            output.append("The node name can't start with 'id:'!");
            output.append('\n');
        }

        if (!triggerRepository.isTriggerNameUnique(name)) {
            output.append("A node with this name already exists!");
            output.append('\n');
        }

        return output.toString();
    }
}