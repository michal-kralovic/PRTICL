package com.minkuh.prticl.nodes.commands;

import org.bukkit.command.CommandSender;

public interface IPrticlCommand {

    boolean isCommandSentByPlayer(CommandSender sender);

    boolean execute(String[] args, CommandSender sender);
}