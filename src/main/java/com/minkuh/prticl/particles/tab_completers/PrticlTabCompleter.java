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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrticlTabCompleter implements TabCompleter {

    List<String> particleCommandList = new ArrayList<>();
    List<String> particleList = new ArrayList<>();
    List<String> marker = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && label.equalsIgnoreCase("prticl")) {
            if (args.length == 1) {
                particleCommandList.clear();
                return sortedCommands(args[0]);
            }
            if (args[0].equalsIgnoreCase("spawn")) {
                if (args.length == 2) {
                    return sortedParticles(args[1]);
                }
                if (args.length == 3) {
                    if (label.equalsIgnoreCase("prticl") && args[2].isEmpty()) {
                        marker.clear();
                        marker.add("repeat_delay s");
                        return marker;
                    }
                }
            }
            if (args[0].equalsIgnoreCase("line")) {
                switch (args.length) {
                    case 2: {
                        marker.clear();
                        return marker(marker, "x");
                    }
                    case 3: {
                        marker.clear();
                        return marker(marker, "y");
                    }
                    case 4: {
                        marker.clear();
                        return marker(marker, "z");
                    }
                    case 5: {
                        marker.clear();
                        return marker(marker, "x2");
                    }
                    case 6: {
                        marker.clear();
                        return marker(marker, "y2");
                    }
                    case 7: {
                        marker.clear();
                        return marker(marker, "z2");
                    }
                    case 8: {
                        marker.clear();
                        return marker(marker, "density");
                    }
                    default: return Collections.emptyList();
                }
            }
        }
        return Collections.emptyList();
    }

    public List<String> sortedParticles(String arg) {
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

        final List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(arg, commands, completions);

        particleCommandList.clear();

        particleCommandList.addAll(completions);

        return particleCommandList;
    }

    private static List<String> marker(List<String> numberMarker, String marker) {
        numberMarker.clear();
        numberMarker.add(marker);
        return numberMarker;
    }
}
