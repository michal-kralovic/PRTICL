package com.minkuh.prticl.systemutil;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.node.CreateNodeCommand;
import com.minkuh.prticl.commands.node.DespawnNodeCommand;
import com.minkuh.prticl.commands.node.SpawnNodeCommand;
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
        COMMANDS.put(SpawnNodeCommand.getCommandName(), new SpawnNodeCommand(plugin));
        COMMANDS.put(DespawnNodeCommand.getCommandName(), new DespawnNodeCommand(plugin));
        COMMANDS.put(CreateNodeCommand.getCommandName(), new CreateNodeCommand(plugin));
        COMMANDS.put(ListCommand.getCommandName(), new ListCommand(plugin));
        COMMANDS.put(HelpCommand.getCommandName(), new HelpCommand());
    }

    /**
     * Determines which Command to execute based on the input arguments.
     *
     * @return TRUE if handled.
     */
    public boolean commandExecutor(Command command, CommandSender sender, String[] args) {
        switch (command.getName().toLowerCase(Locale.ROOT)) {

            case PRTICL_COMMAND -> {
                if (args.length > 1 // if args is longer than 1 (has more args past "/prticl <subcommand>")
                        // AND the subcommand arg is "node" OR "n"
                        && (args[0].equalsIgnoreCase(NODE_DEFAULT_NAME) || args[0].equalsIgnoreCase(String.valueOf(NODE_DEFAULT_NAME.charAt(0))))) {
                    String[] commandArgs = Arrays.stream(args).skip(2).toArray(String[]::new);

                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case SPAWN_COMMAND -> COMMANDS.get(SpawnNodeCommand.getCommandName()).execute(commandArgs, sender);
                        case DESPAWN_COMMAND -> COMMANDS.get(DespawnNodeCommand.getCommandName()).execute(commandArgs, sender);
                        case CREATE_COMMAND -> COMMANDS.get(CreateNodeCommand.getCommandName()).execute(commandArgs, sender);
                        case LIST_COMMAND -> COMMANDS.get(ListCommand.getCommandName()).execute(commandArgs, sender);
                    }
                }

                if (args[0].equalsIgnoreCase(HelpCommand.getCommandName())
                        || args[0].equalsIgnoreCase(String.valueOf(HelpCommand.getCommandName().charAt(0)))) {
                    COMMANDS.get(HelpCommand.getCommandName()).execute(Arrays.stream(args).skip(1).toArray(String[]::new), sender);
                }
            }

            default -> sender.sendMessage(new PrticlMessages().error(UNKNOWN_COMMAND));
        }
        return true;
    }

    public static final Map<String, IPrticlCommand> COMMANDS = new HashMap<>();
}