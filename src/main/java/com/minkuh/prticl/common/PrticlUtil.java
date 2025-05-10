package com.minkuh.prticl.common;

import com.minkuh.prticl.commands.CommandCategory;
import com.minkuh.prticl.commands.PrticlCommands;
import com.minkuh.prticl.commands.base.ICommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class PrticlUtil {
    public static boolean matchesWordOrLetter(String first, String second) {
        if (first.equalsIgnoreCase(second))
            return true;

        return first.toLowerCase(Locale.ROOT).toCharArray()[0] == second.toLowerCase(Locale.ROOT).toCharArray()[0];
    }

    public static Map<String, ICommand> filterCommandsByCategory(Map<String, ICommand> commands, CommandCategory filter) {
        return commands.entrySet()
                .stream()
                .filter(c -> c.getValue().getCategory() == filter)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static ICommand getCommandByName(Map<String, ICommand> commands, String commandName) {
        return commands
                .entrySet()
                .stream()
                .filter(cmd -> cmd.getValue().getName().contains(commandName))
                .findFirst()
                .orElseThrow()
                .getValue();
    }

    public static ICommand getCommandByCategoryAndName(Map<String, ICommand> commands, CommandCategory category, String commandName) {
        var filteredCmds = PrticlUtil.filterCommandsByCategory(commands, category);
        return filteredCmds
                .entrySet()
                .stream()
                .filter(c -> c.getValue().getName().split("-")[0].equalsIgnoreCase(commandName))
                .findFirst()
                .orElseThrow()
                .getValue();
    }

    public static @NotNull List<String> getSubcommandNames(CommandCategory category) {
        return PrticlUtil.filterCommandsByCategory(PrticlCommands.getCommands(), category)
                .values()
                .stream()
                .map(c -> ((com.minkuh.prticl.commands.base.Command) c).getCommandName())
                .toList();
    }
}