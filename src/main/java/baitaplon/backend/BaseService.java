package baitaplon.backend;

import java.util.List;
import java.util.Map;
import baitaplon.DB;

/**
 * Abstract base class for all services
 * Implements common functionality and defines contract for CRUD operations
 */
public abstract class BaseService implements IBaseService {
    protected DB db;
    
    public BaseService() {
        this.db = new DB();
    }
    
    /**
     * Get all records from a table
     */
    public List<Object[]> getAll(String tableName) {
        try {
            DB.Result res = db.select(tableName, " WHERE 1");
            return res.fetchArrayAll();
        } catch (Exception e) {
            System.err.println("Error getting all records from " + tableName + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get records by condition
     */
    public List<Object[]> getByCondition(String tableName, String condition) {
        try {
            DB.Result res = db.select(tableName, " WHERE " + condition);
            return res.fetchArrayAll();
        } catch (Exception e) {
            System.err.println("Error getting records from " + tableName + ": " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Insert a new record
     */
    public boolean insert(String tableName, Map<String, Object> data) {
        try {
            return db.insert(tableName, data);
        } catch (Exception e) {
            System.err.println("Error inserting into " + tableName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update records by condition
     */
    public boolean update(String tableName, Map<String, Object> data, String condition) {
        try {
            return db.update(tableName, data, " WHERE " + condition);
        } catch (Exception e) {
            System.err.println("Error updating " + tableName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete records by condition
     */
    public boolean delete(String tableName, String condition) {
        try {
            return db.delete(tableName, condition);
        } catch (Exception e) {
            System.err.println("Error deleting from " + tableName + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get single record by condition
     */
    public Map<String, Object> getSingle(String tableName, String condition) {
        try {
            DB.Result res = db.select(tableName, " WHERE " + condition);
            if (res.rowCount() > 0) {
                return res.getRows().get(0);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Error getting single record from " + tableName + ": " + e.getMessage());
            return null;
        }
    }
}
