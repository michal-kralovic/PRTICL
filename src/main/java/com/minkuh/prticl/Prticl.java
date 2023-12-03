package com.minkuh.prticl;

import com.minkuh.prticl.eventlisteners.RightClickEventListener;
import com.minkuh.prticl.particles.tab_completers.PrticlTabCompleter;
import com.minkuh.prticl.systemutil.CommandsUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * PRTICL 🎉
 */
public final class Prticl extends JavaPlugin {
    private final CommandsUtil cmdUtil = new CommandsUtil(this);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new RightClickEventListener(), this);
        this.getCommand("prticl").setTabCompleter(new PrticlTabCompleter());

        saveResource("config.json", false);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return cmdUtil.commandSwitcher(command, sender, args);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
