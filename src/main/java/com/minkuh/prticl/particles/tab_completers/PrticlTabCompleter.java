package com.minkuh.prticl.particles.tab_completers;

import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrticlTabCompleter implements TabCompleter {

    List<String> particleCommandList = new ArrayList<>();
    static List<String> particleList = new ArrayList<>();
    List<String> marker = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && label.equalsIgnoreCase("prticl")) {
            if (args.length == 1) {
                particleCommandList.clear();
                return sortedCommands(args[0]);
            }

            return switch (args[0].toLowerCase(Locale.ROOT)) {
                case ("spawn") -> spawnLogic(marker, args);
                case ("line") -> lineLogic(marker, args);
                case ("create") -> createLogic(marker, args);
                default -> Collections.emptyList();
            };
        }
        return Collections.emptyList();
    }

    private static List<String> lineLogic(List<String> marker, String[] args) {
        return switch (args.length) {
            case 2 -> {
                marker.clear();
                yield marker(marker, "x");
            }
            case 3 -> {
                marker.clear();
                yield marker(marker, "y");
            }
            case 4 -> {
                marker.clear();
                yield marker(marker, "z");
            }
            case 5 -> {
                marker.clear();
                yield marker(marker, "x2");
            }
            case 6 -> {
                marker.clear();
                yield marker(marker, "y2");
            }
            case 7 -> {
                marker.clear();
                yield marker(marker, "z2");
            }
            case 8 -> {
                marker.clear();
                yield marker(marker, "density");
            }
            default -> Collections.emptyList();
        };
    }

    private static List<String> spawnLogic(List<String> marker, String[] args) {
        if (args.length == 2) {
            return sortedParticles(args[1]);
        }
        if (args.length == 3) {
            if (args[2].isEmpty()) {
                marker.clear();
                marker.add("repeat_delay (ticks)");
                return marker;
            }
        }
        return Collections.emptyList();
    }

    private static List<String> createLogic(List<String> marker, String[] args) {
        return switch (args.length) {
            case 2 -> {
                marker.clear();
                yield marker(marker, "name");
            }
            case 3 -> sortedParticles(args[2]);
            case 4 -> {
                marker.clear();
                yield marker(marker, "x y z");
            }
            case 5 -> {
                marker.clear();
                yield marker(marker, "y z");
            }
            case 6 -> {
                marker.clear();
                yield marker(marker, "z");
            }
            default -> Collections.emptyList();
        };
    }

    public static List<String> sortedParticles(String arg) {
        final List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(arg, Stream.of(Particle.values()).map(Particle::name).collect(Collectors.toList()), completions);
        Collections.sort(completions);

        particleList.clear();

        particleList.addAll(completions);

        return particleList;
    }

    public List<String> sortedCommands(String arg) {
        final List<String> commands = new ArrayList<>();
        commands.add("spawn");
        commands.add("line");
        commands.add("create");

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
