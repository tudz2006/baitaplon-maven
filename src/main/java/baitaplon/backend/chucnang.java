package baitaplon.backend;

import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.List;

/**
 * Business logic service for complex operations
 * Handles high-level business processes and workflows
 */
public class chucnang {
    private final SalesBusinessService salesBusinessService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final OrderService orderService;
    private final helper hp;
    
    private String currentEmpId = null;
    private String currentEmpName = null;
    
    public chucnang() {
        this.salesBusinessService = new SalesBusinessService();
        this.productService = new ProductService();
        this.inventoryService = new InventoryService();
        this.orderService = new OrderService();
        this.hp = new helper();
    }
    public void taodonhangmoi(String empId, String empName) {
        this.currentEmpId = empId;
        this.currentEmpName = empName;
        Map<String, Integer> sanpham = new HashMap<>();
        Scanner scanner = new Scanner(System.in);
        int choice;
        
        while (true) {
            hp.xoamh();
            // Hiển thị đơn hàng hiện tại
            if (!sanpham.isEmpty()) {
                System.out.println("=== Đơn hàng hiện tại ===");
                BigDecimal totalOrder = salesBusinessService.calculateOrderTotal(sanpham);
                
                for (Map.Entry<String, Integer> entry : sanpham.entrySet()) {
                    String productName = entry.getKey();
                    Integer quantity = entry.getValue();

                    Map<String, Object> product = productService.getProductByName(productName);
                    if (product == null) {
                        System.out.println("Không tìm thấy mặt hàng: " + productName);
                        continue;
                    }

                    Object priceObj = product.get("price");
                    if (priceObj == null) {
                        System.out.println("Mặt hàng " + productName + " không có giá");
                        continue;
                    }

                    BigDecimal price = new BigDecimal(priceObj.toString());
                    BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));
                    System.out.println(productName + " x " + quantity + " = " + itemTotal);
                }
                System.out.println("Tổng cộng: " + totalOrder);
                System.out.println();
            }

            System.out.println("1. Thêm sản phẩm mới");
            System.out.println("2. Xóa sản phẩm");
            System.out.println("3. Xuất hóa đơn" + (currentEmpName != null ? (" (NV: " + currentEmpName + ")") : ""));
            System.out.println("4. Thoát");
            System.out.print("Nhập lựa chọn: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    themSanPham(sanpham, scanner);
                    break;
                case 2:
                    xoaSanPham(sanpham, scanner);
                    break;
                case 3:
                    xuatHoaDon(sanpham);
                    return; // Thoát khỏi hàm sau khi xuất hóa đơn
                case 4:
                    return; // Thoát khỏi hàm
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
                    break;
            }
        }
    }
    
    private void themSanPham(Map<String, Integer> sanpham, Scanner scanner) {
        List<Object[]> list = productService.getAllProducts();
        if (list == null || list.isEmpty()) {
            System.out.println("Không có sản phẩm nào trong kho!");
            return;
        }
        
        System.out.println("=== Danh sách sản phẩm ===");
        for (int i = 0; i < list.size(); i++) {
            Object[] row = list.get(i);
            System.out.println((i + 1) + ". " + row[1] + " - " + row[2] + " VND"); // name, price
        }
        
        System.out.print("Chọn sản phẩm (số thứ tự): ");
        int productChoice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline
        
        if (productChoice < 0 || productChoice >= list.size()) {
            System.out.println("Lựa chọn không hợp lệ!");
            return;
        }
        
        Object[] selectedProduct = list.get(productChoice);
        String productName = (String) selectedProduct[1];
        
        System.out.print("Nhập số lượng: ");
        int quantity = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        if (quantity <= 0) {
            System.out.println("Số lượng phải lớn hơn 0!");
            return;
        }
        
        sanpham.put(productName, sanpham.getOrDefault(productName, 0) + quantity);
        System.out.println("Đã thêm " + quantity + " " + productName + " vào đơn hàng!");
    }
    
    private void xoaSanPham(Map<String, Integer> sanpham, Scanner scanner) {
        if (sanpham.isEmpty()) {
            System.out.println("Đơn hàng trống!");
            return;
        }
        
        System.out.println("=== Sản phẩm trong đơn hàng ===");
        int index = 1;
        for (Map.Entry<String, Integer> entry : sanpham.entrySet()) {
            System.out.println(index + ". " + entry.getKey() + " x " + entry.getValue());
            index++;
        }
        
        System.out.print("Chọn sản phẩm cần xóa (số thứ tự): ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline
        
        if (choice < 0 || choice >= sanpham.size()) {
            System.out.println("Lựa chọn không hợp lệ!");
            return;
        }
        
        String[] keys = sanpham.keySet().toArray(new String[0]);
        String productToRemove = keys[choice];
        sanpham.remove(productToRemove);
        System.out.println("Đã xóa " + productToRemove + " khỏi đơn hàng!");
    }
    
    private void xuatHoaDon(Map<String, Integer> sanpham) {
        if (sanpham.isEmpty()) {
            System.out.println("Đơn hàng trống, không thể xuất hóa đơn!");
            return;
        }
        
        // Calculate total using business service
        BigDecimal totalOrder = salesBusinessService.calculateOrderTotal(sanpham);
        
        // Check inventory availability
        Map<Integer, Integer> requiredIngredients = salesBusinessService.calculateRequiredIngredients(sanpham);
        if (requiredIngredients == null) {
            System.out.println("Lỗi khi tính toán nguyên liệu cần thiết!");
            return;
        }
        
        String inventoryWarning = salesBusinessService.checkInventoryAvailability(requiredIngredients);
        if (inventoryWarning != null) {
            System.out.println("Không đủ nguyên liệu:\n" + inventoryWarning);
            return;
        }

        System.out.println("=== HÓA ĐƠN BÁN HÀNG ===");
        if (currentEmpName != null) {
            System.out.println("Nhân viên: " + currentEmpName + (currentEmpId != null ? (" (" + currentEmpId + ")") : ""));
        }
        
        // Display order details
        for (Map.Entry<String, Integer> entry : sanpham.entrySet()) {
            String productName = entry.getKey();
            Integer quantity = entry.getValue();

            Map<String, Object> product = productService.getProductByName(productName);
            if (product == null) {
                System.out.println("Không tìm thấy mặt hàng: " + productName);
                continue;
            }

            Object priceObj = product.get("price");
            if (priceObj == null) {
                System.out.println("Mặt hàng " + productName + " không có giá!");
                continue;
            }

            BigDecimal price = new BigDecimal(priceObj.toString());
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(quantity));
            System.out.println(productName + " x " + quantity + " = " + itemTotal + " VND");
        }
        
        System.out.println("========================");
        System.out.println("TỔNG CỘNG: " + totalOrder + " VND");
        System.out.println("========================");
        
        // Process order using business service
        int empIdInt = 0;
        if (currentEmpId != null) {
            try { empIdInt = Integer.parseInt(currentEmpId); } catch (Exception ignore) { empIdInt = 0; }
        }
        
        int orderId = salesBusinessService.processOrder(sanpham, empIdInt, "Đơn hàng mới");
        
        if (orderId > 0) {
            System.out.println("Đã tạo đơn hàng thành công! ID đơn hàng: " + orderId);
        } else {
            System.out.println("Lỗi khi tạo đơn hàng!");
        }
    }

    /**
     * Get daily sales statistics
     */
    public Map<String, Object> getDailySalesStats(String date) {
        return salesBusinessService.getDailySalesStats(date);
    }
    
    /**
     * Process order with business logic
     */
    public int processOrder(Map<String, Integer> products, int employeeId, String note) {
        return salesBusinessService.processOrder(products, employeeId, note);
    }
    
    /**
     * Calculate order total
     */
    public BigDecimal calculateOrderTotal(Map<String, Integer> products) {
        return salesBusinessService.calculateOrderTotal(products);
    }
}
