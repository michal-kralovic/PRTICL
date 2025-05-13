package com.minkuh.prticl.commands;

import com.minkuh.prticl.commands.base.Command;
import com.minkuh.prticl.common.PrticlMessages;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public class HelpCommand extends Command {
    private static Map<CommandCategory, List<TextComponent.Builder>> HELP_SECTIONS = new HashMap<>() {{
        var allCommands = PrticlCommands.getCommands();

        for (var kvp : allCommands.entrySet()) {
            var command = kvp.getValue();

            var sectionsForCategory = get(command.getCategory());
            if (sectionsForCategory == null) {
                put(command.getCategory(), new ArrayList<>());
                sectionsForCategory = get(command.getCategory());
            }

            sectionsForCategory.add(command.getHelpSection());

            put(
                    command.getCategory(),
                    sectionsForCategory
            );
        }
    }};

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        try {
            if (args.length < 1) {
                sender.sendMessage(listPrticlCategories());
                return true;
            }

            CommandCategory category = null;
            try {
                category = Enum.valueOf(CommandCategory.class, args[0].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ex) {
                sender.sendMessage(PrticlMessages.error("Unknown command category: " + args[0]));
                return true;
            }

            if (!HELP_SECTIONS.containsKey(category)) {
                sender.sendMessage(
                        PrticlMessages.error(
                                "Help for command category '"
                                        + category.name().toLowerCase(Locale.ROOT)
                                        + "' does not exist. Available categories are: "
                                        + HELP_SECTIONS
                                        .keySet()
                                        .stream()
                                        .map(enumVal -> enumVal.name().toLowerCase(Locale.ROOT))
                                        .collect(Collectors.joining(", "))
                        )
                );
                return true;
            }

            sender.sendMessage(getSubcommandTextComponentForCategory(category));
        } catch (Exception ex) {
            sender.sendMessage(PrticlMessages.error(ex.getMessage()));
        }
        return true;
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return List.of();
    }

    @Override
    public TextComponent.Builder getHelpSection() {
        return createHelpSectionForCommand(
                getCommandName(),
                "Displays helpful information about Prticl.",
                "/prticl help",
                "/prticl help"
        );
    }

    @Override
    public String getCommandName() {
        return PrticlCommands.Names.HELP;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.OTHER;
    }

    private TextComponent.Builder getHelpTitle(String content) {
        return text()
                .appendNewline()
                .appendNewline()
                .append(text()
                        .content("        [")
                        .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                        .color(color(PrticlMessages.Colors.strong))
                        .append(text()
                                .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)
                                .content(" " + content + " ")
                                .color(color(PrticlMessages.Colors.light))
                                .append(text()
                                        .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                                        .content("]")
                                        .color(color(PrticlMessages.Colors.strong))
                                )
                        )
                );
    }

    private TextComponent getSubcommandTextComponentForCategory(CommandCategory category) {
        var categoryName = category.name().toLowerCase(Locale.ROOT);

        var builder = getHelpTitle("PRTICL - Help - " + (categoryName.charAt(0) + "").toUpperCase(Locale.ROOT) + categoryName.substring(1) + " Subcommands")
                .appendNewline()
                .appendNewline();

        for (var helpSection : HELP_SECTIONS.entrySet()
                .stream()
                .filter(kvp -> kvp.getKey() == category)
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow()) {
            builder.append(helpSection).appendNewline();
        }

        return builder.build();
    }

    private TextComponent listPrticlCategories() {
        var builder = getHelpTitle("PRTICL - Help")
                .appendNewline()
                .appendNewline()
                .append(text("  Usage: ")
                        .color(color(PrticlMessages.Colors.light))
                        .append(text()
                                .content("/prticl help <category>")
                                .color(color(PrticlMessages.Colors.system))
                        )
                )
                .appendNewline()
                .appendNewline();

        for (var commandCategory : HELP_SECTIONS.keySet()) {
            var categoryName = commandCategory.name().toLowerCase(Locale.ROOT);

            builder.append(text()
                            .content("  - " + categoryName + " » ")
                            .color(color(PrticlMessages.Colors.system))
                            .append(text()
                                    .content("Shows help for all the available " + categoryName + " subcommands.")
                                    .color(color(PrticlMessages.Colors.light))
                            )
                    )
                    .appendNewline();
        }

        return builder.build();
    }
}