package com.minkuh.prticl.commands;

import com.minkuh.prticl.commands.base.Command;
import com.minkuh.prticl.common.PrticlMessages;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;

import java.util.List;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

// TODO: Rewrite into dynamic sections for nodes, triggers, players, and so on. Currently only does node.
public class HelpCommand extends Command {
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
    public boolean execute(String[] args, CommandSender sender) {
        if (args.length != 1) {
            sender.sendMessage(listPrticlCategories);
            return true;
        }

        sender.sendMessage(listPrticlNodeSubcommands());
        return true;
    }

    @Override
    public String getCommandName() {
        return PrticlCommands.Names.HELP;
    }

    @Override
    public CommandCategory getCategory() {
        return CommandCategory.OTHER;
    }

    private TextComponent.Builder helpMenuTitle(String content) {
        return text().appendNewline().appendNewline()
                .append(text().content("        [").decoration(TextDecoration.BOLD, TextDecoration.State.TRUE).color(color(PrticlMessages.Colors.strong))
                        .append(text().decoration(TextDecoration.BOLD, TextDecoration.State.FALSE).content(" " + content + " ").color(color(PrticlMessages.Colors.light))
                                .append(text().decoration(TextDecoration.BOLD, TextDecoration.State.TRUE).content("]").color(color(PrticlMessages.Colors.strong)))));
    }

    private TextComponent listPrticlNodeSubcommands() {
        var output = helpMenuTitle("PRTICL - Help - Node Subcommands")
                .appendNewline().appendNewline();

        for (var command : PrticlCommands.getCommands().entrySet()) {
            output.append(command.getValue().getHelpSection());
            output.appendNewline();
        }

        return output.build();
    }

    private final TextComponent listPrticlCategories = helpMenuTitle("PRTICL - Help")
            .appendNewline().appendNewline()
            .append(text("  Usage: ").color(color(PrticlMessages.Colors.light)).append(text().content("/prticl help <category>").color(color(PrticlMessages.Colors.system))))
            .appendNewline().appendNewline()
            .append(text().content("  - ")
                    .append(text().content("node » ")).color(color(PrticlMessages.Colors.system))
                    .append(text().content("Shows help for all the available node subcommands.").color(color(PrticlMessages.Colors.light))))
            .build();
}