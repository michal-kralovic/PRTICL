package com.minkuh.prticl.nodes.commands;

import com.minkuh.prticl.systemutil.message.MessageColors;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import org.bukkit.command.CommandSender;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.HELP_COMMAND;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

/**
 * Displays help information
 */
public class PrticlHelpCommand extends PrticlCommand {
    @Override
    public boolean command(String[] args, CommandSender sender) {
        if (args.length != 1) {
            sender.sendMessage(listPrticlCategories());
            return true;
        }

        sender.sendMessage(listPrticlNodeSubcommands());
        return true;
    }

    private TextComponent listPrticlCategories() {
        return text()
                .appendNewline().appendNewline()
                .append(text().content("        [").decoration(TextDecoration.BOLD, State.TRUE).color(color(MessageColors.prticlStrong))
                        .append(text().decoration(TextDecoration.BOLD, State.FALSE).content(" PRTICL - Help ").color(color(MessageColors.prticlLight))
                                .append(text().decoration(TextDecoration.BOLD, State.TRUE).content("]").color(color(MessageColors.prticlStrong)))))
                .appendNewline().appendNewline()
                .append(text().content("/prticl help <category>").color(color(MessageColors.system)))
                .appendNewline().appendNewline()
                .append(text().content("  - ")
                        .append(text().content("node / n » ")).color(color(MessageColors.system))
                        .append(text().content("Shows help for all the available node subcommands.").color(color(MessageColors.prticlLight))))
                .build();
    }

    private TextComponent listPrticlNodeSubcommands() {
        return text()
                .appendNewline().appendNewline()
                .append(text().content("        [").decoration(TextDecoration.BOLD, State.TRUE).color(color(MessageColors.prticlStrong))
                        .append(text().decoration(TextDecoration.BOLD, State.FALSE).content(" PRTICL - Help - Node Subcommands ").color(color(MessageColors.prticlLight))
                                .append(text().decoration(TextDecoration.BOLD, State.TRUE).content("]").color(color(MessageColors.prticlStrong)))))
                .appendNewline().appendNewline()
                .append(listEntryOfNodeHelp(
                        PrticlCreateCommand.getCommandName(),
                        "Creates a new prticl node.",
                        "/prticl node create <name> <particle type> <repeat delay> <density> <(x) (y) (z)>"
                )).appendNewline()
                .append(listEntryOfNodeHelp(
                        PrticlListCommand.getCommandName(),
                        "Lists out all the available prticl nodes.",
                        "/prticl node list <page number>"
                )).appendNewline()
                .append(listEntryOfNodeHelp(
                        PrticlSpawnCommand.getCommandName(),
                        "Spawns a prticl node based on a given id/name.",
                        "/prticl node spawn <(id:node id) | (node name)>"
                )).build();
    }

    private TextComponent listEntryOfNodeHelp(String nodeSubcommandName, String nodeSubcommandDescription, String exampleSubcommandUse) {
        return text()
                .append(text().content("  " + nodeSubcommandName + " » ").decoration(TextDecoration.BOLD, State.TRUE).color(color(MessageColors.system)))
                .append(text().decoration(TextDecoration.BOLD, State.FALSE).content(nodeSubcommandDescription).color(color(MessageColors.prticlLight)))
                .appendNewline()
                .append(text().content("Template: ").color(color(MessageColors.system)))
                .append(text().content(exampleSubcommandUse).color(color(MessageColors.prticlLight)))
                .appendNewline()
                .build();
    }

    public static String getCommandName() {
        return HELP_COMMAND;
    }
}
