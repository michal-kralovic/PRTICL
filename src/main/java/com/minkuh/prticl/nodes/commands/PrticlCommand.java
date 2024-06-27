package com.minkuh.prticl.nodes.commands;

import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.PLAYER_COMMAND_ONLY;
import static com.minkuh.prticl.systemutil.resources.PrticlStrings.PRTICL_COMMAND;


/**
 * An abstract PRTICL command class. <br>
 * Extend me for new commands!
 */
public abstract class PrticlCommand implements IPrticlCommand {
    /**
     * Allows access to PRTICL system messages.
     */
    BaseMessageComponents prticlMessage = new BaseMessageComponents();

    /**
     * A utility method to handle non-Player command triggers.
     *
     * @param sender The current sender of the command
     * @return TRUE if sent by the player.
     */
    public boolean isCommandSentByPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PLAYER_COMMAND_ONLY);
            return false;
        }
        return true;
    }

    /**
     * Defines the implementation of a Prticl command.
     *
     * @param args   The arguments of the Command
     * @param sender The Command sender (usually a Player)
     * @return TRUE if handled.
     */
    abstract public boolean execute(String[] args, CommandSender sender);

    public static String getCommandName() {
        return PRTICL_COMMAND;
    }
}