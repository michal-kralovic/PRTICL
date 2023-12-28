package com.minkuh.prticl;

import com.minkuh.prticl.eventlisteners.RightClickEventListener;
import com.minkuh.prticl.particles.prticl.PrticlNode;
import com.minkuh.prticl.particles.tabcompleters.PrticlTabCompleter;
import com.minkuh.prticl.systemutil.CommandsUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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

        ConfigurationSerialization.registerClass(PrticlNode.class);

        saveDefaultConfig();

        getResource("config.yml");

        if (getConfig().getConfigurationSection("particles") == null) {
            getConfig().createSection("particles");
            saveConfig();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return cmdUtil.commandSwitcher(command, sender, args);
    }

    @Override
    public void onDisable() {
        saveConfig();
    }
}
