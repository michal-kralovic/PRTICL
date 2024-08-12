package com.minkuh.prticl.common.systemutil;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.HelpCommand;
import com.minkuh.prticl.commands.IPrticlCommand;
import com.minkuh.prticl.commands.ListCommand;
import com.minkuh.prticl.commands.node.CreateNodeCommand;
import com.minkuh.prticl.commands.node.DespawnNodeCommand;
import com.minkuh.prticl.commands.node.SpawnNodeCommand;
import com.minkuh.prticl.commands.trigger.AddNodeTriggerCommand;
import com.minkuh.prticl.commands.trigger.CreateTriggerCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.minkuh.prticl.common.PrticlConstants.*;

/**
 * A utility class for executing Commands.
 */
public class PrticlCommandsUtil {

    public PrticlCommandsUtil(Prticl plugin) {
        COMMANDS.put(SpawnNodeCommand.getCommandName(), new SpawnNodeCommand(plugin));
        COMMANDS.put(DespawnNodeCommand.getCommandName(), new DespawnNodeCommand(plugin));
        COMMANDS.put(CreateNodeCommand.getCommandName(), new CreateNodeCommand(plugin));
        COMMANDS.put(ListCommand.getCommandName(), new ListCommand(plugin));
        COMMANDS.put(HelpCommand.getCommandName(), new HelpCommand());
        COMMANDS.put(CreateTriggerCommand.getCommandName(), new CreateTriggerCommand(plugin));
        COMMANDS.put(AddNodeTriggerCommand.getCommandName(), new AddNodeTriggerCommand(plugin));
    }

    /**
     * Determines which Command to execute based on the input arguments.
     *
     * @return TRUE if handled.
     */
    public boolean commandExecutor(Command command, CommandSender sender, String[] args) {
        if (!command.getName().equalsIgnoreCase(PRTICL_COMMAND)) return true;
        if (args.length <= 1) return true;

        String[] commandArgs = Arrays.stream(args).skip(2).toArray(String[]::new);
        if (args[0].equalsIgnoreCase(NODE_DEFAULT_NAME) || args[0].equalsIgnoreCase(String.valueOf(NODE_DEFAULT_NAME.charAt(0)))) {
            // /prticl node (...)

            switch (args[1].toLowerCase(Locale.ROOT)) {
                case SPAWN_COMMAND -> COMMANDS.get(SpawnNodeCommand.getCommandName()).execute(commandArgs, sender);
                case DESPAWN_COMMAND -> COMMANDS.get(DespawnNodeCommand.getCommandName()).execute(commandArgs, sender);
                case CREATE_COMMAND -> COMMANDS.get(CreateNodeCommand.getCommandName()).execute(commandArgs, sender);
                case LIST_COMMAND -> COMMANDS.get(ListCommand.getCommandName()).execute(commandArgs, sender);
            }
        }

        if (args[0].equalsIgnoreCase(TRIGGER) || args[0].equalsIgnoreCase(String.valueOf(TRIGGER.charAt(0)))) {
            // /prticl trigger (...)

            if (args[1].toLowerCase(Locale.ROOT).equals(CREATE_COMMAND)) {
                COMMANDS.get(CreateTriggerCommand.getCommandName()).execute(commandArgs, sender);
            } else {
                // prticl trigger (name/id)
                executeUnnamedTriggerCommand(Arrays.stream(args).skip(1).toArray(String[]::new), sender);
            }
        }

        if (args[0].equalsIgnoreCase(HelpCommand.getCommandName())
                || args[0].equalsIgnoreCase(String.valueOf(HelpCommand.getCommandName().charAt(0)))) {
            COMMANDS.get(HelpCommand.getCommandName()).execute(Arrays.stream(args).skip(1).toArray(String[]::new), sender);
        }

        return true;
    }

    private boolean executeUnnamedTriggerCommand(String[] args, CommandSender sender) {
        if (args.length <= 1) return true;

        if (args[1].equalsIgnoreCase("node") && args.length > 2) {
            if (args[2].equalsIgnoreCase("add")) {
                COMMANDS.get(AddNodeTriggerCommand.getCommandName()).execute(args, sender);
            }
        }

        return true;
    }

    public static final Map<String, IPrticlCommand> COMMANDS = new HashMap<>();
}