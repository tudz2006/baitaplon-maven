package baitaplon.backend;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for product management (xuathang table)
 * Handles basic CRUD operations for products
 */
public class ProductService extends BaseService implements IProductService {
    
    /**
     * Add a new product
     */
    public boolean addProduct(String name, int price, String nguyenlieu) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        data.put("nguyenlieu", nguyenlieu);
        return insert("xuathang", data);
    }
    
    /**
     * Update product by ID
     */
    public boolean updateProduct(int id, String name, int price, String nguyenlieu) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("price", price);
        data.put("nguyenlieu", nguyenlieu);
        return update("xuathang", data, "id=" + id);
    }
    
    /**
     * Delete product by ID
     */
    public boolean deleteProduct(int id) {
        return delete("xuathang", "id=" + id);
    }
    
    /**
     * Get product by name
     */
    public Map<String, Object> getProductByName(String name) {
        return getSingle("xuathang", "name='" + name + "'");
    }
    
    /**
     * Get product by ID
     */
    public Map<String, Object> getProductById(int id) {
        return getSingle("xuathang", "id=" + id);
    }
    
    /**
     * Get all products
     */
    public java.util.List<Object[]> getAllProducts() {
        return getAll("xuathang");
    }
}
