package com.minkuh.prticl;

import com.minkuh.prticl.eventlisteners.RightClickEventListener;
import com.minkuh.prticl.nodes.commands.PrticlCommand;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.tabcompleters.PrticlTabCompleter;
import com.minkuh.prticl.systemutil.CommandsUtil;
import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.NODE_CONFIGURATION_SECTION;

/**
 * PRTICL 🎉
 */
public final class Prticl extends JavaPlugin {
    private final CommandsUtil cmdUtil = new CommandsUtil(this);
    private PrticlNodeConfigUtil configUtil = new PrticlNodeConfigUtil(this);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new RightClickEventListener(), this);
        this.getCommand(PrticlCommand.getCommandName()).setTabCompleter(new PrticlTabCompleter());

        ConfigurationSerialization.registerClass(PrticlNode.class);

        saveDefaultConfig();

        getResource("config.yml");

        if (!configUtil.configNodeSectionExists()) {
            getConfig().createSection(NODE_CONFIGURATION_SECTION);
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
