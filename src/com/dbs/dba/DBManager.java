package com.dbs.dba;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Manages the JDBC database connection, SQL queries, DDL/DML updates,
 * and transaction boundaries.
 */
public class DBManager {

    private static DBManager instance;
    private Connection connection;

    private String url;
    private String user;
    private String password;

    private DBManager() {
        // Default MySQL connection settings
        this.url = "jdbc:mysql://localhost:3306/dbs_proj1?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        this.user = "root";
        this.password = "";
    }

    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    /**
     * Connects to the database using the specified parameters.
     */
    public void connect(String url, String user, String password) throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }

        this.url = url;
        this.user = user;
        this.password = password;

        // Load MySQL JDBC driver class explicitly (works across all Java runtime versions)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Driver may also be loaded via modern ServiceLoader
        }

        this.connection = DriverManager.getConnection(this.url, this.user, this.password);
        System.out.println("[SUCCESS] Successfully connected to database: " + connection.getCatalog());
    }

    /**
     * Reconnects using current credentials or establishes default connection.
     */
    public void connect() throws SQLException {
        connect(this.url, this.user, this.password);
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        if (!isConnected()) {
            connect();
        }
        return connection;
    }

    /**
     * Executes a SELECT query and formats the result with TableFormatter.
     */
    public void executeQuery(String sql) {
        if (!isConnected()) {
            System.err.println("[ERROR] No active database connection. Please connect first.");
            return;
        }

        long start = System.currentTimeMillis();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            TableFormatter.printResultSet(rs);
            long elapsed = System.currentTimeMillis() - start;
            System.out.printf("Execution time: %d ms%n", elapsed);

        } catch (SQLException e) {
            System.err.printf("[SQL ERROR %d] %s%n", e.getErrorCode(), e.getMessage());
        }
    }

    /**
     * Executes an INSERT, UPDATE, DELETE, or DDL statement.
     */
    public int executeUpdate(String sql) throws SQLException {
        if (!isConnected()) {
            throw new SQLException("No active database connection.");
        }

        long start = System.currentTimeMillis();
        try (Statement stmt = connection.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            long elapsed = System.currentTimeMillis() - start;
            System.out.printf("[SUCCESS] Query executed successfully. Rows affected: %d (%d ms)%n", rows, elapsed);
            return rows;
        }
    }

    /**
     * Executes multiple statements inside an explicit atomic transaction.
     */
    public boolean executeTransaction(List<String> statements) {
        if (!isConnected()) {
            System.err.println("[ERROR] No active database connection.");
            return false;
        }

        if (statements == null || statements.isEmpty()) {
            System.out.println("[WARN] No statements provided for transaction.");
            return false;
        }

        boolean originalAutoCommit = true;
        try {
            originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false); // Begin transaction

            System.out.println("[TRANSACTION] Began transaction. Executing " + statements.size() + " statement(s)...");

            try (Statement stmt = connection.createStatement()) {
                int step = 1;
                for (String sql : statements) {
                    System.out.printf("  Step %d: %s%n", step++, sql);
                    stmt.executeUpdate(sql);
                }
            }

            connection.commit(); // Commit transaction
            System.out.println("[TRANSACTION] Transaction committed successfully!");
            return true;

        } catch (SQLException e) {
            System.err.printf("[TRANSACTION FAILED] %s%n", e.getMessage());
            try {
                connection.rollback();
                System.out.println("[TRANSACTION] Rollback performed. No changes were applied.");
            } catch (SQLException ex) {
                System.err.println("[ERROR] Failed to rollback: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(originalAutoCommit);
            } catch (SQLException ignored) {
            }
        }
    }

    /**
     * Executes a multi-line SQL script file.
     */
    public void executeSqlScript(String filePath) throws IOException, SQLException {
        if (!isConnected()) {
            throw new SQLException("No active database connection.");
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--") || line.startsWith("#") || line.isEmpty()) {
                    continue;
                }
                sb.append(line).append(" ");
            }
        }

        String[] statements = sb.toString().split(";");
        int count = 0;
        try (Statement stmt = connection.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                    count++;
                }
            }
        }
        System.out.printf("[SUCCESS] Executed %d SQL statement(s) from '%s'%n", count, filePath);
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[INFO] Database connection closed.");
            } catch (SQLException e) {
                System.err.println("[WARN] Error closing connection: " + e.getMessage());
            }
        }
    }
}
