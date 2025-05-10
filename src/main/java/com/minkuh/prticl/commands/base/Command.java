package com.minkuh.prticl.commands.base;

import com.minkuh.prticl.common.PrticlMessages;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.apache.commons.lang3.Validate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;

public abstract class Command implements ICommand {
    public abstract String getCommandName();

    protected boolean isCommandSentByPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return false;
        }

        return true;
    }

    protected TextComponent.Builder createHelpSectionForCommand(String name, String description, String template, String workingExample) {
        return text()
                .append(text()
                        .content(name + " » ")
                        .hoverEvent(HoverEvent.showText(text().content("Click me!")))
                        .clickEvent(ClickEvent.suggestCommand(workingExample))
                        .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                        .color(color(PrticlMessages.Colors.system))
                ).append(text().decoration(TextDecoration.BOLD, TextDecoration.State.FALSE).content(description).color(color(PrticlMessages.Colors.light)))
                .appendNewline()
                .append(text().content("  " + "Template: ").color(color(PrticlMessages.Colors.system)))
                .append(text().content(template).color(color(PrticlMessages.Colors.light)));
    }

    @Override
    public String getName() {
        // Preventing command names with '-' due to later command filtering
        Validate.isTrue(!getCommandName().contains("-"), "The command name can't contain the '-' character!");

        return getCommandName() + '-' + getCategory().toString();
    }
}