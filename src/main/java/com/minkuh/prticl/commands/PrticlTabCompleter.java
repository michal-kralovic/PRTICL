package com.minkuh.prticl.commands;

import com.minkuh.prticl.commands.node.CreateNodeCommand;
import com.minkuh.prticl.commands.node.DespawnNodeCommand;
import com.minkuh.prticl.commands.node.SpawnNodeCommand;
import com.minkuh.prticl.systemutil.PrticlCommandsUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.minkuh.prticl.common.resources.PrticlConstants.*;

public class PrticlTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && label.equalsIgnoreCase(PrticlCommand.getCommandName())) {
            if (args.length == 1) return getSortedSubcommands(args[0]);

            if (args.length == 2 && isNodeSubcommand(args[0])) {
                return getSortedCommands(args[1]);
            }

            String[] tabCompletionArray = Arrays.copyOfRange(args, 1, args.length);
            return switch (args[1].toLowerCase(Locale.ROOT)) {
                case (SPAWN_COMMAND) ->
                        PrticlCommandsUtil.COMMANDS.get(SpawnNodeCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                case (DESPAWN_COMMAND) ->
                        PrticlCommandsUtil.COMMANDS.get(DespawnNodeCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                case (CREATE_COMMAND) ->
                        PrticlCommandsUtil.COMMANDS.get(CreateNodeCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                case (LIST_COMMAND) ->
                        PrticlCommandsUtil.COMMANDS.get(ListCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                default -> Collections.emptyList();
            };
        }
        return Collections.emptyList();
    }

    private static List<String> getSortedSubcommands(String arg) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, SUBCOMMANDS, completions);

        return completions;
    }

    private List<String> getSortedCommands(String arg) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, COMMAND_NAMES, completions);

        return completions;
    }

    private static final List<String> SUBCOMMANDS = new ArrayList<>() {{
        add(NODE_DEFAULT_NAME);
        add(HELP_COMMAND);
    }};

    private static final List<String> COMMAND_NAMES = new ArrayList<>() {{
        PrticlCommandsUtil.COMMANDS.forEach((cmdName, command) -> COMMAND_NAMES.add(cmdName));
    }};

    private boolean isNodeSubcommand(String arg) {
        return arg.equalsIgnoreCase(NODE_DEFAULT_NAME) || arg.equalsIgnoreCase(NODE_DEFAULT_NAME.substring(0, 1));
    }
}