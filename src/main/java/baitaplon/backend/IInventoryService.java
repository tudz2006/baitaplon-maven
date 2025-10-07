package baitaplon.backend;

import java.util.List;
import java.util.Map;

/**
 * Interface for Inventory Management Service
 * Defines contract for inventory CRUD operations
 */
public interface IInventoryService {
    
    /**
     * Add a new inventory item
     * @param name Item name
     * @param price Item price
     * @param count Item count
     * @return true if successful, false otherwise
     */
    boolean addInventoryItem(String name, int price, int count);
    
    /**
     * Update inventory item by ID
     * @param id Item ID
     * @param name Item name
     * @param price Item price
     * @param count Item count
     * @return true if successful, false otherwise
     */
    boolean updateInventoryItem(int id, String name, int price, int count);
    
    /**
     * Update only inventory count
     * @param id Item ID
     * @param count New count
     * @return true if successful, false otherwise
     */
    boolean updateInventoryCount(int id, int count);
    
    /**
     * Delete inventory item by ID
     * @param id Item ID
     * @return true if successful, false otherwise
     */
    boolean deleteInventoryItem(int id);
    
    /**
     * Get inventory item by ID
     * @param id Item ID
     * @return Item data as Map, null if not found
     */
    Map<String, Object> getInventoryItemById(int id);
    
    /**
     * Get inventory item by name
     * @param name Item name
     * @return Item data as Map, null if not found
     */
    Map<String, Object> getInventoryItemByName(String name);
    
    /**
     * Get all inventory items
     * @return List of all inventory items as Object arrays
     */
    List<Object[]> getAllInventoryItems();
}

