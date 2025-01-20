package de.fh.albsig.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Helper class for SQLite database operations.
 *
 * <p>Usage examples:</p>
 * <ul>
 *     <li>Insert data into a table:</li>
 *     <pre>
 *     SQLiteHelperMain.insert("Aggregate", "name, fuel_cap, consumption, power_output", "Generator A", 100.5, 5.0, 50.0);
 *     </pre>
 *     <li>Update rows in a table:</li>
 *     <pre>
 *     SQLiteHelperMain.update("Aggregate", "fuel_cap = ?, consumption = ?", "name = ?", 120.0, 6.0, "Generator A");
 *     </pre>
 *     <li>Delete rows from a table:</li>
 *     <pre>
 *     SQLiteHelperMain.delete("Aggregate", "name = ?", "Generator A");
 *     </pre>
 *     <li>Select rows from a table:</li>
 *     <pre>
 *     Object[][] results = SQLiteHelperMain.select("Aggregate", "name, fuel_cap", "fuel_cap > ?", 50.0);
 *     for (Object[] row : results) {
 *         System.out.println(Arrays.toString(row));
 *     }
 *     </pre>
 * </ul>
 */
public class SQLiteHelperMain {

    private static final String DATABASE_FILE = "local_database.db";
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_FILE;
    private static final Logger logger = LogManager.getLogger(SQLiteHelperMain.class);

    /**
     * Checks if the database file exists. If not, creates a new one.
     */
    public static void ensureDatabaseFileExists() {
        File dbFile = new File(DATABASE_FILE);
        if (!dbFile.exists()) {
            try {
                if (dbFile.createNewFile()) {
                    logger.info("Database file created: {}", DATABASE_FILE);
                }
            } catch (Exception e) {
                logger.error("Failed to create database file: {}", e.getMessage(), e);
            }
        } else {
            logger.info("Database file already exists: {}", DATABASE_FILE);
        }
    }

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return a Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection connect() throws SQLException {
        ensureDatabaseFileExists();
        return DriverManager.getConnection(DATABASE_URL);
    }

    /**
     * Initializes the database with required tables.
     * Creates tables if they do not exist.
     */
    public static void initializeDatabase() {
        String createTableSQL = """
            CREATE TABLE IF NOT EXISTS Aggregate (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                fuel_cap FLOAT NOT NULL,
                consumption FLOAT NOT NULL,
                power_output FLOAT NOT NULL
            );
        """;

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
            stmt.execute();
            logger.info("Database initialized successfully.");
        } catch (SQLException e) {
            logger.error("Failed to initialize database: {}", e.getMessage(), e);
        }
    }
    /**
     * Inserts a row into a specified table with specified column values.
     *
     * @param table the name of the table
     * @param columns the columns to insert into, separated by commas
     * @param values the values to insert, matching the order of the columns
     */
    public static void insert(String table, String columns, Object... values) {
        String placeholders = "?".repeat(values.length).replace("", ", ").trim().substring(1);
        placeholders = placeholders.substring(0, placeholders.length() - 1);
        String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", table, columns, placeholders);

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, values);
            stmt.executeUpdate();
            logger.info("Inserted into table {}: columns=[{}], values={}", table, columns, values);
        } catch (SQLException e) {
            logger.error("Failed to insert into table {}: {}", table, e.getMessage(), e);
        }
    }

    /**
     * Updates rows in a specified table based on conditions.
     *
     * @param table the name of the table
     * @param setClause the columns to update with placeholders (e.g., "col1 = ?, col2 = ?")
     * @param whereClause the WHERE clause with placeholders (e.g., "id = ?")
     * @param values the values to bind to the placeholders in the set and where clauses
     */
    public static void update(String table, String setClause, String whereClause, Object... values) {
        String sql = String.format("UPDATE %s SET %s WHERE %s", table, setClause, whereClause);

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, values);
            stmt.executeUpdate();
            logger.info("Updated table {}: set={}, where={}, values={}", table, setClause, whereClause, values);
        } catch (SQLException e) {
            logger.error("Failed to update table {}: {}", table, e.getMessage(), e);
        }
    }

    /**
     * Deletes rows from a specified table based on conditions.
     *
     * @param table the name of the table
     * @param whereClause the WHERE clause with placeholders (e.g., "id = ?")
     * @param values the values to bind to the placeholders in the where clause
     */
    public static void delete(String table, String whereClause, Object... values) {
        String sql = String.format("DELETE FROM %s WHERE %s", table, whereClause);

        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, values);
            stmt.executeUpdate();
            logger.info("Deleted from table {}: where={}, values={}", table, whereClause, values);
        } catch (SQLException e) {
            logger.error("Failed to delete from table {}: {}", table, e.getMessage(), e);
        }
    }

    /**
     * Selects rows from a specified table and returns the results as a 2D array.
     *
     * @param table the name of the table
     * @param columns the columns to select, separated by commas
     * @param whereClause the WHERE clause with placeholders (optional, can be null)
     * @param values the values to bind to the placeholders in the where clause (if applicable)
     * @return a 2D array containing the results, where each row is an Object array
     */
    public static Object[][] select(String table, String columns, String whereClause, Object... values) {
        String sql = String.format("SELECT %s FROM %s", columns, table);
        if (whereClause != null && !whereClause.isBlank()) {
            sql += " WHERE " + whereClause;
        }

        List<Object[]> results = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameters(stmt, values);
            try (ResultSet rs = stmt.executeQuery()) {
                int columnCount = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 1; i <= columnCount; i++) {
                        row[i - 1] = rs.getObject(i);
                    }
                    results.add(row);
                }
            }
            logger.info("Selected from table {}: columns=[{}], where={}, values={}, rows fetched={}.",
                    table, columns, whereClause, values, results.size());
        } catch (SQLException e) {
            logger.error("Failed to select from table {}: {}", table, e.getMessage(), e);
        }
        return results.toArray(new Object[0][]);
    }

    /**
     * Helper method to bind parameters to a PreparedStatement.
     *
     * @param stmt the PreparedStatement
     * @param parameters the parameters to bind
     * @throws SQLException if a database access error occurs
     */
    private static void setParameters(PreparedStatement stmt, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            stmt.setObject(i + 1, parameters[i]);
        }
    }
}
