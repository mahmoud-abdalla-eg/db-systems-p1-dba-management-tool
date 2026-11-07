package com.dbs.dba;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main application for Project 1: Database Systems DBA Management Tool.
 * Provides an interactive console interface for database administrators
 * to inspect schema metadata, run queries, perform updates, and manage transactions.
 */
public class DBAToolApp {

    private final DBManager dbManager;
    private final Scanner scanner;

    public DBAToolApp() {
        this.dbManager = DBManager.getInstance();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();
        promptInitialConnection();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option (0-9): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> handleConnect();
                case "2" -> handleShowDatabaseInfo();
                case "3" -> handleListTables();
                case "4" -> handleDescribeTable();
                case "5" -> handleExecuteQuery();
                case "6" -> handleExecuteUpdate();
                case "7" -> handleTransactionSession();
                case "8" -> handleExecuteScript();
                case "9" -> handleRunQuickVerification();
                case "0" -> {
                    running = false;
                    dbManager.close();
                    System.out.println("\nExiting DBA Management Tool. Goodbye!");
                }
                default -> System.out.println("[WARN] Invalid option. Please enter a number between 0 and 9.");
            }
        }
    }

    private void printBanner() {
        System.out.println("========================================");
        System.out.println(" Database Systems - Project 1");
        System.out.println(" JDBC Database Management Tool");
        System.out.println("========================================");
    }

    private void promptInitialConnection() {
        System.out.println("\n[SETUP] Do you want to connect using default local MySQL settings?");
        System.out.println("Default: jdbc:mysql://localhost:3306/dbs_proj1 (User: root, Password: [empty])");
        System.out.print("Connect with defaults now? (Y/n): ");
        String ans = scanner.nextLine().trim();

        if (ans.equalsIgnoreCase("y") || ans.isEmpty()) {
            try {
                dbManager.connect();
            } catch (SQLException e) {
                System.err.println("[WARN] Default connection failed: " + e.getMessage());
                System.out.println("You can configure a custom connection string using Option 1 in the menu.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\nMain Menu");
        System.out.println(" 1. Connect to database");
        System.out.println(" 2. Show database metadata");
        System.out.println(" 3. List tables");
        System.out.println(" 4. Describe a table");
        System.out.println(" 5. Run a query");
        System.out.println(" 6. Run an update");
        System.out.println(" 7. Run a transaction");
        System.out.println(" 8. Run an SQL script");
        System.out.println(" 9. Run verification queries");
        System.out.println(" 0. Exit");
    }

    private void handleConnect() {
        System.out.println("\nConnect to Database");
        System.out.print("JDBC URL [default: jdbc:mysql://localhost:3306/dbs_proj1?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC]: ");
        String url = scanner.nextLine().trim();
        if (url.isEmpty()) {
            url = "jdbc:mysql://localhost:3306/dbs_proj1?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
        }

        System.out.print("Username [default: root]: ");
        String user = scanner.nextLine().trim();
        if (user.isEmpty()) {
            user = "root";
        }

        System.out.print("Password [leave empty if none]: ");
        String password = scanner.nextLine();

        try {
            dbManager.connect(url, user, password);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to connect: " + e.getMessage());
        }
    }

    private void handleShowDatabaseInfo() {
        try {
            MetadataExplorer.showDatabaseInfo(dbManager.getConnection());
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to read metadata: " + e.getMessage());
        }
    }

    private void handleListTables() {
        try {
            MetadataExplorer.listTables(dbManager.getConnection());
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to list tables: " + e.getMessage());
        }
    }

    private void handleDescribeTable() {
        System.out.print("\nEnter table name to describe: ");
        String tableName = scanner.nextLine().trim();
        if (tableName.isEmpty()) {
            System.out.println("[WARN] Table name cannot be empty.");
            return;
        }

        try {
            MetadataExplorer.describeTable(dbManager.getConnection(), tableName);
        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to describe table: " + e.getMessage());
        }
    }

    private void handleExecuteQuery() {
        System.out.println("\nRun Query");
        System.out.println("Enter SQL SELECT statement (type 'cancel' to return):");
        System.out.print("SQL> ");
        String sql = scanner.nextLine().trim();

        if (sql.equalsIgnoreCase("cancel") || sql.isEmpty()) {
            return;
        }

        dbManager.executeQuery(sql);
    }

    private void handleExecuteUpdate() {
        System.out.println("\nRun Update");
        System.out.println("Enter SQL statement (INSERT, UPDATE, DELETE, CREATE, DROP, ALTER):");
        System.out.print("SQL> ");
        String sql = scanner.nextLine().trim();

        if (sql.equalsIgnoreCase("cancel") || sql.isEmpty()) {
            return;
        }

        try {
            dbManager.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.printf("[SQL ERROR %d] %s%n", e.getErrorCode(), e.getMessage());
        }
    }

    private void handleTransactionSession() {
        System.out.println("\nTransaction Mode");
        System.out.println("Enter SQL statements one at a time.");
        System.out.println("Type COMMIT to apply changes or ROLLBACK to cancel.");

        List<String> buffer = new ArrayList<>();
        int lineNo = 1;

        while (true) {
            System.out.printf("TX [%d]> ", lineNo);
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("COMMIT")) {
                if (buffer.isEmpty()) {
                    System.out.println("[INFO] No statements to commit.");
                } else {
                    dbManager.executeTransaction(buffer);
                }
                break;
            } else if (line.equalsIgnoreCase("ROLLBACK") || line.equalsIgnoreCase("CANCEL")) {
                System.out.println("[TRANSACTION] Transaction session aborted by user.");
                break;
            } else if (!line.isEmpty()) {
                buffer.add(line);
                lineNo++;
            }
        }
    }

    private void handleExecuteScript() {
        System.out.println("\nRun SQL Script");
        System.out.print("Enter file path [default: sql/init_sample_db.sql]: ");
        String path = scanner.nextLine().trim();
        if (path.isEmpty()) {
            path = "sql/init_sample_db.sql";
        }

        try {
            dbManager.executeSqlScript(path);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to execute script: " + e.getMessage());
        }
    }

    private void handleRunQuickVerification() {
        System.out.println("\nVerification Queries");
        String[] testQueries = {
            "SELECT 'Connection Active' AS status, CURRENT_TIMESTAMP() AS current_server_time",
            "SELECT table_name, table_rows, avg_row_length, data_length FROM information_schema.tables WHERE table_schema = DATABASE()",
            "SELECT * FROM departments ORDER BY department_id",
            "SELECT * FROM employees ORDER BY employee_id"
        };

        for (String sql : testQueries) {
            System.out.println("\n[RUNNING]: " + sql);
            dbManager.executeQuery(sql);
        }
    }

    public static void main(String[] args) {
        DBAToolApp app = new DBAToolApp();
        app.start();
    }
}
