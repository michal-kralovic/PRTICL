package com.minkuh.prticl.data.repositories.base;

import com.minkuh.prticl.data.Query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repository implements IRepository {
    protected final Logger logger;

    public Repository(Logger logger) {
        this.logger = logger;
    }

    protected int getTotalCount(String tableName) {
        return new Query("SELECT COUNT(*) as count FROM " + tableName)
                .toSingle(catchyMapper(rs -> rs.getInt("count")))
                .orElse(0);
    }

    protected void logSqlError(SQLException ex) {
        logger.log(Level.SEVERE, "An SQL error occurred!", ex);
    }

    protected <T> Function<ResultSet, T> catchyMapper(ThrowingFunction<ResultSet, T> mapper) {
        return rs -> {
            try {
                return mapper.apply(rs);
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "An SQL error occurred!", ex);
                return null;
            }
        };
    }

    @FunctionalInterface
    protected interface ThrowingFunction<T, R> {
        R apply(T t) throws SQLException;
    }
}