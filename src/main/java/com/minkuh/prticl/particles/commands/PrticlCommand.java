package com.minkuh.prticl.particles.commands;

import com.minkuh.prticl.systemutil.message.BaseMessageComponents;
import org.bukkit.command.CommandSender;


/**
 * An abstract PRTICL command class.
 */
public abstract class PrticlCommand {
    /**
     * Allows access to PRTICL system messages.
     */
    BaseMessageComponents messageComponents = new BaseMessageComponents();

    /**
     * The command to be run.
     * @param args The arguments of the Command
     * @param sender The Command sender (usually a Player)
     * @return TRUE if handled.
     */
    abstract boolean command(String[] args, CommandSender sender);

    /**
     * A comfy utility method that returns the name of the current Command.
     * @return This Command's name.
     */
    abstract String getCommandName();
}
