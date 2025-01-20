package de.fh.albsig.database;

import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class SqliteHelperMainTest {
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
        SqliteHelperMain.ensureDatabaseFileExists();
        SqliteHelperMain.initializeDatabase();
    }

    @AfterEach
    void tearDown() {
        // Delete the database file after each test to ensure a clean slate
        File dbFile = new File(DATABASE_FILE);
        if (dbFile.exists() && dbFile.delete()) {
            System.out.println("Test database file deleted.");
        }else {
            System.err.println("Test database file could not be deleted.");
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
            Connection connection = SqliteHelperMain.connect();
            assertNotNull(connection, "Connection should not be null.");
            connection.close();
        });
    }

    @Test
    void initializeDatabase() {
        // Test if the users table is created successfully
        assertDoesNotThrow(() -> {
            Connection connection = SqliteHelperMain.connect();
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

    @Test
    void select() {
        // Test the select method
        assertDoesNotThrow(() -> {
            SqliteHelperMain.insert("Aggregate", "name, fuel_cap, consumption, power_output", "Test Generator", 100.0, 5.0, 50.0);

            Object[][] results = SqliteHelperMain.select("Aggregate", "name, fuel_cap, consumption, power_output", "name = ?", "Test Generator");

            assertEquals(1, results.length, "There should be one row returned.");
            assertArrayEquals(new Object[]{"Test Generator", 100.0, 5.0, 50.0}, results[0], "The row data should match.");
        });
    }

    @Test
    void insert() {
        // Test the insert method
        assertDoesNotThrow(() -> {
            SqliteHelperMain.insert("Aggregate", "name, fuel_cap, consumption, power_output", "Test Generator", 100.0, 5.0, 50.0);

            Connection connection = SqliteHelperMain.connect();
            String query = "SELECT * FROM Aggregate WHERE name = 'Test Generator';";
            var stmt = connection.prepareStatement(query);
            var resultSet = stmt.executeQuery();

            assertTrue(resultSet.next(), "The row should have been inserted.");
            assertEquals("Test Generator", resultSet.getString("name"));
            assertEquals(100.0, resultSet.getFloat("fuel_cap"));
            assertEquals(5.0, resultSet.getFloat("consumption"));
            assertEquals(50.0, resultSet.getFloat("power_output"));

            resultSet.close();
            stmt.close();
            connection.close();
        });
    }

    @Test
    void update() {
        // Test the update method
        assertDoesNotThrow(() -> {
            SqliteHelperMain.insert("Aggregate", "name, fuel_cap, consumption, power_output", "Test Generator", 100.0, 5.0, 50.0);
            SqliteHelperMain.update("Aggregate", "fuel_cap = ?, consumption = ?", "name = ?", 150.0, 10.0, "Test Generator");

            Connection connection = SqliteHelperMain.connect();
            String query = "SELECT * FROM Aggregate WHERE name = 'Test Generator';";
            var stmt = connection.prepareStatement(query);
            var resultSet = stmt.executeQuery();

            assertTrue(resultSet.next(), "The row should exist after the update.");
            assertEquals(150.0, resultSet.getFloat("fuel_cap"));
            assertEquals(10.0, resultSet.getFloat("consumption"));

            resultSet.close();
            stmt.close();
            connection.close();
        });
    }

    @Test
    void delete() {
        // Test the delete method
        assertDoesNotThrow(() -> {
            SqliteHelperMain.insert("Aggregate", "name, fuel_cap, consumption, power_output", "Test Generator", 100.0, 5.0, 50.0);
            SqliteHelperMain.delete("Aggregate", "name = ?", "Test Generator");

            Connection connection = SqliteHelperMain.connect();
            String query = "SELECT * FROM Aggregate WHERE name = 'Test Generator';";
            var stmt = connection.prepareStatement(query);
            var resultSet = stmt.executeQuery();

            assertFalse(resultSet.next(), "The row should have been deleted.");

            resultSet.close();
            stmt.close();
            connection.close();
        });
    }
}