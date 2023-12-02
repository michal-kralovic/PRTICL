package com.minkuh.prticl.systemutil;

import com.minkuh.prticl.particles.commands.PrticlSpawnCommand;
import com.minkuh.prticl.particles.commands.PrticlVectorCommand;
import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public class CommandsUtil {

    PrticlSpawnCommand spawnParticleCommand;
    PrticlVectorCommand vectorCommand;
    public static BaseMessageComponents messageComponents = new BaseMessageComponents();

    public CommandsUtil(Plugin plugin) {
        spawnParticleCommand = new PrticlSpawnCommand(plugin);
        vectorCommand = new PrticlVectorCommand(plugin);
    }

    public boolean commandSwitcher(Command command, CommandSender sender, String[] args) {
        switch (command.getName().toLowerCase(Locale.ROOT)) {
            case "hi": {
                if (sender instanceof Player && args.length != 0) {
                    sender.sendMessage(messageComponents.prticlPlayerMessage("Hello " + args[0] + "!"));
                    return true;
                }
            }
            break;

            case "particle": {
                if (args[0].equalsIgnoreCase("spawn"))
                    return spawnParticleCommand.command(args, sender);
                if (args[0].equalsIgnoreCase("line"))
                    return vectorCommand.command(args, sender);
            }
            break;

        }
        return false;
    }
}
