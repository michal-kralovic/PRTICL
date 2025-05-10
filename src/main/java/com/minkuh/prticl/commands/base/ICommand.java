package com.minkuh.prticl.commands.base;

import com.minkuh.prticl.commands.CommandCategory;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface ICommand {
    List<String> getTabCompletion(String[] args);
    TextComponent.Builder getHelpSection();
    boolean execute(String[] args, CommandSender sender);
    String getName();
    CommandCategory getCategory();
}