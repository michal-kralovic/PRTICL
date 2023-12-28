package com.minkuh.prticl.systemutil;

import com.minkuh.prticl.particles.commands.PrticlCreateCommand;
import com.minkuh.prticl.particles.commands.PrticlListCommand;
import com.minkuh.prticl.particles.commands.PrticlSpawnCommand;
import com.minkuh.prticl.particles.commands.PrticlLineCommand;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

/**
 * A utility class for executing Commands.
 */
public class CommandsUtil {
    PrticlSpawnCommand spawnParticleCommand;
    PrticlLineCommand lineCommand;
    PrticlCreateCommand createCommand;
    PrticlListCommand listCommand;
    public static BaseMessageComponents messageComponents = new BaseMessageComponents();

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
            case "hi": {
                if (sender instanceof Player && args.length != 0) {
                    sender.sendMessage(messageComponents.player("Hello " + args[0] + "!"));
                    return true;
                }
            }
            break;

            case "prticl": {
                if (args[0].equalsIgnoreCase("spawn"))
                    return spawnParticleCommand.command(args, sender);
                if (args[0].equalsIgnoreCase("line"))
                    return lineCommand.command(args, sender);
                if (args[0].equalsIgnoreCase("create"))
                    return createCommand.command(args, sender);
                if (args[0].equalsIgnoreCase("list"))
                    return listCommand.command(args, sender);
            }
            break;

        }
        return false;
    }
}
