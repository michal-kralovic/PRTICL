package com.minkuh.prticl.nodes.commands;

import org.bukkit.command.CommandSender;

public interface IPrticlCommand {

    public boolean isCommandSentByPlayer(CommandSender sender);

    abstract public boolean command(String[] args, CommandSender sender);
}
