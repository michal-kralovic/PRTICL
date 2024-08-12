package com.minkuh.prticl.commands.trigger;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.PrticlCommand;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.data.database.entity_util.EntityValidation;
import com.minkuh.prticl.data.database.entity_util.PlayerBuilder;
import com.minkuh.prticl.data.database.entity_util.TriggerBuilder;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.block.data.Powerable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static com.minkuh.prticl.common.PrticlConstants.CREATE_TRIGGER_COMMAND;
import static com.minkuh.prticl.common.PrticlConstants.NODE_PARAM_NAME;

public class CreateTriggerCommand extends PrticlCommand {

    private final PrticlDatabase prticlDb;

    public CreateTriggerCommand(Prticl plugin) {
        this.prticlDb = new PrticlDatabase(plugin);
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (args.length != 1) {
            sender.sendMessage(prticlMessage.error("Incorrect parameter amount! Only need a name."));
            return true;
        }

        if (!EntityValidation.isTriggerNameValid(prticlDb, args[0], sender)) return true;

        var targetedBlock = ((Player) sender).getTargetBlockExact(5);
        if (targetedBlock == null) {
            sender.sendMessage(prticlMessage.error("Not looking at any block!"));
            return true;
        }

        boolean isBlockInteractable = targetedBlock.getBlockData() instanceof Powerable;
        if (!isBlockInteractable) {
            sender.sendMessage(prticlMessage.error("Can't bind trigger to block: " + targetedBlock.getType().name() + ". Block is not interactable!"));
            return true;
        }

        var trigger = new TriggerBuilder()
                .setPlayer(PlayerBuilder.fromBukkitPlayer((Player) sender))
                .setName(args[0])
                .setBlockName(targetedBlock.getType().name())
                .setX(targetedBlock.getX())
                .setY(targetedBlock.getY())
                .setZ(targetedBlock.getZ())
                .setWorldUUID(targetedBlock.getWorld().getUID())
                .build();

        prticlDb.getTriggerFunctions().add(trigger);

        sender.sendMessage(prticlMessage.player("Created the trigger."));
        return true;
    }

    private static final List<String> marker = new ArrayList<>();

    @Override
    public List<String> getTabCompletion(String[] args) {
        return switch (args.length) {
            case 2 -> {
                marker.add(NODE_PARAM_NAME);
                yield marker;
            }

            default -> List.of();
        };
    }

    @Override
    public TextComponent.Builder getHelpDescription() {
        return createHelpSectionForCommand(
                CreateTriggerCommand.getCommandName(),
                "Binds a trigger to the interactable block (e.g. lever, button, pressure plate, etc.) you're looking at.",
                "/prticl trigger create <name>",
                "/prticl trigger create my_trigger"
        );
    }

    public static String getCommandName() {
        return CREATE_TRIGGER_COMMAND;
    }
}