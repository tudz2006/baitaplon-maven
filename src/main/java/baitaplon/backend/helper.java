/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package baitaplon.backend;

/**
 * Helper class for basic CRUD operations
 * Implements ihelper interface for backward compatibility
 * @author HI
 */
import java.util.Map;
import java.util.List;

public class helper implements ihelper {
    
    // Service instances for different domains
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final EmployeeService employeeService;
    
    public helper() {
        this.productService = new ProductService();
        this.inventoryService = new InventoryService();
        this.orderService = new OrderService();
        this.employeeService = new EmployeeService();
    }
    
    /**
     * Clear console (utility method)
     */
    public void xoamh() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    @Override
    public Map<String,String> nhanvien_list() {
        return employeeService.getEmployeeList();
    }
    @Override
    public int create_order(Map<String,Object> map_noidung, int giatien, int trangthai, String ghichu, int nhanvien_id) {
        return orderService.createOrder(map_noidung, giatien, trangthai, ghichu, nhanvien_id);
    }
    @Override
    public boolean update_order(String madonhang, Map<String,Object> update) {
        return orderService.updateOrder(madonhang, update);
    }
    
    @Override
    public Object getvaluebyorder(String madonhang, String column) {
        return orderService.getOrderValue(madonhang, column);
    }
    
    @Override
    public Map<String,Object> get_order(String madonhang) {
        return orderService.getOrderByCode(madonhang);
    }
    
    @Override
    public List<Object[]> getinfo(String table) {
        switch (table.toLowerCase()) {
            case "xuathang":
                return productService.getAllProducts();
            case "nhaphang":
                return inventoryService.getAllInventoryItems();
            case "donhang":
                return orderService.getAllOrders();
            case "nhanvien":
                return employeeService.getAllEmployees();
            default:
                // Fallback to generic method
                return productService.getAll(table);
        }
    }
    @Override
    public boolean nhaphang_add(String name, int price, int count) {
        return inventoryService.addInventoryItem(name, price, count);
    }

    @Override
    public boolean nhaphang_update(int id, Map<String, Object> update) {
        // Extract values from update map
        String name = update.get("name") != null ? update.get("name").toString() : null;
        Integer price = update.get("price") != null ? (Integer) update.get("price") : null;
        Integer count = update.get("count") != null ? (Integer) update.get("count") : null;
        
        // Get current values for null fields
        Map<String, Object> current = inventoryService.getInventoryItemById(id);
        if (current == null) return false;
        
        if (name == null) name = current.get("name").toString();
        if (price == null) price = (Integer) current.get("price");
        if (count == null) count = (Integer) current.get("count");
        
        return inventoryService.updateInventoryItem(id, name, price, count);
    }

    @Override
    public boolean nhaphang_delete(int id) {
        return inventoryService.deleteInventoryItem(id);
    }

    @Override
    public boolean xuathang_add(String name, int price, String nguyenlieu) {
        return productService.addProduct(name, price, nguyenlieu);
    }

    @Override
    public boolean xuathang_update(int id, Map<String, Object> update) {
        // Extract values from update map
        String name = update.get("name") != null ? update.get("name").toString() : null;
        Integer price = update.get("price") != null ? (Integer) update.get("price") : null;
        String nguyenlieu = update.get("nguyenlieu") != null ? update.get("nguyenlieu").toString() : null;
        
        // Get current values for null fields
        Map<String, Object> current = productService.getProductById(id);
        if (current == null) return false;
        
        if (name == null) name = current.get("name").toString();
        if (price == null) price = (Integer) current.get("price");
        if (nguyenlieu == null) nguyenlieu = current.get("nguyenlieu").toString();
        
        return productService.updateProduct(id, name, price, nguyenlieu);
    }

    @Override
    public boolean xuathang_delete(int id) {
        return productService.deleteProduct(id);
    }
}
