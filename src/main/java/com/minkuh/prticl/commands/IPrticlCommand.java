package com.minkuh.prticl.commands;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface IPrticlCommand {

    boolean isCommandSentByPlayer(CommandSender sender);
    List<String> getTabCompletion(String[] args);
    TextComponent.Builder getHelpDescription();
    boolean execute(String[] args, CommandSender sender);
}