package com.minkuh.prticl.data;

import com.minkuh.prticl.Prticl;
import com.zaxxer.hikari.HikariConfig;

import java.io.File;

public class PrticlDatabaseUtil {
    public static HikariConfig config;

    public static void init(Prticl prticl) {
        if (config == null) {
            config = new HikariConfig();

            var pathToDataFolder = prticl.getDataFolder().getAbsolutePath();
            config.setJdbcUrl("jdbc:sqlite:" + pathToDataFolder + File.separator + "database.db");
            config.setDriverClassName("org.sqlite.JDBC");

            config.setMaximumPoolSize(1);
            config.setConnectionTestQuery("SELECT 1");

            config.setAutoCommit(false);
        }

        buildTablesIfNeeded();
    }

    private static void buildTablesIfNeeded() {
        // Create Players table
        new Query("""
                    CREATE TABLE IF NOT EXISTS players (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        uuid VARCHAR(36) NOT NULL UNIQUE,
                        username VARCHAR(16) NOT NULL
                    )
                """).execute();

        // Create Nodes table
        new Query("""
                    CREATE TABLE IF NOT EXISTS nodes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name VARCHAR(64) NOT NULL,
                        repeat_delay INTEGER NOT NULL DEFAULT 0,
                        repeat_count INTEGER NOT NULL DEFAULT 0,
                        particle_density INTEGER NOT NULL DEFAULT 1,
                        particle_type VARCHAR(32) NOT NULL,
                        is_enabled BOOLEAN NOT NULL DEFAULT 0,
                        is_spawned BOOLEAN NOT NULL DEFAULT 0,
                        world_uuid VARCHAR(36) NOT NULL,
                        x DOUBLE NOT NULL,
                        y DOUBLE NOT NULL,
                        z DOUBLE NOT NULL,
                        player_id INTEGER NOT NULL,
                        FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
                    )
                """).execute();

        // Create Triggers table
        new Query("""
                    CREATE TABLE IF NOT EXISTS triggers (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name VARCHAR(64) NOT NULL,
                        x DOUBLE NOT NULL,
                        y DOUBLE NOT NULL,
                        z DOUBLE NOT NULL,
                        block_name VARCHAR(64) NOT NULL,
                        world_uuid VARCHAR(36) NOT NULL,
                        player_id INTEGER NOT NULL,
                        FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
                    )
                """).execute();

        // Create Node-Trigger relationship table (many-to-many)
        new Query("""
                    CREATE TABLE IF NOT EXISTS node_trigger (
                        node_id INTEGER NOT NULL,
                        trigger_id INTEGER NOT NULL,
                        PRIMARY KEY (node_id, trigger_id),
                        FOREIGN KEY (node_id) REFERENCES nodes(id) ON DELETE CASCADE,
                        FOREIGN KEY (trigger_id) REFERENCES triggers(id) ON DELETE CASCADE
                    )
                """).execute();
    }
}