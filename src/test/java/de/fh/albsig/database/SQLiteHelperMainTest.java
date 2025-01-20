package de.fh.albsig.database;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class SQLiteHelperMainTest {
    private static final String DATABASE_FILE = "local_database.db";

    @Before
    public void init(){
        // Delete the database file if somehow file is pressent bevore the test.
        File dbFile = new File(DATABASE_FILE);
        if (dbFile.exists() && dbFile.delete()) {
            System.out.println("Test database file deleted.");
        }else {
            System.err.println("Test database file could not be deleted.");
        }
    }

    @BeforeEach
    void setUp() {
        SQLiteHelperMain.ensureDatabaseFileExists();
        SQLiteHelperMain.initializeDatabase();
    }

    @AfterEach
    void tearDown() {
        // Delete the database file after each test to ensure a clean slate
        File dbFile = new File(DATABASE_FILE);
        if (dbFile.exists() && dbFile.delete()) {
            System.out.println("Test database file deleted.");
        }
    }


    @Test
    void ensureDatabaseFileExists() {
        // Test if the database file is created
        File dbFile = new File(DATABASE_FILE);
        assertTrue(dbFile.exists(), "Database file should exist.");
    }

    @Test
    void connect() {
        // Test if a connection to the database can be established
        assertDoesNotThrow(() -> {
            Connection connection = SQLiteHelperMain.connect();
            assertNotNull(connection, "Connection should not be null.");
            connection.close();
        });
    }

    @Test
    void initializeDatabase() {
        // Test if the users table is created successfully
        assertDoesNotThrow(() -> {
            Connection connection = SQLiteHelperMain.connect();
            String query = """
                SELECT name FROM sqlite_master
                WHERE type = 'table' AND name = 'Aggregate';
            """;
            var stmt = connection.prepareStatement(query);
            var resultSet = stmt.executeQuery();
            assertTrue(resultSet.next(), "The 'Aggregate' table should exist.");
            resultSet.close();
            stmt.close();
            connection.close();
        });
    }
}