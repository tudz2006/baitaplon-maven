package baitaplon.backend;

import java.util.List;
import java.util.Map;

/**
 * Interface for Order Management Service
 * Defines contract for order CRUD operations
 */
public interface IOrderService {
    
    /**
     * Create a new order
     * @param orderContent Order content as Map
     * @param totalPrice Total order price
     * @param status Order status
     * @param note Order note
     * @param employeeId Employee ID
     * @return Order ID if successful, -1 otherwise
     */
    int createOrder(Map<String, Object> orderContent, int totalPrice, int status, String note, int employeeId);
    
    /**
     * Update order by order code
     * @param orderCode Order code
     * @param updateData Update data
     * @return true if successful, false otherwise
     */
    boolean updateOrder(String orderCode, Map<String, Object> updateData);
    
    /**
     * Get order by order code
     * @param orderCode Order code
     * @return Order data as Map, null if not found
     */
    Map<String, Object> getOrderByCode(String orderCode);
    
    /**
     * Get order value by order code and column
     * @param orderCode Order code
     * @param column Column name
     * @return Column value, null if not found
     */
    Object getOrderValue(String orderCode, String column);
    
    /**
     * Get all orders
     * @return List of all orders as Object arrays
     */
    List<Object[]> getAllOrders();
    
    /**
     * Get orders by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of orders in date range
     */
    List<Object[]> getOrdersByDateRange(String startDate, String endDate);
    
    /**
     * Get orders by status
     * @param status Order status
     * @return List of orders with specified status
     */
    List<Object[]> getOrdersByStatus(int status);
    
    /**
     * Get orders by employee
     * @param employeeId Employee ID
     * @return List of orders by employee
     */
    List<Object[]> getOrdersByEmployee(int employeeId);
}

