package com.minkuh.prticl.nodes.tab_completers;

import com.minkuh.prticl.nodes.commands.*;
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

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.*;

// TODO: Investigate ditching a common class, instead separate TabCompleter into each class using TabExecutor
public class PrticlTabCompleter implements TabCompleter {

    List<String> particleCommandList = new ArrayList<>();
    static List<String> particleList = new ArrayList<>();
    List<String> marker = new ArrayList<>();


    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && label.equalsIgnoreCase(PrticlCommand.getCommandName())) {
            if (args.length == 1)
                return getSortedSubcommands(args[0]);

            if (args.length == 2 && (args[0].equalsIgnoreCase(NODE_DEFAULT_NAME) || args[0].equalsIgnoreCase(NODE_DEFAULT_NAME.substring(0, 1)))) {
                particleCommandList.clear();
                return getSortedCommands(args[1]);
            }

            return switch (args[1].toLowerCase(Locale.ROOT)) {
                case (SPAWN_COMMAND) -> spawnLogic(marker, args);
                case (LINE_COMMAND) -> lineLogic(marker, args);
                case (CREATE_COMMAND) -> createLogic(marker, args);
                case (LIST_COMMAND) -> listLogic(marker, args);
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

    /**
     * Returns a list of the main prticl subcommands (e.g. help, node)
     *
     * @param arg the user's input to base the output on (dynamically alters the list to only show relevant particles)
     * @return A list of all/relevant particles.
     */
    public static List<String> getSortedSubcommands(String arg) {
        final List<String> subcommands = new ArrayList<>(){{
            add(NODE_DEFAULT_NAME);
            add(HELP_COMMAND);
        }};
        List<String> completions = new ArrayList<>();

        StringUtil.copyPartialMatches(arg, subcommands, completions);

        return completions;
    }

    /**
     * Returns a List of Strings, a list of relevant parameters for the line command.
     *
     * @param marker utility marker input
     * @param args   the user's input to base the output on
     * @return A list of relevant tab completions.
     */
    private static List<String> lineLogic(List<String> marker, String[] args) {
        return switch (args.length) {
            case 3 -> {
                marker.clear();
                yield marker(marker, "x y z");
            }
            case 4 -> {
                marker.clear();
                yield marker(marker, "y z");
            }
            case 5 -> {
                marker.clear();
                yield marker(marker, "z");
            }
            case 6 -> {
                marker.clear();
                yield marker(marker, "x2 y2 z2");
            }
            case 7 -> {
                marker.clear();
                yield marker(marker, "y2 z2");
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

    /**
     * Returns a List of Strings, a list of relevant parameters for the create command.
     *
     * @param marker utility marker input
     * @param args   the user's input to base the output on
     * @return A list of relevant tab completions.
     */
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

    /**
     * Returns a List of Strings, a list of relevant parameters for the list command.
     *
     * @param marker utility marker input
     * @param args   the user's input to base the output on
     * @return A list of relevant tab completions.
     */
    private static List<String> listLogic(List<String> marker, String[] args) {
        if (args.length == 2) {
            marker.clear();
            marker.add("page");
            return marker;
        }
        return Collections.emptyList();
    }

    /**
     * Returns a List of Strings - a list of sorted Minecraft particles taken from the Particle enum class. <br>
     * Prefixes every particle with "minecraft:" for vanilla MC /particle command parity, but it doesn't support
     * modded namespaces.
     *
     * @param arg the user's input to base the output on (dynamically alters the list to only show relevant particles)
     * @return A list of all/relevant particles.
     */
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

    /**
     * Returns a List of Strings, a list of sorted PRTICL command names to autocomplete the subcommand parameter.
     *
     * @param arg the user's input to base the output on (dynamically alters the list to only show relevant sub-commands)
     * @return A list of all/relevant sub-commands.
     */
    private List<String> getSortedCommands(String arg) {
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

    /**
     * A quick utility List of Strings, a marker to simply put a single value into with some operations for QoL-filled code writing.
     *
     * @param marker    utility marker input
     * @param strMarker what text the marker should contain/display
     * @return A list with a single value—the chosen input string.
     */
    private static List<String> marker(List<String> marker, String strMarker) {
        marker.clear();
        marker.add(strMarker);
        return marker;
    }
}