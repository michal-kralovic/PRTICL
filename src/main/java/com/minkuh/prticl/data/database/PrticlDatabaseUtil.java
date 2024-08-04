package com.minkuh.prticl.data.database;

import com.minkuh.prticl.Prticl;
import com.minkuh.prticl.common.wrappers.PrticlDataSource;
import com.minkuh.prticl.data.entities.Node;
import com.minkuh.prticl.data.entities.Player;
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

import java.util.Properties;

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
        var pluginConfig = plugin.getConfig();
        var useMySQL = pluginConfig.getConfigurationSection("database").getBoolean("use-mysql");
        var dataSource = PrticlDataSource.getFromConfig(pluginConfig);

        var config = new Configuration();
        var props = new Properties();

        props.put(Environment.JAKARTA_JDBC_URL, dataSource.url(useMySQL));
        props.put(Environment.JAKARTA_JDBC_USER, dataSource.user());
        props.put(Environment.JAKARTA_JDBC_PASSWORD, dataSource.password());
        props.put(Environment.JAKARTA_JDBC_DRIVER, useMySQL ? "com.mysql.cj.jdbc.Driver" : "org.postgresql.Driver");
        props.put(Environment.HBM2DDL_AUTO, "update");
        props.put(Environment.CONNECTION_PROVIDER, HikariCPConnectionProvider.class.getName());
        props.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        props.put(Environment.SHOW_SQL, "true");

        config.setProperties(props);
        config.addAnnotatedClass(Player.class);
        config.addAnnotatedClass(Node.class);

        var serviceRegistry = new StandardServiceRegistryBuilder().applySettings(config.getProperties()).build();
        sessionFactory = config.buildSessionFactory(serviceRegistry);
    }
}