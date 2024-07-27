package com.minkuh.prticl.systemutil;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.message.PrticlMessages;
import com.minkuh.prticl.commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.*;

import static com.minkuh.prticl.common.resources.PrticlConstants.*;

/**
 * A utility class for executing Commands.
 */
public class PrticlCommandsUtil {

    public PrticlCommandsUtil(Prticl plugin) throws SQLException {
        commands.put(PrticlSpawnCommand.getCommandName(), new PrticlSpawnCommand(plugin));
        commands.put(PrticlDespawnCommand.getCommandName(), new PrticlDespawnCommand(plugin));
        commands.put(PrticlLineCommand.getCommandName(), new PrticlLineCommand(plugin));
        commands.put(PrticlCreateCommand.getCommandName(), new PrticlCreateCommand(plugin));
        commands.put(PrticlListCommand.getCommandName(), new PrticlListCommand(plugin));
        commands.put(PrticlHelpCommand.getCommandName(), new PrticlHelpCommand());
    }

    /**
     * Determines which Command to execute based on the input arguments.
     *
     * @return TRUE if handled.
     */
    public boolean commandExecutor(Command command, CommandSender sender, String[] args) {
        PrticlMessages prticlMessage = new PrticlMessages();
        switch (command.getName().toLowerCase(Locale.ROOT)) {

            case PRTICL_COMMAND -> {

                if (args.length > 1 // if args is longer than 1 (has more args past "/prticl <subcommand>")
                        // AND the subcommand arg is "node" OR "n"
                        && (args[0].equalsIgnoreCase(NODE_DEFAULT_NAME) || args[0].equalsIgnoreCase(String.valueOf(NODE_DEFAULT_NAME.charAt(0))))) {
                    String[] commandArgs = Arrays.stream(args).skip(2).toArray(String[]::new);

                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case SPAWN_COMMAND -> commands.get(PrticlSpawnCommand.getCommandName()).execute(commandArgs, sender);
                        case DESPAWN_COMMAND -> commands.get(PrticlDespawnCommand.getCommandName()).execute(commandArgs, sender);
                        case LINE_COMMAND -> commands.get(PrticlLineCommand.getCommandName()).execute(commandArgs, sender);
                        case CREATE_COMMAND -> commands.get(PrticlCreateCommand.getCommandName()).execute(commandArgs, sender);
                        case LIST_COMMAND -> commands.get(PrticlListCommand.getCommandName()).execute(commandArgs, sender);
                    }
                }

                if (args[0].equalsIgnoreCase(PrticlHelpCommand.getCommandName()) // if arg is "help" OR "h"
                        || args[0].equalsIgnoreCase(String.valueOf(PrticlHelpCommand.getCommandName().charAt(0)))) {
                    commands.get(PrticlHelpCommand.getCommandName()).execute(Arrays.stream(args).skip(1).toArray(String[]::new), sender);
                }

            }

            default -> sender.sendMessage(prticlMessage.error(UNKNOWN_COMMAND));
        }
        return true;
    }

    public static final Map<String, IPrticlCommand> commands = new HashMap<>();
}