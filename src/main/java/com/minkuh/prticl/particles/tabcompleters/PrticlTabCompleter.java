package com.minkuh.prticl.particles.tabcompleters;

import com.minkuh.prticl.particles.commands.*;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

// TODO: Investigate ditching a common class, instead separate TabCompleter into each class using TabExecutor
public class PrticlTabCompleter implements TabCompleter {

    List<String> particleCommandList = new ArrayList<>();
    static List<String> particleList = new ArrayList<>();
    List<String> marker = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && label.equalsIgnoreCase(PrticlCommand.getCommandName())) {
            if (args.length == 1) {
                marker.clear();
                marker.add("node");
                return marker;
            }

            if (args.length == 2 && args[0].equals("node")) {
                particleCommandList.clear();
                return sortedCommands(args[1]);
            }

            // TODO: Manage to figure out how to overcome constants restriction here, to eliminate magic strings
            return switch (args[1].toLowerCase(Locale.ROOT)) {
                case ("spawn") -> spawnLogic(marker, args);
                case ("line") -> lineLogic(marker, args);
                case ("create") -> createLogic(marker, args);
                case ("list") -> listLogic(marker, args);
                default -> Collections.emptyList();
            };
        }
        return Collections.emptyList();
    }

    private static List<String> spawnLogic(List<String> marker, String[] args) {
        if (args.length == 3) {
            marker.clear();
            return marker(marker, NODE_PARAM_ID + "/" + NODE_PARAM_NAME);
        }
        return Collections.emptyList();
    }

    private static List<String> lineLogic(List<String> marker, String[] args) {
        return switch (args.length) {
            case 3 -> {
                marker.clear();
                yield marker(marker, "x");
            }
            case 4 -> {
                marker.clear();
                yield marker(marker, "y");
            }
            case 5 -> {
                marker.clear();
                yield marker(marker, "z");
            }
            case 6 -> {
                marker.clear();
                yield marker(marker, "x2");
            }
            case 7 -> {
                marker.clear();
                yield marker(marker, "y2");
            }
            case 8 -> {
                marker.clear();
                yield marker(marker, "z2");
            }
            case 9 -> {
                marker.clear();
                yield marker(marker, NODE_PARAM_PARTICLE_DENSITY);
            }
            default -> Collections.emptyList();
        };
    }

    private static List<String> createLogic(List<String> marker, String[] args) {
        return switch (args.length) {
            case 3 -> {
                marker.clear();
                yield marker(marker, NODE_PARAM_NAME);
            }
            case 4 -> sortedParticles(args[3]);
            case 5 -> {
                marker.clear();
                yield marker(marker, NODE_PARAM_REPEAT_DELAY);
            }
            case 6 -> {
                marker.clear();
                yield marker(marker, NODE_PARAM_PARTICLE_DENSITY);
            }
            case 7 -> {
                marker.clear();
                yield marker(marker, "x y z");
            }
            case 8 -> {
                marker.clear();
                yield marker(marker, "y z");
            }
            case 9 -> {
                marker.clear();
                yield marker(marker, "z");
            }
            default -> Collections.emptyList();
        };
    }

    private static List<String> listLogic(List<String> marker, String[] args) {
        if (args.length == 2) {
            marker.clear();
            marker.add("page");
            return marker;
        }
        return Collections.emptyList();
    }

    public static List<String> sortedParticles(String arg) {
        final List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(arg, Arrays.stream(Particle.values())
                .map(p -> "minecraft:" + p.name().toLowerCase())
                .collect(Collectors.toList()), completions);
        Collections.sort(completions);

        particleList.clear();
        particleList.addAll(completions);

        return particleList;
    }


    public List<String> sortedCommands(String arg) {
        final List<String> commands = new ArrayList<>();
        commands.add(PrticlSpawnCommand.getCommandName());
        commands.add(PrticlLineCommand.getCommandName());
        commands.add(PrticlCreateCommand.getCommandName());
        commands.add(PrticlListCommand.getCommandName());

        List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(arg, commands, completions);

        particleCommandList.clear();
        particleCommandList.addAll(completions);

        return particleCommandList;
    }

    private static List<String> marker(List<String> marker, String strMarker) {
        marker.clear();
        marker.add(strMarker);
        return marker;
    }
}
