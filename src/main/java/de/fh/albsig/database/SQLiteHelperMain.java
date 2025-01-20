package de.fh.albsig.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class for SQLite database operations.
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
}
