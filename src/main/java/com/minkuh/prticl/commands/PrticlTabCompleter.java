package com.minkuh.prticl.commands;

import com.minkuh.prticl.commands.node.CreateNodeCommand;
import com.minkuh.prticl.commands.node.DespawnNodeCommand;
import com.minkuh.prticl.commands.node.SpawnNodeCommand;
import com.minkuh.prticl.commands.trigger.AddNodeTriggerCommand;
import com.minkuh.prticl.commands.trigger.CreateTriggerCommand;
import com.minkuh.prticl.common.systemutil.PrticlCommandsUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.minkuh.prticl.common.PrticlConstants.*;

public class PrticlTabCompleter implements TabCompleter {
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && label.equalsIgnoreCase(PrticlCommand.getCommandName())) {
            if (args.length == 1) return getSortedStrings(args[0], SUBCOMMANDS);

            if (args.length == 2) {
                if (isNodeSubcommand(args[0]))
                    return getSortedStrings(args[1], NODE_COMMAND_NAMES);

                if (isTriggerSubcommand(args[0]))
                    return getSortedStrings(args[1], TRIGGER_COMMAND_NAMES);
            }

            String[] tabCompletionArray = Arrays.copyOfRange(args, 1, args.length);
            if (isNodeSubcommand(args[0])) {
                return switch (args[1].toLowerCase(Locale.ROOT)) {
                    case SPAWN_COMMAND ->
                            PrticlCommandsUtil.COMMANDS.get(SpawnNodeCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                    case DESPAWN_COMMAND ->
                            PrticlCommandsUtil.COMMANDS.get(DespawnNodeCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                    case CREATE_COMMAND ->
                            PrticlCommandsUtil.COMMANDS.get(CreateNodeCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                    case LIST_COMMAND ->
                            PrticlCommandsUtil.COMMANDS.get(ListCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                    default -> Collections.emptyList();
                };
            }

            if (isTriggerSubcommand(args[0])) {
                return switch (args[1].toLowerCase(Locale.ROOT)) {
                    case CREATE_COMMAND ->
                            PrticlCommandsUtil.COMMANDS.get(CreateTriggerCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                    case NODE_ADD_COMMAND ->
                            PrticlCommandsUtil.COMMANDS.get(AddNodeTriggerCommand.getCommandName()).getTabCompletion(tabCompletionArray);
                    default -> Collections.emptyList();
                };
            }
        }
        return Collections.emptyList();
    }

    private static List<String> getSortedStrings(String arg, List<String> list) {
        List<String> completions = new ArrayList<>();
        StringUtil.copyPartialMatches(arg, list, completions);

        return completions;
    }

    private static final List<String> SUBCOMMANDS = new ArrayList<>() {{
        add(NODE_DEFAULT_NAME);
        add(TRIGGER);
        add(HELP_COMMAND);
    }};

    private static final List<String> NODE_COMMAND_NAMES = new ArrayList<>() {{
        add(HelpCommand.getCommandName());
        add(CreateNodeCommand.getCommandName());
        add(SpawnNodeCommand.getCommandName());
        add(DespawnNodeCommand.getCommandName());
        add(ListCommand.getCommandName());
    }};

    private static final List<String> TRIGGER_COMMAND_NAMES = new ArrayList<>() {{
        add(CreateTriggerCommand.getCommandName());
        add(AddNodeTriggerCommand.getCommandName());
    }};

    private boolean isNodeSubcommand(String arg) {
        return arg.equalsIgnoreCase(NODE_DEFAULT_NAME) || arg.equalsIgnoreCase(NODE_DEFAULT_NAME.substring(0, 1));
    }

    private boolean isTriggerSubcommand(String arg) {
        return arg.equalsIgnoreCase(TRIGGER) || arg.equalsIgnoreCase(TRIGGER.substring(0, 1));
    }
}