package com.minkuh.prticl;

import com.minkuh.prticl.commands.PrticlCommand;
import com.minkuh.prticl.commands.PrticlTabCompleter;
import com.minkuh.prticl.commands.PrticlCommandsUtil;
import com.minkuh.prticl.data.database.PrticlDatabaseUtil;
import com.minkuh.prticl.event_listeners.PlayerInteractEventListener;
import com.minkuh.prticl.event_listeners.RightClickEventListener;
import com.minkuh.prticl.event_listeners.TerrainStateChangeEventListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * PRTICL 🎉
 */
public final class Prticl extends JavaPlugin {
    private final PrticlCommandsUtil cmdUtil = new PrticlCommandsUtil(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PrticlDatabaseUtil.init(this);

        getServer().getPluginManager().registerEvents(new RightClickEventListener(), this);
        getServer().getPluginManager().registerEvents(new TerrainStateChangeEventListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractEventListener(this), this);
        getCommand(PrticlCommand.getCommandName()).setTabCompleter(new PrticlTabCompleter());
    }

    @Override
    public void onDisable() {
        PrticlDatabaseUtil.shutdown();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return cmdUtil.commandExecutor(command, sender, args);
    }
}