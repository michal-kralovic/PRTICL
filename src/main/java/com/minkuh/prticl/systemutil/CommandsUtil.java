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
            case "prticl": {
                if (args[0].equalsIgnoreCase("node")) {
                    String[] commandArgs = Arrays.stream(args).skip(2).toArray(String[]::new);

                    if (args[1].equalsIgnoreCase(PrticlSpawnCommand.getCommandName()))
                        return spawnParticleCommand.command(commandArgs, sender);
                    if (args[1].equalsIgnoreCase(PrticlLineCommand.getCommandName()))
                        return lineCommand.command(commandArgs, sender);
                    if (args[1].equalsIgnoreCase(PrticlCreateCommand.getCommandName()))
                        return createCommand.command(commandArgs, sender);
                    if (args[1].equalsIgnoreCase(PrticlListCommand.getCommandName()))
                        return listCommand.command(commandArgs, sender);
                }
            }
            break;
        }
        return false;
    }
}
