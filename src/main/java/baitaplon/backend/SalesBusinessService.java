package baitaplon.backend;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.math.BigDecimal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Business service for sales operations
 * Handles complex business logic for sales, inventory management, and order processing
 */
public class SalesBusinessService implements ISalesBusinessService {
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final Gson gson;
    
    public SalesBusinessService() {
        this.productService = new ProductService();
        this.inventoryService = new InventoryService();
        this.orderService = new OrderService();
        this.gson = new Gson();
    }
    
    /**
     * Calculate total price for a list of products
     */
    public BigDecimal calculateOrderTotal(Map<String, Integer> products) {
        BigDecimal total = BigDecimal.ZERO;
        
        for (Map.Entry<String, Integer> entry : products.entrySet()) {
            String productName = entry.getKey();
            Integer quantity = entry.getValue();
            
            Map<String, Object> product = productService.getProductByName(productName);
            if (product != null && product.get("price") != null) {
                BigDecimal price = new BigDecimal(product.get("price").toString());
                BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));
                total = total.add(itemTotal);
            }
        }
        
        return total;
    }
    
    /**
     * Calculate required ingredients for a list of products
     * Returns Map<ingredient_id, required_quantity>
     */
    public Map<Integer, Integer> calculateRequiredIngredients(Map<String, Integer> products) {
        Map<Integer, Integer> required = new HashMap<>();
        
        try {
            for (Map.Entry<String, Integer> entry : products.entrySet()) {
                String productName = entry.getKey();
                int productQuantity = entry.getValue();
                
                Map<String, Object> product = productService.getProductByName(productName);
                if (product == null) {
                    System.err.println("Product not found: " + productName);
                    continue;
                }
                
                Object ingredientsJson = product.get("nguyenlieu");
                if (ingredientsJson == null) continue;
                
                String json = ingredientsJson.toString();
                if (json.trim().isEmpty()) continue;
                
                Map<String, Number> ingredientsPerUnit = gson.fromJson(json, 
                    new TypeToken<Map<String, Number>>(){}.getType());
                
                if (ingredientsPerUnit == null) continue;
                
                for (Map.Entry<String, Number> ingredient : ingredientsPerUnit.entrySet()) {
                    int ingredientId = Integer.parseInt(ingredient.getKey());
                    int needPerUnit = ingredient.getValue() == null ? 0 : ingredient.getValue().intValue();
                    int totalNeed = needPerUnit * productQuantity;
                    
                    required.put(ingredientId, required.getOrDefault(ingredientId, 0) + totalNeed);
                }
            }
            return required;
        } catch (Exception e) {
            System.err.println("Error calculating required ingredients: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if inventory is sufficient for required ingredients
     * Returns warning message if insufficient, null if sufficient
     */
    public String checkInventoryAvailability(Map<Integer, Integer> requiredIngredients) {
        StringBuilder warnings = new StringBuilder();
        
        for (Map.Entry<Integer, Integer> entry : requiredIngredients.entrySet()) {
            int ingredientId = entry.getKey();
            int requiredQuantity = entry.getValue();
            
            Map<String, Object> ingredient = inventoryService.getInventoryItemById(ingredientId);
            if (ingredient == null) {
                warnings.append("- Ingredient ID ").append(ingredientId).append(" not found\n");
                continue;
            }
            
            int availableQuantity = Integer.parseInt(ingredient.get("count").toString());
            String ingredientName = ingredient.get("name").toString();
            
            if (availableQuantity < requiredQuantity) {
                warnings.append("- ").append(ingredientName)
                       .append(" (ID ").append(ingredientId).append(") insufficient: ")
                       .append("need ").append(requiredQuantity)
                       .append(", have ").append(availableQuantity).append("\n");
            }
        }
        
        return warnings.length() == 0 ? null : warnings.toString();
    }
    
    /**
     * Consume ingredients from inventory
     */
    public boolean consumeIngredients(Map<Integer, Integer> requiredIngredients) {
        try {
            for (Map.Entry<Integer, Integer> entry : requiredIngredients.entrySet()) {
                int ingredientId = entry.getKey();
                int consumedQuantity = entry.getValue();
                
                Map<String, Object> ingredient = inventoryService.getInventoryItemById(ingredientId);
                if (ingredient == null) continue;
                
                int currentQuantity = Integer.parseInt(ingredient.get("count").toString());
                int remainingQuantity = Math.max(0, currentQuantity - consumedQuantity);
                
                inventoryService.updateInventoryCount(ingredientId, remainingQuantity);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Error consuming ingredients: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Process a complete order (create order + consume ingredients)
     */
    public int processOrder(Map<String, Integer> products, int employeeId, String note) {
        try {
            // Calculate total price
            BigDecimal totalPrice = calculateOrderTotal(products);
            if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
                System.err.println("Invalid order total");
                return -1;
            }
            
            // Calculate required ingredients
            Map<Integer, Integer> requiredIngredients = calculateRequiredIngredients(products);
            if (requiredIngredients == null) {
                System.err.println("Error calculating required ingredients");
                return -1;
            }
            
            // Check inventory availability
            String inventoryWarning = checkInventoryAvailability(requiredIngredients);
            if (inventoryWarning != null) {
                System.err.println("Insufficient inventory:\n" + inventoryWarning);
                return -1;
            }
            
            // Create order (convert Map<String, Integer> to Map<String, Object>)
            Map<String, Object> orderContent = new HashMap<>();
            for (Map.Entry<String, Integer> entry : products.entrySet()) {
                orderContent.put(entry.getKey(), entry.getValue());
            }
            int orderId = orderService.createOrder(orderContent, totalPrice.intValue(), 0, note, employeeId);
            if (orderId <= 0) {
                System.err.println("Failed to create order");
                return -1;
            }
            
            // Consume ingredients
            if (!consumeIngredients(requiredIngredients)) {
                System.err.println("Failed to consume ingredients");
                return -1;
            }
            
            return orderId;
        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Get daily sales statistics
     */
    public Map<String, Object> getDailySalesStats(String date) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Object[]> orders = orderService.getOrdersByDateRange(date + " 00:00:00", date + " 23:59:59");
            
            int totalOrders = orders.size();
            int paidOrders = 0;
            int pendingOrders = 0;
            int cancelledOrders = 0;
            BigDecimal totalRevenue = BigDecimal.ZERO;
            
            for (Object[] order : orders) {
                if (order.length >= 5) {
                    int status = (Integer) order[4]; // trangthai
                    BigDecimal price = new BigDecimal(order[3].toString()); // giatien
                    
                    switch (status) {
                        case 0: pendingOrders++; break;
                        case 1: 
                            paidOrders++; 
                            totalRevenue = totalRevenue.add(price);
                            break;
                        case -1: cancelledOrders++; break;
                    }
                }
            }
            
            stats.put("totalOrders", totalOrders);
            stats.put("paidOrders", paidOrders);
            stats.put("pendingOrders", pendingOrders);
            stats.put("cancelledOrders", cancelledOrders);
            stats.put("totalRevenue", totalRevenue);
            
            return stats;
        } catch (Exception e) {
            System.err.println("Error getting daily sales stats: " + e.getMessage());
            return null;
        }
    }
}
