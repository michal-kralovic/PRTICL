package com.minkuh.prticl.commands;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface IPrticlCommand {

    boolean isCommandSentByPlayer(CommandSender sender);
    List<String> getTabCompletion(String[] args);
    boolean execute(String[] args, CommandSender sender);
}