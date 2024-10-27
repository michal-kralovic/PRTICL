package com.minkuh.prticl.data.database;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.PrticlDataSource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Properties;

import static com.minkuh.prticl.data.database.PrticlDatabase.PRTICL_DATABASE_ENTITIES;

public class PrticlDatabaseUtil {

    @PersistenceContext
    @PersistenceUnit(name = "PrticlPersistenceUnit")
    private static SessionFactory sessionFactory;

    public static EntityManager getEntityManager() {
        return sessionFactory.createEntityManager();
    }

    public static Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public static void init(Prticl plugin) {
        getSessionFactory(plugin);
    }

    public static EntityManagerFactory getSessionFactory(Prticl plugin) {
        if (sessionFactory == null) {
            configureSessionFactory(plugin);
        }

        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null && sessionFactory.isOpen()) {
            sessionFactory.close();
        }
    }

    private static void configureSessionFactory(Prticl plugin) {
        var useMySQL = false;
        var pluginConfig = plugin.getConfig();
        var databaseSection = pluginConfig.getConfigurationSection("database");

        if (databaseSection != null) {
            useMySQL = databaseSection.getBoolean("use-mysql");
        }

        var dataSourceOpt = PrticlDataSource.getFromConfig(pluginConfig);
        if (dataSourceOpt.isEmpty()) {
            plugin.getLogger().severe("Couldn't obtain the database configuration!");
            throw new RuntimeException("Couldn't obtain the database configuration!");
        }

        var config = getConfig(dataSourceOpt.get(), useMySQL);

        for (var clazz : PRTICL_DATABASE_ENTITIES) {
            config.addAnnotatedClass(clazz.getClass());
        }

        var serviceRegistry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
        sessionFactory = config.buildSessionFactory(serviceRegistry);
    }

    private static @NotNull Configuration getConfig(PrticlDataSource dataSource, boolean useMySQL) {
        var config = new Configuration();
        var props = new Properties();

        props.put(Environment.JAKARTA_JDBC_URL, dataSource.url(useMySQL));
        props.put(Environment.JAKARTA_JDBC_USER, dataSource.user());
        props.put(Environment.JAKARTA_JDBC_PASSWORD, dataSource.password());
        props.put(Environment.JAKARTA_JDBC_DRIVER, useMySQL ? "com.mysql.cj.jdbc.Driver" : "org.postgresql.Driver");
        props.put(Environment.HBM2DDL_AUTO, "create-drop");
        props.put(Environment.CONNECTION_PROVIDER, HikariCPConnectionProvider.class.getName());
        props.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        props.put(Environment.SHOW_SQL, "true");
        props.put(Environment.DEFAULT_SCHEMA, "prticl");

        config.setProperties(props);
        return config;
    }
}