package com.minkuh.prticl.commands;

import com.minkuh.prticl.common.message.MessageColors;
import com.minkuh.prticl.systemutil.PrticlCommandsUtil;
import net.kyori.adventure.text.TextComponent;
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
public class HelpCommand extends PrticlCommand {
    @Override
    public boolean execute(String[] args, CommandSender sender) {
        if (args.length != 1) {
            sender.sendMessage(listPrticlCategories);
            return true;
        }

        sender.sendMessage(listPrticlNodeSubcommands());
        return true;
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        return List.of();
    }

    @Override
    public TextComponent.Builder getHelpDescription() {
        return listEntryOfNodeHelp(
                HelpCommand.getCommandName(),
                "Displays helpful information about Prticl.",
                "/prticl help",
                "/prticl help"
        );
    }

    private final TextComponent listPrticlCategories = helpMenuTitle("PRTICL - Help")
            .appendNewline().appendNewline()
            .append(text("  Usage: ").color(color(MessageColors.prticlLight)).append(text().content("/prticl help <category>").color(color(MessageColors.system))))
            .appendNewline().appendNewline()
            .append(text().content("  - ")
                    .append(text().content("node » ")).color(color(MessageColors.system))
                    .append(text().content("Shows help for all the available node subcommands.").color(color(MessageColors.prticlLight))))
            .build();

    private TextComponent listPrticlNodeSubcommands() {
        var output = helpMenuTitle("PRTICL - Help - Node Subcommands")
                .appendNewline().appendNewline();

        for (var command : PrticlCommandsUtil.COMMANDS.entrySet()) {
            output.append(command.getValue().getHelpDescription());
            output.appendNewline();
        }

        return output.build();
    }

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