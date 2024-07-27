package com.minkuh.prticl.commands;

import com.minkuh.prticl.common.message.MessageColors;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.command.CommandSender;

import java.util.List;

import static com.minkuh.prticl.common.resources.PrticlConstants.HELP_COMMAND;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

/**
 * Displays help information
 */
public class PrticlHelpCommand extends PrticlCommand {
    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (args.length != 1) {
            sender.sendMessage(listPrticlCategories);
            return true;
        }

        sender.sendMessage(listPrticlNodeSubcommands);
        return true;
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return List.of();
    }

    private final TextComponent listPrticlCategories = helpMenuTitle("PRTICL - Help")
            .appendNewline().appendNewline()
            .append(text("  Usage: ").color(color(MessageColors.prticlLight)).append(text().content("/prticl help <category>").color(color(MessageColors.system))))
            .appendNewline().appendNewline()
            .append(text().content("  - ")
                    .append(text().content("node » ")).color(color(MessageColors.system))
                    .append(text().content("Shows help for all the available node subcommands.").color(color(MessageColors.prticlLight))))
            .build();

    private final TextComponent listPrticlNodeSubcommands = helpMenuTitle("PRTICL - Help - Node Subcommands")
            .appendNewline().appendNewline()

            .append(listEntryOfNodeHelp(
                    PrticlCreateCommand.getCommandName(),
                    "Creates a new prticl node.",
                    "/prticl node create <name> <particle type> <repeat delay> <density> <(x) (y) (z)>",
                    "/prticl node create my_node minecraft:cloud 5 5 ~ ~ ~"
            )).appendNewline()

            .append(listEntryOfNodeHelp(
                    PrticlListCommand.getCommandName(),
                    "Lists out all the available prticl nodes.",
                    "/prticl node list <page number>",
                    "/prticl node list 1"
            )).appendNewline()

            .append(listEntryOfNodeHelp(
                    PrticlSpawnCommand.getCommandName(),
                    "Spawns a prticl node based on a given id/name.",
                    "/prticl node spawn <(id:node id) | (node name)>",
                    "/prticl node spawn id:1"
            )).build();

    private TextComponent.Builder listEntryOfNodeHelp(String nodeSubcommandName, String nodeSubcommandDescription, String exampleSubcommandUse, String exampleCommand) {
        return text()
                .append(text().content("  " + nodeSubcommandName + " » ").clickEvent(ClickEvent.suggestCommand(exampleCommand)).decoration(TextDecoration.BOLD, State.TRUE).color(color(MessageColors.system)))
                .append(text().decoration(TextDecoration.BOLD, State.FALSE).content(nodeSubcommandDescription).color(color(MessageColors.prticlLight)))
                .appendNewline()
                .append(text().content("Template: ").color(color(MessageColors.system)))
                .append(text().content(exampleSubcommandUse).color(color(MessageColors.prticlLight)))
                .appendNewline();
    }

    /**
     * Returns the [ TEXT ] title part of the help menu message.
     *
     * @param content The TEXT part of the returned message.
     */
    private TextComponent.Builder helpMenuTitle(String content) {
        return text().appendNewline().appendNewline()
                .append(text().content("        [").decoration(TextDecoration.BOLD, State.TRUE).color(color(MessageColors.prticlStrong))
                        .append(text().decoration(TextDecoration.BOLD, State.FALSE).content(" " + content + " ").color(color(MessageColors.prticlLight))
                                .append(text().decoration(TextDecoration.BOLD, State.TRUE).content("]").color(color(MessageColors.prticlStrong)))));
    }

    public static String getCommandName() {
        return HELP_COMMAND;
    }
}