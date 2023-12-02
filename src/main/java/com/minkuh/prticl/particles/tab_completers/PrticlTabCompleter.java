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
    List<String> numberMarker = new ArrayList<>();

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && label.equalsIgnoreCase("particle")) {
            if (args.length == 1) {
                particleCommandList.clear();
                return sortedCommands(args[0]);
            }
            if (args[0].equalsIgnoreCase("spawn")) {
                if (args.length == 2) {
                    particleList.clear();
                    particleList.add("particle");
                    return sortedParticles(args[1]);
                }
                if (args.length == 3) {
                    if (label.equalsIgnoreCase("particle") && args[2].isEmpty()) {
                        numberMarker.add("repeat_delay s");
                        return numberMarker;
                    }
                }
            }
            if (args[0].equalsIgnoreCase("line")) {
                switch (args.length) {
                    case 2: {
                        numberMarker.clear();
                        numberMarker.add("x");
                        return numberMarker;
                    }
                    case 3: {
                        numberMarker.clear();
                        numberMarker.add("y");
                        return numberMarker;
                    }
                    case 4: {
                        numberMarker.clear();
                        numberMarker.add("z");
                        return numberMarker;
                    }
                    case 5: {
                        numberMarker.clear();
                        numberMarker.add("x2");
                        return numberMarker;
                    }
                    case 6: {
                        numberMarker.clear();
                        numberMarker.add("y2");
                        return numberMarker;
                    }
                    case 7: {
                        numberMarker.clear();
                        numberMarker.add("z2");
                        return numberMarker;
                    }
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
        final List<String> completions = new ArrayList<>();

        completions.add("spawn");
        completions.add("line");

        particleCommandList.clear();

        particleList.addAll(completions);

        return particleList;
    }
}
