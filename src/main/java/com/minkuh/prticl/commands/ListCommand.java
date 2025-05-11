package com.minkuh.prticl.commands;

import com.minkuh.prticl.commands.base.Command;
import com.minkuh.prticl.common.PaginatedResult;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.data.entities.IPrticlEntity;
import com.minkuh.prticl.data.repositories.NodeRepository;
import com.minkuh.prticl.data.repositories.PlayerRepository;
import com.minkuh.prticl.data.repositories.TriggerRepository;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.logging.Logger;

public class ListCommand extends Command {
    private final NodeRepository nodeRepository;
    private final TriggerRepository triggerRepository;
    private final PlayerRepository playerRepository;
    private final CommandCategory category;

    public ListCommand(Logger logger, CommandCategory category) {
        this.nodeRepository = new NodeRepository(logger);
        this.triggerRepository = new TriggerRepository(logger);
        this.playerRepository = new PlayerRepository(logger);
        this.category = category;
    }

    @Override
    public boolean execute(String[] args, CommandSender sender) {
        int page;
        try {
            if (args.length >= 1) {
                page = Integer.parseInt(args[0]) - 1;
                if (page < 0) page = 0;
            } else {
                page = 0;
            }
        } catch (NumberFormatException ex) {
            sender.sendMessage(PrticlMessages.error("Incorrect page number format!"));
            return true;
        }

        try {
            PaginatedResult<?> result;
            switch (category) {
                case CommandCategory.NODE -> {
                    result = nodeRepository.getByPage(page);
                    displayResults(sender, result, "Nodes");
                }
                case CommandCategory.TRIGGER -> {
                    result = triggerRepository.getByPage(page);
                    displayResults(sender, result, "Triggers");
                }
                case CommandCategory.PLAYER -> {
                    result = playerRepository.getByPage(page);
                    displayResults(sender, result, "Players");
                }
                default -> throw new UnsupportedOperationException("Listing not supported for this category");
            }
        } catch (Exception ex) {
            sender.sendMessage(PrticlMessages.error(ex.getMessage()));
        }
        return true;
    }

    @Override
    public List<String> getTabCompletion(String[] args) {
        if (args.length == 2) {
            return List.of("page");
        }
        return List.of();
    }

    @Override
    public TextComponent.Builder getHelpSection() {
        String entityType = switch (category) {
            case CommandCategory.NODE -> "nodes";
            case CommandCategory.TRIGGER -> "triggers";
            case CommandCategory.PLAYER -> "players";
            default -> "entities";
        };

        return createHelpSectionForCommand(
                getCommandName(),
                "Lists out all available " + entityType + ".",
                "/prticl " + category.toString().toLowerCase() + " list [page]",
                "/prticl " + category.toString().toLowerCase() + " list 1"
        );
    }

    @Override
    public String getCommandName() {
        return PrticlCommands.Names.LIST;
    }

    @Override
    public CommandCategory getCategory() {
        return category;
    }

    private <T extends IPrticlEntity> void displayResults(CommandSender sender, PaginatedResult<T> results, String entityType) {
        int displayPage = results.getPage() + 1;

        var message = new StringBuilder();
        message.append(entityType)
                .append(" (Page ").append(displayPage).append(" of ").append(results.getTotalPages())
                .append(" | ")
                .append(results.getTotalItems()).append(" total)\n");

        if (results.getItems().isEmpty()) {
            message.append("No ").append(entityType.toLowerCase()).append(" found.");
        } else {
            for (T item : results.getItems()) {
                message.append(" - ")
                        .append(item.getListView())
                        .append("\n");
            }
        }

        sender.sendMessage(PrticlMessages.player(message.toString()));
    }
}