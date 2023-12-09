package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


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
            sender.sendMessage("This command can only be executed by a player!");
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

    /**
     * A comfy utility method that returns the name of the current Command.
     * @return This Command's name.
     */
    abstract String getCommandName();
}
