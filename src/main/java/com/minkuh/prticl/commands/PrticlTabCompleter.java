package com.minkuh.prticl.commands;

import com.minkuh.prticl.commands.base.ICommand;
import com.minkuh.prticl.common.PrticlUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PrticlTabCompleter implements TabCompleter {
    private final Map<String, ICommand> commands;
    private static final List<String> nodeSubcommandNames = PrticlUtil.getSubcommandNames(CommandCategory.NODE);
    private static final List<String> triggerSubcommandNames = PrticlUtil.getSubcommandNames(CommandCategory.TRIGGER);
    private static final List<String> playerSubcommandNames = PrticlUtil.getSubcommandNames(CommandCategory.PLAYER);

    public PrticlTabCompleter(Map<String, ICommand> commands) {
        this.commands = commands;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) || !label.equalsIgnoreCase("prticl"))
            return List.of();

        if (args.length == 1)
            return getSortedStrings(args[0], List.of("node", "trigger", "help", "player"));

        if (args.length == 2) {
            var subCommand = args[0].toLowerCase(Locale.ROOT);

            if (PrticlUtil.matchesWordOrLetter(subCommand, PrticlCommands.Names.NODE)) {
                return getSortedStrings(args[1], nodeSubcommandNames);
            } else if (PrticlUtil.matchesWordOrLetter(subCommand, PrticlCommands.Names.TRIGGER)) {
                return getSortedStrings(args[1], triggerSubcommandNames);
            } else if (PrticlUtil.matchesWordOrLetter(subCommand, PrticlCommands.Names.PLAYER)) {
                return getSortedStrings(args[1], playerSubcommandNames);
            }
        }

        var tabCompletionArray = Arrays.copyOfRange(args, 1, args.length);
        if (PrticlUtil.matchesWordOrLetter(args[0], PrticlCommands.Names.NODE)) {
            if (nodeSubcommandNames.stream().anyMatch(name -> args[1].equalsIgnoreCase(name))) {
                return PrticlUtil.getCommandByCategoryAndName(commands, CommandCategory.NODE, args[1]).getTabCompletion(tabCompletionArray);
            } else {
                return List.of();
            }
        } else if (PrticlUtil.matchesWordOrLetter(args[0], PrticlCommands.Names.TRIGGER)) {
            if (triggerSubcommandNames.stream().anyMatch(name -> args[1].equalsIgnoreCase(name))) {
                return PrticlUtil.getCommandByCategoryAndName(commands, CommandCategory.TRIGGER, args[1]).getTabCompletion(tabCompletionArray);
            } else {
                return List.of();
            }
        } else {
            return List.of();
        }
    }

    private static @NotNull List<String> getSortedStrings(String userArg, List<String> listToSort) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(userArg, listToSort, completions);

        return completions;
    }
}