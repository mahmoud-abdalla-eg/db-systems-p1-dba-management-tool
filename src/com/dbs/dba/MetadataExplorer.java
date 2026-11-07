package com.dbs.dba;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all metadata extraction and display using JDBC's DatabaseMetaData interface.
 * Implements DBA tools inspection for tables, column details, constraints, and driver info.
 */
public class MetadataExplorer {

    /**
     * Displays general database and JDBC driver specifications.
     */
    public static void showDatabaseInfo(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();

        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|                   DATABASE & DRIVER METADATA                |");
        System.out.println("+-------------------------------------------------------------+");
        System.out.printf("| %-25s : %-31s |%n", "Database Product Name", meta.getDatabaseProductName());
        System.out.printf("| %-25s : %-31s |%n", "Database Product Version", meta.getDatabaseProductVersion());
        System.out.printf("| %-25s : %-31s |%n", "Database Major.Minor", meta.getDatabaseMajorVersion() + "." + meta.getDatabaseMinorVersion());
        System.out.printf("| %-25s : %-31s |%n", "JDBC Driver Name", meta.getDriverName());
        System.out.printf("| %-25s : %-31s |%n", "JDBC Driver Version", meta.getDriverVersion());
        System.out.printf("| %-25s : %-31s |%n", "JDBC Specification", meta.getJDBCMajorVersion() + "." + meta.getJDBCMinorVersion());
        System.out.printf("| %-25s : %-31s |%n", "Connected URL", meta.getURL());
        System.out.printf("| %-25s : %-31s |%n", "Connected User", meta.getUserName());
        System.out.printf("| %-25s : %-31s |%n", "Current Catalog", conn.getCatalog());
        System.out.println("+-------------------------------------------------------------+\n");
    }

    /**
     * Lists all tables and views in the current database catalog.
     */
    public static List<String> listTables(Connection conn) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String catalog = conn.getCatalog();
        List<String> tableNames = new ArrayList<>();

        System.out.println("\n+-------------------------------------------------------------+");
        System.out.printf("| Tables in Catalog [%-39s] |%n", catalog != null ? catalog : "DEFAULT");
        System.out.println("+-----+------------------------------------+------------------+");
        System.out.printf("| %-3s | %-34s | %-16s |%n", "#", "Table Name", "Table Type");
        System.out.println("+-----+------------------------------------+------------------+");

        try (ResultSet rs = meta.getTables(catalog, null, "%", new String[]{"TABLE", "VIEW"})) {
            int count = 0;
            while (rs.next()) {
                count++;
                String name = rs.getString("TABLE_NAME");
                String type = rs.getString("TABLE_TYPE");
                tableNames.add(name);
                System.out.printf("| %-3d | %-34s | %-16s |%n", count, name, type);
            }

            if (count == 0) {
                System.out.println("| (No user tables or views found in this catalog)              |");
            }
            System.out.println("+-----+------------------------------------+------------------+");
            System.out.printf("Total: %d table(s)%n%n", count);
        }
        return tableNames;
    }

    /**
     * Describes the detailed schema of a table: columns, types, nullability, PKs, and FKs.
     */
    public static void describeTable(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        String catalog = conn.getCatalog();

        System.out.printf("%n=== Table Schema: %s ===%n", tableName.toUpperCase());

        // 1. Columns metadata
        System.out.println("+-----+----------------------+----------------------+---------+----------+");
        System.out.printf("| %-3s | %-20s | %-20s | %-7s | %-8s |%n", "#", "Column Name", "Type Name", "Size", "Nullable");
        System.out.println("+-----+----------------------+----------------------+---------+----------+");

        try (ResultSet columns = meta.getColumns(catalog, null, tableName, "%")) {
            int colIdx = 0;
            while (columns.next()) {
                colIdx++;
                String colName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                int colSize = columns.getInt("COLUMN_SIZE");
                String isNullable = columns.getString("IS_NULLABLE");

                System.out.printf("| %-3d | %-20s | %-20s | %-7d | %-8s |%n",
                        colIdx, colName, typeName, colSize, isNullable);
            }
            if (colIdx == 0) {
                System.out.printf("| (Table '%s' does not exist or has no columns)%n", tableName);
                System.out.println("+-----+----------------------+----------------------+---------+----------+\n");
                return;
            }
            System.out.println("+-----+----------------------+----------------------+---------+----------+");
        }

        // 2. Primary Keys metadata
        System.out.println("\n[Primary Keys]");
        try (ResultSet pks = meta.getPrimaryKeys(catalog, null, tableName)) {
            boolean hasPk = false;
            while (pks.next()) {
                hasPk = true;
                String colName = pks.getString("COLUMN_NAME");
                short keySeq = pks.getShort("KEY_SEQ");
                String pkName = pks.getString("PK_NAME");
                System.out.printf("  * Column: %s (Sequence: %d, PK Constraint: %s)%n",
                        colName, keySeq, pkName != null ? pkName : "N/A");
            }
            if (!hasPk) {
                System.out.println("  * (No primary key defined)");
            }
        }

        // 3. Foreign Keys (Imported Keys) metadata
        System.out.println("\n[Foreign Keys (References)]");
        try (ResultSet fks = meta.getImportedKeys(catalog, null, tableName)) {
            boolean hasFk = false;
            while (fks.next()) {
                hasFk = true;
                String fkCol = fks.getString("FKCOLUMN_NAME");
                String pkTable = fks.getString("PKTABLE_NAME");
                String pkCol = fks.getString("PKCOLUMN_NAME");
                String fkName = fks.getString("FK_NAME");
                System.out.printf("  * %s -> %s(%s) [FK Name: %s]%n",
                        fkCol, pkTable, pkCol, fkName != null ? fkName : "N/A");
            }
            if (!hasFk) {
                System.out.println("  * (No foreign keys imported)");
            }
        }

        // 4. Indexes metadata
        System.out.println("\n[Indexes]");
        try (ResultSet idx = meta.getIndexInfo(catalog, null, tableName, false, false)) {
            boolean hasIdx = false;
            while (idx.next()) {
                String indexName = idx.getString("INDEX_NAME");
                String colName = idx.getString("COLUMN_NAME");
                boolean nonUnique = idx.getBoolean("NON_UNIQUE");
                if (indexName != null) {
                    hasIdx = true;
                    System.out.printf("  * Index: %-20s | Column: %-15s | Unique: %s%n",
                            indexName, colName != null ? colName : "N/A", !nonUnique ? "YES" : "NO");
                }
            }
            if (!hasIdx) {
                System.out.println("  * (No additional indexes)");
            }
        }
        System.out.println();
    }
}
