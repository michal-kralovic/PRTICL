package com.minkuh.prticl.commands;

import com.minkuh.prticl.common.message.PrticlMessages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.minkuh.prticl.common.resources.PrticlConstants.PLAYER_COMMAND_ONLY;
import static com.minkuh.prticl.common.resources.PrticlConstants.PRTICL_COMMAND;


/**
 * An abstract PRTICL command class. <br>
 * Extend me for new commands!
 */
public abstract class PrticlCommand implements IPrticlCommand {
    /**
     * Allows access to PRTICL system messages.
     */
    PrticlMessages prticlMessage = new PrticlMessages();

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
     * Defines tab completion cases for each of the provided arguments. <br/>
     * It also assumes the arguments passed to it are stripped of the main command (prticl) and the main subcommand (e.g. node)
     *
     * @return Tab completion result
     */
    abstract public List<String> getTabCompletion(String[] args);

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