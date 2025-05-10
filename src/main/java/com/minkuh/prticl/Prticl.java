package com.minkuh.prticl;

import com.minkuh.prticl.commands.CommandCategory;
import com.minkuh.prticl.commands.PrticlCommands;
import com.minkuh.prticl.commands.PrticlTabCompleter;
import com.minkuh.prticl.common.PrticlMessages;
import com.minkuh.prticl.common.PrticlUtil;
import com.minkuh.prticl.data.PrticlDatabaseUtil;
import com.minkuh.prticl.data.Query;
import com.minkuh.prticl.events.PlayerInteractEventListener;
import com.minkuh.prticl.events.RightClickEventListener;
import com.minkuh.prticl.events.TerrainStateChangeEventListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * PRTICL 🎉
 */
public final class Prticl extends JavaPlugin {
    private List<String> nodeSubcommandNames;
    private List<String> triggerSubcommandNames;
    private List<String> playerSubcommandNames;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PrticlDatabaseUtil.init(this);
        PrticlMessages.loadConfiguration(this);
        PrticlCommands.init(this);

        getServer().getPluginManager().registerEvents(new RightClickEventListener(), this);
        getServer().getPluginManager().registerEvents(new TerrainStateChangeEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(this), this);
        getCommand("prticl").setTabCompleter(new PrticlTabCompleter(PrticlCommands.getCommands()));

        this.nodeSubcommandNames = PrticlUtil.getSubcommandNames(CommandCategory.NODE);
        this.triggerSubcommandNames = PrticlUtil.getSubcommandNames(CommandCategory.TRIGGER);
        this.playerSubcommandNames = PrticlUtil.getSubcommandNames(CommandCategory.PLAYER);
    }

    @Override
    public void onDisable() {
        getLogger().info("Despawning all nodes");
        new Query("UPDATE nodes SET is_spawned = 0").execute();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("prticl") || args.length == 0) {
            return true;
        }

        var commands = PrticlCommands.getCommands();

        // NOTE: args[0] is the first word after the command name
        if (PrticlUtil.matchesWordOrLetter(args[0], PrticlCommands.Names.HELP)) {
            PrticlUtil.getCommandByCategoryAndName(commands, CommandCategory.OTHER, PrticlCommands.Names.HELP)
                    .execute(skip(args, 1), sender);
        }

        if (args.length == 1)
            return true;


        var commandArgs = skip(args, 2);
        var subcommand = args[1].toLowerCase(Locale.ROOT);

        if (PrticlUtil.matchesWordOrLetter(args[0], PrticlCommands.Names.NODE)) {
            if (nodeSubcommandNames.stream().anyMatch(subcommand::equalsIgnoreCase)) {
                return PrticlUtil.getCommandByCategoryAndName(commands, CommandCategory.NODE, subcommand).execute(commandArgs, sender);
            } else {
                sender.sendMessage(PrticlMessages.error("Unknown node subcommand: " + subcommand));
                return true;
            }
        }

        if (PrticlUtil.matchesWordOrLetter(args[0], PrticlCommands.Names.TRIGGER)) {
            if (triggerSubcommandNames.stream().anyMatch(subcommand::equalsIgnoreCase)) {
                return PrticlUtil.getCommandByCategoryAndName(commands, CommandCategory.TRIGGER, subcommand).execute(commandArgs, sender);
            } else {
                sender.sendMessage(PrticlMessages.error("Unknown trigger subcommand: " + subcommand));
                return true;
            }
        }

        if (PrticlUtil.matchesWordOrLetter(args[0], PrticlCommands.Names.PLAYER)) {
            if (playerSubcommandNames.stream().anyMatch(subcommand::equalsIgnoreCase)) {
                return PrticlUtil.getCommandByCategoryAndName(commands, CommandCategory.PLAYER, subcommand).execute(commandArgs, sender);
            } else {
                sender.sendMessage(PrticlMessages.error("Unknown player subcommand: " + subcommand));
                return true;
            }
        }

        return true;
    }

    private @NotNull String[] skip(@NotNull String[] strings, int count) {
        return Arrays.stream(strings).skip(count).toArray(String[]::new);
    }
}