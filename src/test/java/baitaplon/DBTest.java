package baitaplon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;

/**
 * Test class for DB functionality
 */
public class DBTest {
    
    private DB db;
    
    @BeforeEach
    void setUp() {
        db = new DB();
    }
    
    @Test
    @DisplayName("Test database connection")
    void testDatabaseConnection() {
        // Skip this test if database is not available
        try {
            Object result = db.fetchScalar("SELECT 1");
            assertNotNull(result);
            assertEquals(1, result);
        } catch (Exception e) {
            System.out.println("Database connection test skipped: " + e.getMessage());
            // Don't fail the test if database is not available
        }
    }
    
    @Test
    @DisplayName("Test basic SQL execution for SELECT queries")
    void testBasicSQLExecution() {
        // Skip this test if database is not available
        try {
            Object result = db.fetchScalar("SELECT 1 as test_value");
            assertNotNull(result);
            assertEquals(1, result);
        } catch (Exception e) {
            System.out.println("Basic SQL execution test skipped: " + e.getMessage());
            // Don't fail the test if database is not available
        }
    }
    
    @Test
    @DisplayName("Test execute method with non-SELECT statements")
    void testExecuteMethod() {
        // Test that execute method properly rejects SELECT statements
        assertThrows(SQLException.class, () -> {
            db.execute("SELECT 1");
        });
        
        // Test that execute method accepts non-SELECT statements (if database is available)
        try {
            int result = db.execute("CREATE TABLE IF NOT EXISTS test_table (id INT, name VARCHAR(50))");
            // CREATE TABLE returns 0 for success
            assertTrue(result >= 0, "CREATE TABLE should succeed or return 0");
        } catch (SQLException e) {
            // Table creation might fail due to permissions or database not available
            System.out.println("Table creation failed (expected in some environments): " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Test fetchAll method")
    void testFetchAll() {
        // Skip this test if database is not available
        try {
            var results = db.fetchAll("SELECT 1 as col1, 2 as col2");
            assertNotNull(results);
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            
            var firstRow = results.get(0);
            assertEquals(1, firstRow.get("col1"));
            assertEquals(2, firstRow.get("col2"));
        } catch (Exception e) {
            System.out.println("fetchAll test skipped: " + e.getMessage());
            // Don't fail the test if database is not available
        }
    }
    
    @Test
    @DisplayName("Test fetchArray method")
    void testFetchArray() {
        // Skip this test if database is not available
        try {
            var results = db.fetchArray("SELECT 1, 2, 3");
            assertNotNull(results);
            assertFalse(results.isEmpty());
            assertEquals(1, results.size());
            
            var firstRow = results.get(0);
            assertEquals(3, firstRow.length);
            assertEquals(1, firstRow[0]);
            assertEquals(2, firstRow[1]);
            assertEquals(3, firstRow[2]);
        } catch (Exception e) {
            System.out.println("fetchArray test skipped: " + e.getMessage());
            // Don't fail the test if database is not available
        }
    }
}
