package baitaplon.backend;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Interface for Sales Business Service
 * Defines contract for complex business operations
 */
public interface ISalesBusinessService {
    
    /**
     * Calculate total price for a list of products
     * @param products Map of product name to quantity
     * @return Total price as BigDecimal
     */
    BigDecimal calculateOrderTotal(Map<String, Integer> products);
    
    /**
     * Calculate required ingredients for a list of products
     * @param products Map of product name to quantity
     * @return Map of ingredient ID to required quantity
     */
    Map<Integer, Integer> calculateRequiredIngredients(Map<String, Integer> products);
    
    /**
     * Check if inventory is sufficient for required ingredients
     * @param requiredIngredients Map of ingredient ID to required quantity
     * @return Warning message if insufficient, null if sufficient
     */
    String checkInventoryAvailability(Map<Integer, Integer> requiredIngredients);
    
    /**
     * Consume ingredients from inventory
     * @param requiredIngredients Map of ingredient ID to quantity to consume
     * @return true if successful, false otherwise
     */
    boolean consumeIngredients(Map<Integer, Integer> requiredIngredients);
    
    /**
     * Process a complete order (create order + consume ingredients)
     * @param products Map of product name to quantity
     * @param employeeId Employee ID
     * @param note Order note
     * @return Order ID if successful, -1 otherwise
     */
    int processOrder(Map<String, Integer> products, int employeeId, String note);
    
    /**
     * Get daily sales statistics
     * @param date Date in format "yyyy-MM-dd"
     * @return Map containing statistics (totalOrders, paidOrders, totalRevenue, etc.)
     */
    Map<String, Object> getDailySalesStats(String date);
}

