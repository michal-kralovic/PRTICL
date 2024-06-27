package com.minkuh.prticl;

import com.minkuh.prticl.systemutil.PrticlCommandsUtil;
import com.minkuh.prticl.systemutil.PrticlPluginMainFunctionsConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

/**
 * PRTICL 🎉
 */
public final class Prticl extends JavaPlugin {
    private final PrticlCommandsUtil cmdUtil = new PrticlCommandsUtil(this);
    private final PrticlPluginMainFunctionsConfigUtil pluginOnEnableConfig = new PrticlPluginMainFunctionsConfigUtil(this);

    public Prticl() throws SQLException {
    }

    @Override
    public void onEnable() {
        try {
            pluginOnEnableConfig.startPrticl();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return cmdUtil.commandExecutor(command, sender, args);
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}