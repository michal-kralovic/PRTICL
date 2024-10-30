package com.minkuh.prticl.commands.trigger;

import com.minkuh.prticl.commands.PrticlCommand;
import com.minkuh.prticl.data.database.PrticlDatabase;
import com.minkuh.prticl.data.database.entities.Node;
import com.minkuh.prticl.data.database.entities.Trigger;
import com.minkuh.prticl.data.database.functions.PrticlNodeFunctions;
import com.minkuh.prticl.data.database.functions.PrticlTriggerFunctions;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.minkuh.prticl.common.PrticlConstants.*;

public class AddNodeTriggerCommand extends PrticlCommand {
    private final PrticlTriggerFunctions triggerFunctions;
    private final PrticlNodeFunctions nodeFunctions;

    public AddNodeTriggerCommand() {
        var prticlDb = new PrticlDatabase();
        this.nodeFunctions = prticlDb.getNodeFunctions();
        this.triggerFunctions = prticlDb.getTriggerFunctions();
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        sender.sendMessage(prticlMessage.player(Arrays.toString(args)));
        var triggerOpt = getTriggerFromDatabase(args[0], sender);
        var nodeOpt = getNodeFromDatabase(args[3], sender);

        if (triggerOpt.isEmpty()) {
            sender.sendMessage(prticlMessage.error("Couldn't obtain the trigger from the database!"));
            return true;
        }

        if (nodeOpt.isEmpty()) {
            sender.sendMessage(prticlMessage.error("Couldn't obtain the node from the database!"));
            return true;
        }

        var trigger = triggerOpt.get();
        var node = nodeOpt.get();

        triggerFunctions.addNodeToTrigger(trigger, node);
        sender.sendMessage(prticlMessage.player("Successfully assigned the node to the trigger!"));

        return true;
    }

    private static final List<String> marker = new ArrayList<>();

    @Override
    public List<String> getTabCompletion(String[] args) {
        marker.clear();

        return switch (args.length) {
            case 3 -> {
                marker.add(NODE_PARAM_ID + '/' + NODE_PARAM_NAME);
                yield marker;
            }
            default -> List.of();
        };
    }

    @Override
    public TextComponent.Builder getHelpDescription() {
        return createHelpSectionForCommand(
                getCommandName(),
                "Adds a specific node to a specific trigger.",
                "/prticl trigger <(id:X) or (node name)> node add <(id:X) or (node name)>",
                "/prticl trigger foobar node add id:1");
    }

    public static String getCommandName() {
        return NODE_ADD_COMMAND;
    }

    private Optional<Trigger> getTriggerFromDatabase(String arg, CommandSender sender) {
        try {
            return arg.startsWith("id:")
                    ? triggerFunctions.getById(Integer.parseInt(arg.substring("id:".length())))
                    : triggerFunctions.getByName(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(prticlMessage.error(INCORRECT_NODE_ID_FORMAT));
        } catch (Exception e) {
            sender.sendMessage(prticlMessage.error(e.getMessage()));
        }

        return Optional.empty();
    }

    private Optional<Node> getNodeFromDatabase(String arg, CommandSender sender) {
        try {
            return arg.startsWith("id:")
                    ? nodeFunctions.getById(Integer.parseInt(arg.substring("id:".length())))
                    : nodeFunctions.getByName(arg);
        } catch (NumberFormatException e) {
            sender.sendMessage(prticlMessage.error(INCORRECT_NODE_ID_FORMAT));
        } catch (Exception e) {
            sender.sendMessage(prticlMessage.error(e.getMessage()));
        }
        return Optional.empty();
    }
}