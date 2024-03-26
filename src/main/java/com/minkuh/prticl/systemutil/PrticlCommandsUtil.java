package com.minkuh.prticl.systemutil;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.nodes.commands.*;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * A utility class for executing Commands.
 */
public class PrticlCommandsUtil {
    private final PrticlSpawnCommand spawnCommand;
    private final PrticlLineCommand lineCommand;
    private final PrticlCreateCommand createCommand;
    private final PrticlListCommand listCommand;
    private final PrticlHelpCommand helpCommand;

    public PrticlCommandsUtil(Prticl plugin) throws SQLException {
        spawnCommand = new PrticlSpawnCommand(plugin);
        lineCommand = new PrticlLineCommand(plugin);
        createCommand = new PrticlCreateCommand(plugin);
        listCommand = new PrticlListCommand(plugin);
        helpCommand = new PrticlHelpCommand();
    }

    /**
     * Determines which Command to execute based on the input arguments.
     *
     * @return TRUE if handled.
     */
    public boolean commandSwitcher(Command command, CommandSender sender, String[] args) {
        BaseMessageComponents prticlMessage = new BaseMessageComponents();
        switch (command.getName().toLowerCase(Locale.ROOT)) {

            case PRTICL_COMMAND -> {

                if (args.length > 1 // if args is longer than 1 (has more args past "node")
                        // AND the arg is "node" OR "n"
                        && (args[0].equalsIgnoreCase(NODE_DEFAULT_NAME) || args[0].equalsIgnoreCase(String.valueOf(NODE_DEFAULT_NAME.charAt(0))))) {
                    String[] commandArgs = Arrays.stream(args).skip(2).toArray(String[]::new);

                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case SPAWN_COMMAND -> spawnCommand.command(commandArgs, sender);
                        case LINE_COMMAND -> lineCommand.command(commandArgs, sender);
                        case CREATE_COMMAND -> createCommand.command(commandArgs, sender);
                        case LIST_COMMAND -> listCommand.command(commandArgs, sender);
                    }
                }

                if (args[0].equalsIgnoreCase(PrticlHelpCommand.getCommandName()) // if arg is "help" OR "h"
                        || args[0].equalsIgnoreCase(String.valueOf(PrticlHelpCommand.getCommandName().charAt(0)))) {
                    helpCommand.command(Arrays.stream(args).skip(1).toArray(String[]::new), sender);
                }

            }

            default -> sender.sendMessage(prticlMessage.error(UNKNOWN_COMMAND));
        }
        return false;
    }
}