package com.dbs.dba;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class that inspects ResultSetMetaData to dynamically format
 * SQL query results into well-aligned ASCII text tables.
 */
public class TableFormatter {

    private static final int MAX_COLUMN_WIDTH = 40;

    /**
     * Prints a ResultSet as a formatted ASCII grid table.
     *
     * @param rs The executed ResultSet
     * @return Number of rows displayed
     * @throws SQLException If metadata or data retrieval fails
     */
    public static int printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        if (columnCount == 0) {
            System.out.println("Query returned an empty column set.");
            return 0;
        }

        List<String> headers = new ArrayList<>();
        List<Integer> colWidths = new ArrayList<>();

        // Read column headers and initialize column widths
        for (int i = 1; i <= columnCount; i++) {
            String label = meta.getColumnLabel(i);
            if (label == null || label.isEmpty()) {
                label = meta.getColumnName(i);
            }
            headers.add(label);
            colWidths.add(Math.max(label.length(), 6));
        }

        // Cache rows to calculate exact maximum column widths dynamically
        List<List<String>> rows = new ArrayList<>();
        while (rs.next()) {
            List<String> row = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                Object val = rs.getObject(i);
                String cell = (val == null) ? "NULL" : val.toString();
                if (cell.length() > MAX_COLUMN_WIDTH) {
                    cell = cell.substring(0, MAX_COLUMN_WIDTH - 3) + "...";
                }
                row.add(cell);

                int currentWidth = colWidths.get(i - 1);
                if (cell.length() > currentWidth) {
                    colWidths.set(i - 1, cell.length());
                }
            }
            rows.add(row);
        }

        // Print header and border
        printDivider(colWidths);
        printRow(headers, colWidths);
        printDivider(colWidths);

        // Print data rows
        for (List<String> row : rows) {
            printRow(row, colWidths);
        }

        printDivider(colWidths);
        System.out.printf("(%d %s returned)%n%n", rows.size(), rows.size() == 1 ? "row" : "rows");
        return rows.size();
    }

    private static void printDivider(List<Integer> colWidths) {
        StringBuilder sb = new StringBuilder("+");
        for (int w : colWidths) {
            sb.append("-".repeat(w + 2)).append("+");
        }
        System.out.println(sb);
    }

    private static void printRow(List<String> values, List<Integer> colWidths) {
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < values.size(); i++) {
            String val = values.get(i);
            int width = colWidths.get(i);
            sb.append(" ").append(padRight(val, width)).append(" |");
        }
        System.out.println(sb);
    }

    private static String padRight(String s, int n) {
        if (s.length() >= n) {
            return s;
        }
        return s + " ".repeat(n - s.length());
    }
}
