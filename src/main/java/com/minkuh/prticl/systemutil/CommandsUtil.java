package com.minkuh.prticl.systemutil;

import com.minkuh.prticl.particles.commands.PrticlCreateCommand;
import com.minkuh.prticl.particles.commands.PrticlLineCommand;
import com.minkuh.prticl.particles.commands.PrticlListCommand;
import com.minkuh.prticl.particles.commands.PrticlSpawnCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Locale;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

/**
 * A utility class for executing Commands.
 */
public class CommandsUtil {
    PrticlSpawnCommand spawnParticleCommand;
    PrticlLineCommand lineCommand;
    PrticlCreateCommand createCommand;
    PrticlListCommand listCommand;

    public CommandsUtil(Plugin plugin) {
        spawnParticleCommand = new PrticlSpawnCommand(plugin);
        lineCommand = new PrticlLineCommand(plugin);
        createCommand = new PrticlCreateCommand(plugin);
        listCommand = new PrticlListCommand(plugin);
    }

    /**
     * Determines which Command to execute based on the input arguments.
     *
     * @return TRUE if handled.
     */
    public boolean commandSwitcher(Command command, CommandSender sender, String[] args) {
        switch (command.getName().toLowerCase(Locale.ROOT)) {
            case PRTICL_COMMAND -> {
                if (args[0].equalsIgnoreCase(NODE_DEFAULT_NAME)) {
                    String[] commandArgs = Arrays.stream(args).skip(2).toArray(String[]::new);

                    switch (args[1].toLowerCase(Locale.ROOT)) {
                        case SPAWN_COMMAND -> spawnParticleCommand.command(commandArgs, sender);
                        case LINE_COMMAND -> lineCommand.command(commandArgs, sender);
                        case CREATE_COMMAND -> createCommand.command(commandArgs, sender);
                        case LIST_COMMAND -> listCommand.command(commandArgs, sender);
                    }
                }
            }
        }
        return false;
    }
}
