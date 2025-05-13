package com.minkuh.prticl.commands;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.commands.base.ICommand;

import java.util.HashMap;
import java.util.Map;

public class PrticlCommands {
    private static Map<String, ICommand> COMMANDS;

    public static void init(Prticl prticl) {
        if (COMMANDS == null) COMMANDS = new HashMap<>();

        // Register standard commands
        addCommand(new CreateNodeCommand(prticl.getLogger()));
        addCommand(new CreateTriggerCommand(prticl.getLogger()));
        addCommand(new SpawnNodeCommand(prticl));
        addCommand(new DespawnNodeCommand(prticl));
        addCommand(new LinkNodeToTriggerCommand(prticl.getLogger()));
        addCommand(new UnlinkNodeToTriggerCommand(prticl.getLogger()));
        addCommand(new EditNodePropertyCommand(prticl));

        // Register list commands for each entity type
        addCommand(new ListCommand(prticl.getLogger(), CommandCategory.NODE));
        addCommand(new ListCommand(prticl.getLogger(), CommandCategory.TRIGGER));
        addCommand(new ListCommand(prticl.getLogger(), CommandCategory.PLAYER));

        // Has to go last for static context
        addCommand(new HelpCommand());
    }

    public static Map<String, ICommand> getCommands() {
        return COMMANDS;
    }

    private static void addCommand(ICommand command) {
        COMMANDS.put(command.getName(), command);
    }

    public static class Names {
        public static final String HELP = "help";

        public static final String NODE = "node";

        public static final String SPAWN = "spawn";
        public static final String DESPAWN = "despawn";
        public static final String EDIT = "edit";


        public static final String TRIGGER = "trigger";

        public static final String LINK = "link";
        public static final String UNLINK = "unlink";

        public static final String CREATE = "create";

        public static final String PLAYER = "player";

        public static final String LIST = "list";
    }
}