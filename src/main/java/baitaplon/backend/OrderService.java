package baitaplon.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import com.google.gson.Gson;

/**
 * Service for order management (donhang table)
 * Handles basic CRUD operations for orders
 */
public class OrderService extends BaseService implements IOrderService {
    private final Gson gson = new Gson();
    
    /**
     * Create a new order
     */
    public int createOrder(Map<String, Object> orderContent, int totalPrice, int status, String note, int employeeId) {
        try {
            String contentJson = gson.toJson(orderContent);
            String orderCode = "DH" + java.util.UUID.randomUUID().toString().replace("-", "").substring(0, 8);
            
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("madonhang", orderCode);
            orderData.put("noidung", contentJson);
            orderData.put("giatien", totalPrice);
            orderData.put("trangthai", status);
            orderData.put("ghichu", note);
            orderData.put("nhanvien_id", employeeId);
            // Use UTC timezone for new orders (database stores UTC)
            orderData.put("date", new java.sql.Timestamp(System.currentTimeMillis()));
            
            if (insert("donhang", orderData)) {
                Map<String, Object> createdOrder = getSingle("donhang", "madonhang='" + orderCode + "'");
                if (createdOrder != null) {
                    return (int) createdOrder.get("id");
                }
            }
            return -1;
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Update order by order code
     */
    public boolean updateOrder(String orderCode, Map<String, Object> updateData) {
        return update("donhang", updateData, "madonhang='" + orderCode + "'");
    }
    
    /**
     * Get order by order code
     */
    public Map<String, Object> getOrderByCode(String orderCode) {
        return getSingle("donhang", "madonhang='" + orderCode + "'");
    }
    
    /**
     * Get order value by order code and column
     */
    public Object getOrderValue(String orderCode, String column) {
        Map<String, Object> order = getOrderByCode(orderCode);
        return order != null ? order.get(column) : null;
    }
    
    /**
     * Get all orders
     */
    public List<Object[]> getAllOrders() {
        return getAll("donhang");
    }
    
    /**
     * Get orders by date range
     */
    public List<Object[]> getOrdersByDateRange(String startDate, String endDate) {
        return getByCondition("donhang", "date BETWEEN '" + startDate + "' AND '" + endDate + "'");
    }
    
    /**
     * Get orders by status
     */
    public List<Object[]> getOrdersByStatus(int status) {
        return getByCondition("donhang", "trangthai=" + status);
    }
    
    /**
     * Get orders by employee
     */
    public List<Object[]> getOrdersByEmployee(int employeeId) {
        return getByCondition("donhang", "nhanvien_id=" + employeeId);
    }
}
