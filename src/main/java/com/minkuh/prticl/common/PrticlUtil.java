package com.minkuh.prticl.common;

import com.minkuh.prticl.commands.CommandCategory;
import com.minkuh.prticl.commands.NodeProperties;
import com.minkuh.prticl.commands.PrticlCommands;
import com.minkuh.prticl.commands.base.ICommand;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Particle;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PrticlUtil {
    private static final List<String> PARTICLE_LIST = Arrays.stream(Particle.values())
            .map(p -> "minecraft:" + p.name().toLowerCase())
            .toList();

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

    public static List<String> sortedParticles(String arg) {
        final List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(arg, PARTICLE_LIST, completions);
        Collections.sort(completions);

        return completions;
    }

    public static String getSupportedParticle(String arg) throws IllegalArgumentException {
        arg = arg.contains(":") ? arg.split(":")[1].toUpperCase(Locale.ROOT) : arg.toUpperCase(Locale.ROOT);

        if (!EnumUtils.isValidEnum(Particle.class, arg))
            throw new IllegalArgumentException("The " + Particle.class.getName() + " enum doesn't contain the input particle \"" + arg + "\"");

        return arg.toUpperCase(Locale.ROOT);
    }

    public static List<NodeProperties> getNodeProperties(boolean editableByPlayer) {
        return Arrays
                .stream(NodeProperties.values())
                .filter(np -> np.isPlayerEditable() == editableByPlayer)
                .toList();
    }
}