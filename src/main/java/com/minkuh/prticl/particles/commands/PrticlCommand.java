package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.PLAYER_COMMAND_ONLY;
import static com.minkuh.prticl.systemutil.resources.PrticlStrings.PRTICL_COMMAND;


/**
 * An abstract PRTICL command class.
 */
public abstract class PrticlCommand {
    /**
     * Allows access to PRTICL system messages.
     */
    BaseMessageComponents prticlMessage = new BaseMessageComponents();

    /**
     * A utility method to handle non-Player command triggers.
     * @param sender The current sender of the command
     * @return TRUE if sent by the player.
     */
    public boolean isCommandSentByPlayer(CommandSender sender) {
        boolean result = true;
        if (!(sender instanceof Player)) {
            result = false;
            sender.sendMessage(PLAYER_COMMAND_ONLY);
        }
        return result;
    }

    /**
     * The command to be run.
     * @param args The arguments of the Command
     * @param sender The Command sender (usually a Player)
     * @return TRUE if handled.
     */
    abstract public boolean command(String[] args, CommandSender sender);

    public static String getCommandName() {
        return PRTICL_COMMAND;
    }
}
