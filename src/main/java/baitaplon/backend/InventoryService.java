package baitaplon.backend;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for inventory management (nhaphang table)
 * Handles basic CRUD operations for inventory items
 */
public class InventoryService extends BaseService implements IInventoryService {
    
    /**
     * Add a new inventory item
     */
    public boolean addInventoryItem(String name, int price, int count) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        data.put("count", count);
        return insert("nhaphang", data);
    }
    
    /**
     * Update inventory item by ID
     */
    public boolean updateInventoryItem(int id, String name, int price, int count) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        data.put("count", count);
        return update("nhaphang", data, "id=" + id);
    }
    
    /**
     * Update only inventory count
     */
    public boolean updateInventoryCount(int id, int count) {
        Map<String, Object> data = new HashMap<>();
        data.put("count", count);
        return update("nhaphang", data, "id=" + id);
    }
    
    /**
     * Delete inventory item by ID
     */
    public boolean deleteInventoryItem(int id) {
        return delete("nhaphang", "id=" + id);
    }
    
    /**
     * Get inventory item by ID
     */
    public Map<String, Object> getInventoryItemById(int id) {
        return getSingle("nhaphang", "id=" + id);
    }
    
    /**
     * Get inventory item by name
     */
    public Map<String, Object> getInventoryItemByName(String name) {
        return getSingle("nhaphang", "name='" + name + "'");
    }
    
    /**
     * Get all inventory items
     */
    public java.util.List<Object[]> getAllInventoryItems() {
        return getAll("nhaphang");
    }
}
