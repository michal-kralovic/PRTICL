package com.minkuh.prticl.commands;

import com.minkuh.prticl.common.message.MessageColors;
import com.minkuh.prticl.common.message.PrticlMessages;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static com.minkuh.prticl.common.resources.PrticlConstants.PLAYER_COMMAND_ONLY;
import static com.minkuh.prticl.common.resources.PrticlConstants.PRTICL_COMMAND;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextColor.color;


/**
 * An abstract PRTICL command class. <br>
 * Extend me for new commands!
 */
public abstract class PrticlCommand implements IPrticlCommand {
    /**
     * Allows access to PRTICL system messages.
     */
    public PrticlMessages prticlMessage = new PrticlMessages();

    /**
     * A utility method to handle non-Player command triggers.
     *
     * @param sender The current sender of the command
     * @return TRUE if sent by the player.
     */
    public boolean isCommandSentByPlayer(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PLAYER_COMMAND_ONLY);
            return false;
        }
        return true;
    }

    /**
     * Defines tab completion cases for each of the provided arguments. <br/>
     * It also assumes the arguments passed to it are stripped of the main command (prticl) and the main subcommand (e.g. node)
     *
     * @return Tab completion result
     */
    abstract public List<String> getTabCompletion(String[] args);

    /**
     * Defines the implementation of a Prticl command.
     *
     * @param args   The arguments of the Command
     * @param sender The Command sender (usually a Player)
     * @return TRUE if handled.
     */
    abstract public boolean execute(String[] args, CommandSender sender);

    /**
     * Gets the help block of the command. <br/>
     * Used in the help command to display all commands' information, such as: <br/>
     *   - Description<br/>
     *   - Usage<br/>
     *   - Example usage (on click)
     *
     * @return TextComponent containing the command explanation.
     */
    abstract public TextComponent.Builder getHelpDescription();

    public TextComponent.Builder listEntryOfNodeHelp(String nodeSubcommandName, String nodeSubcommandDescription, String exampleSubcommandUse, String exampleCommand) {
        return text()
                .append(text()
                        .content(nodeSubcommandName + " » ")
                        .hoverEvent(HoverEvent.showText(text().content("Click me!")))
                        .clickEvent(ClickEvent.suggestCommand(exampleCommand))
                        .decoration(TextDecoration.BOLD, TextDecoration.State.TRUE)
                        .color(color(MessageColors.system))
                ).append(text().decoration(TextDecoration.BOLD, TextDecoration.State.FALSE).content(nodeSubcommandDescription).color(color(MessageColors.prticlLight)))
                .appendNewline()
                .append(text().content("  " + "Template: ").color(color(MessageColors.system)))
                .append(text().content(exampleSubcommandUse).color(color(MessageColors.prticlLight)));
    }

    public static String getCommandName() {
        return PRTICL_COMMAND;
    }
}