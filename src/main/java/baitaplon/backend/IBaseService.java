package baitaplon.backend;

import java.util.List;
import java.util.Map;

/**
 * Base interface for all services
 * Defines common CRUD operations
 */
public interface IBaseService {
    
    /**
     * Get all records from a table
     * @param tableName Table name
     * @return List of records as Object arrays
     */
    List<Object[]> getAll(String tableName);
    
    /**
     * Get records by condition
     * @param tableName Table name
     * @param condition WHERE condition
     * @return List of records as Object arrays
     */
    List<Object[]> getByCondition(String tableName, String condition);
    
    /**
     * Insert a new record
     * @param tableName Table name
     * @param data Data to insert
     * @return true if successful, false otherwise
     */
    boolean insert(String tableName, Map<String, Object> data);
    
    /**
     * Update records by condition
     * @param tableName Table name
     * @param data Data to update
     * @param condition WHERE condition
     * @return true if successful, false otherwise
     */
    boolean update(String tableName, Map<String, Object> data, String condition);
    
    /**
     * Delete records by condition
     * @param tableName Table name
     * @param condition WHERE condition
     * @return true if successful, false otherwise
     */
    boolean delete(String tableName, String condition);
    
    /**
     * Get single record by condition
     * @param tableName Table name
     * @param condition WHERE condition
     * @return Record as Map, null if not found
     */
    Map<String, Object> getSingle(String tableName, String condition);
}
