package com.minkuh.prticl.data;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Query {
    private static final Logger logger = Logger.getLogger(Query.class.getName());
    private static final HikariDataSource dataSource = new HikariDataSource(PrticlDatabaseUtil.config);

    private final String query;
    private final Map<Integer, Object> parameters = new HashMap<>();
    int index = 1;

    /**
     * Fluently starts building a query.
     *
     * @param query SQL Query to be executed eventually.
     */
    public Query(String query) {
        this.query = query;
    }

    /**
     * Sets the parameters for the query with automatic one-based ordering.
     *
     * @param value What to set the value to
     * @return This query (for chaining)
     */
    public <T> Query withParam(T value) {
        if (!this.parameters.containsKey(index))
            this.parameters.put(index, value);
        else throw new IllegalArgumentException("The index '" + index + "' is already in query params.");
        index++;

        return this;
    }

    /**
     * Sets the parameters for the query.
     *
     * @param index Which parameter to target
     * @param value What to set the value to
     * @return This query (for chaining)
     */
    public <T> Query withParam(int index, T value) {
        if (!this.parameters.containsKey(index))
            this.parameters.put(index, value);
        else throw new IllegalArgumentException("The index '" + index + "' is already in query params.");

        return this;
    }

    /**
     * Executes your query and potentially returns it in a list form.
     *
     * @param mapper For mapping your resulting object from a ResultSet.
     * @return An Optional<List> of the specified objects.
     */
    public <T> Optional<List<T>> toList(Function<ResultSet, T> mapper) {
        try (var conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(this.query)) {
                for (var kvp : parameters.entrySet()) {
                    switch (kvp.getValue()) {
                        case Integer val -> stmt.setInt(kvp.getKey(), val);
                        case String val -> stmt.setString(kvp.getKey(), val);
                        case Double val -> stmt.setDouble(kvp.getKey(), val);
                        case Float val -> stmt.setFloat(kvp.getKey(), val);
                        case Boolean val -> stmt.setBoolean(kvp.getKey(), val);
                        default -> throw new IllegalStateException("Unexpected value: " + kvp.getValue());
                    }
                }

                var rs = stmt.executeQuery();
                List<T> results = new ArrayList<>();

                while (rs.next()) {
                    results.add(mapper.apply(rs));
                }

                if (!conn.getAutoCommit()) {
                    conn.commit();
                }

                return Optional.of(results);
            }
        } catch (SQLException ex) {
            logSqlError(ex);
            return Optional.empty();
        }
    }

    /**
     * Executes your query and potentially returns it in a list form.
     *
     * @return An Optional<List> of the specified objects.
     */
    public Optional<List<Map<String, Object>>> toList() {
        return toList(rs -> {
            try {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                Map<String, Object> row = new HashMap<>();

                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }

                return row;
            } catch (SQLException ex) {
                throw new RuntimeException("Error mapping result set to map", ex);
            }
        });
    }

    /**
     * Executes your query and potentially returns its single return object.
     *
     * @return Optional with your object.
     */
    public <T> Optional<T> toSingle(Function<ResultSet, T> mapper) {
        try (var conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(this.query)) {
                for (var kvp : parameters.entrySet()) {
                    switch (kvp.getValue()) {
                        case Integer val -> stmt.setInt(kvp.getKey(), val);
                        case String val -> stmt.setString(kvp.getKey(), val);
                        case Double val -> stmt.setDouble(kvp.getKey(), val);
                        case Float val -> stmt.setFloat(kvp.getKey(), val);
                        case Boolean val -> stmt.setBoolean(kvp.getKey(), val);
                        default -> throw new IllegalStateException("Unexpected value: " + kvp.getValue());
                    }
                }

                var rs = stmt.executeQuery();
                if (rs.next()) {
                    T result = mapper.apply(rs);

                    if (!conn.getAutoCommit()) {
                        conn.commit();
                    }

                    return Optional.ofNullable(result);
                }

                if (!conn.getAutoCommit()) {
                    conn.commit();
                }

                return Optional.empty();
            }
        } catch (SQLException ex) {
            logSqlError(ex);
            return Optional.empty();
        }
    }

    /**
     * Executes your query.
     *
     * @return the amount of changed rows.
     */
    public int execute() {
        try (var conn = dataSource.getConnection()) {
            try (var stmt = conn.prepareStatement(this.query)) {
                for (var kvp : parameters.entrySet()) {
                    switch (kvp.getValue()) {
                        case Integer val -> stmt.setInt(kvp.getKey(), val);
                        case String val -> stmt.setString(kvp.getKey(), val);
                        case Double val -> stmt.setDouble(kvp.getKey(), val);
                        case Float val -> stmt.setFloat(kvp.getKey(), val);
                        case Boolean val -> stmt.setBoolean(kvp.getKey(), val);
                        default -> throw new IllegalStateException("Unexpected value: " + kvp.getValue());
                    }
                }

                int result = stmt.executeUpdate();

                if (!conn.getAutoCommit()) {
                    conn.commit();
                }

                return result;
            }
        } catch (SQLException ex) {
            logSqlError(ex);
            return 0;
        }
    }

    private void logSqlError(SQLException ex) {
        logger.log(Level.SEVERE, "An SQL error occurred!", ex);
    }
}