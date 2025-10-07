package baitaplon.backend;

import java.util.List;
import java.util.Map;

/**
 * Interface for Product Management Service
 * Defines contract for product CRUD operations
 */
public interface IProductService {
    
    /**
     * Add a new product
     * @param name Product name
     * @param price Product price
     * @param nguyenlieu Ingredients in JSON format
     * @return true if successful, false otherwise
     */
    boolean addProduct(String name, int price, String nguyenlieu);
    
    /**
     * Update product by ID
     * @param id Product ID
     * @param name Product name
     * @param price Product price
     * @param nguyenlieu Ingredients in JSON format
     * @return true if successful, false otherwise
     */
    boolean updateProduct(int id, String name, int price, String nguyenlieu);
    
    /**
     * Delete product by ID
     * @param id Product ID
     * @return true if successful, false otherwise
     */
    boolean deleteProduct(int id);
    
    /**
     * Get product by name
     * @param name Product name
     * @return Product data as Map, null if not found
     */
    Map<String, Object> getProductByName(String name);
    
    /**
     * Get product by ID
     * @param id Product ID
     * @return Product data as Map, null if not found
     */
    Map<String, Object> getProductById(int id);
    
    /**
     * Get all products
     * @return List of all products as Object arrays
     */
    List<Object[]> getAllProducts();
}

