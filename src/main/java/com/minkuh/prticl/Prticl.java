package com.minkuh.prticl;

import com.minkuh.prticl.data.wrappers.PrticlDataSource;
import com.minkuh.prticl.event_listeners.RightClickEventListener;
import com.minkuh.prticl.event_listeners.TerrainStateChangeEventListener;
import com.minkuh.prticl.nodes.commands.PrticlCommand;
import com.minkuh.prticl.nodes.prticl.PrticlNode;
import com.minkuh.prticl.nodes.tab_completers.PrticlTabCompleter;
import com.minkuh.prticl.systemutil.PrticlCommandsUtil;
import com.minkuh.prticl.systemutil.configuration.PrticlConfigurationUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.logging.Level;

/**
 * PRTICL 🎉
 */
public final class Prticl extends JavaPlugin {
    private final PrticlCommandsUtil cmdUtil = new PrticlCommandsUtil(this);

    public Prticl() throws SQLException {
    }

    @Override
    public void onEnable() {
        setupDatabase();

        // serialization
        ConfigurationSerialization.registerClass(PrticlNode.class);

        getServer().getPluginManager().registerEvents(new RightClickEventListener(), this);
        getServer().getPluginManager().registerEvents(new TerrainStateChangeEventListener(this), this);
        getCommand(PrticlCommand.getCommandName()).setTabCompleter(new PrticlTabCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return cmdUtil.commandExecutor(command, sender, args);
    }

    private void setupDatabase() {
        PrticlDataSource dataSource = new PrticlConfigurationUtil(this).getDataSource();

        Flyway flyway = Flyway.configure(getClass().getClassLoader())
                .validateMigrationNaming(true)
                .defaultSchema("prticl")
                .dataSource(dataSource.url(), dataSource.user(), dataSource.password())
                .load();

        try {
            flyway.migrate();
        } catch (FlywayException ex) {
            getLogger().log(Level.SEVERE, "Couldn't run migrations! Reason: "
                    + ex.getMessage()
                    + "\nPlease check the data source configuration inside of the plugin's config.yml");
        }
    }
}