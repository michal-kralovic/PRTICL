package com.minkuh.prticl.data.database.entity_util;

import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.data.database.PrticlDatabase;
import org.bukkit.command.CommandSender;

import java.util.Locale;

import static com.minkuh.prticl.common.PrticlConstants.*;

public class EntityValidation {
    private static final PrticlDatabase prticlDb = new PrticlDatabase();
    static PrticlMessages messages = new PrticlMessages();

    public static boolean isNodeNameValid(String arg, CommandSender sender) {
        boolean validationResult;

        validationResult = isEntityNameValid(arg, sender);

        if (!prticlDb.getNodeFunctions().isNodeNameUnique(arg)) {
            sender.sendMessage(messages.error(DUPLICATE_ENTITY_NAME));
            validationResult = false;
        }

        return validationResult;
    }

    public static boolean isTriggerNameValid(String arg, CommandSender sender) {
        boolean validationResult;

        validationResult = isEntityNameValid(arg, sender);

        if (!prticlDb.getTriggerFunctions().isTriggerNameUnique(arg)) {
            sender.sendMessage(messages.error(DUPLICATE_ENTITY_NAME));
            validationResult = false;
        }

        return validationResult;
    }

    private static boolean isEntityNameValid(String arg, CommandSender sender) {
        if (arg.toLowerCase(Locale.ROOT).startsWith("id:".toLowerCase(Locale.ROOT))) {
            sender.sendMessage(messages.error(ENTITY_NAME_UNAVAILABLE));
            return false;
        }

        if (arg.length() > 50) {
            sender.sendMessage(messages.error(ENTITY_NAME_TOO_LONG));
            return false;
        }

        if (arg.isBlank()) {
            sender.sendMessage(messages.error(EMPTY_ENTITY_NAME));
            return false;
        }

        return true;
    }
}