package com.minkuh.prticl.systemutil;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.eventlisteners.RightClickEventListener;
import com.minkuh.prticl.eventlisteners.TerrainStateChangeEventListener;
import com.minkuh.prticl.nodes.commands.PrticlCommand;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.tabcompleters.PrticlTabCompleter;
import com.minkuh.prticl.systemutil.configuration.PrticlNodeConfigUtil;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.sql.SQLException;

import static com.minkuh.prticl.systemutil.resources.PrticlStrings.NODE_CONFIGURATION_SECTION;

public class PrticlPluginMainFunctionsConfigUtil {
    private final Prticl plugin;
    private final PrticlNodeConfigUtil configUtil;

    public PrticlPluginMainFunctionsConfigUtil(Prticl plugin) {
        this.plugin = plugin;
        this.configUtil = new PrticlNodeConfigUtil(plugin);
    }

    public void startPrticl() throws SQLException {
        plugin.getServer().getPluginManager().registerEvents(new RightClickEventListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new TerrainStateChangeEventListener(plugin), plugin);
        plugin.getCommand(PrticlCommand.getCommandName()).setTabCompleter(new PrticlTabCompleter());

        // serialization
        ConfigurationSerialization.registerClass(PrticlNode.class);

        setupDirectoriesIfNonexistent();
        plugin.saveDefaultConfig();

        if (!configUtil.configNodeSectionExists()) {
            plugin.getConfig().createSection(NODE_CONFIGURATION_SECTION);
            plugin.saveConfig();
        }
    }

    private void setupDirectoriesIfNonexistent() {
        if (!plugin.getDataFolder().exists())
            plugin.getDataFolder().mkdirs();
    }
}