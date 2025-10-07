/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package baitaplon.view;

import baitaplon.backend.helper;
import baitaplon.backend.ihelper;
import baitaplon.utils.CurrencyUtils;
import baitaplon.utils.SessionManager;
import baitaplon.utils.DatabaseTaskManager;
import baitaplon.utils.HikariCPManager;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author HI
 */
public class banhang extends javax.swing.JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(banhang.class);
    private ihelper helper = new helper();
    private SessionManager sessionManager = SessionManager.getInstance();
    private DefaultTableModel orderTableModel;
    private List<Map<String, Object>> currentOrderItems = new ArrayList<>();

    /**
     * Creates new form banhang
     */
    public banhang() {
        initComponents();
        setupUI();
        
        // Initialize HikariCP and DatabaseTaskManager
        try {
            HikariCPManager.initialize();
            DatabaseTaskManager.initialize();
            logger.info("Database components initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize database components", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khởi tạo database: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        
        checkSessionAndLoadData();
        
        // Configure daily orders table size after initialization
        configureDailyOrdersTableSize();
    }
    
    private void checkSessionAndLoadData() {
        // Cập nhật thông tin nhân viên hiện tại
        updateSessionInfo();
        
        if (!sessionManager.isSessionActive()) {
            // Hiển thị dialog chọn nhân viên
            EmployeeSelectionDialog dialog = new EmployeeSelectionDialog(null, true);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            if (!dialog.isSessionStarted()) {
                // Nếu không chọn nhân viên, disable tất cả chức năng
                disableAllFunctions();
                return;
            }
        }
        
        // Nếu có phiên làm việc, load dữ liệu bình thường
        enableAllFunctions();
        initializeOrderTable();
        loadAllDataAsync();
        updateSessionInfo();
    }
    
    private void disableAllFunctions() {
        // Disable tất cả các chức năng bán hàng
        addProductButton.setEnabled(false);
        removeProductButton.setEnabled(false);
        clearOrderButton.setEnabled(false);
        createOrderButton.setEnabled(false);
        newProductButton.setEnabled(false);
        updateOrderStatusButton.setEnabled(false);
        updateDailyOrderStatusButton.setEnabled(false);
        refreshOrdersButton.setEnabled(false);
        endSessionButton.setEnabled(false);
        
        // Enable nút bắt đầu ca mới
        startNewSessionButton.setEnabled(true);
        startNewSessionButton.setVisible(true);
        
        // Hiển thị thông báo
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Ca làm việc đã kết thúc! Click 'Bắt đầu ca mới' để chọn nhân viên.", 
            "Thông báo", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void updateSessionInfo() {
        if (sessionManager.isSessionActive()) {
            String employeeName = sessionManager.getCurrentEmployeeName();
            Integer employeeId = sessionManager.getCurrentEmployeeId();
            // Hiển thị thông tin nhân viên hiện tại
            currentEmployeeLabel.setText("Nhân viên ca hiện tại: " + employeeName + " (ID: " + employeeId + ")");
            currentEmployeeLabel.setForeground(new java.awt.Color(46, 204, 113)); // Màu xanh lá
            logger.info("Current session: {}", employeeName);
        } else {
            currentEmployeeLabel.setText("Chưa có nhân viên ca làm việc");
            currentEmployeeLabel.setForeground(new java.awt.Color(231, 76, 60)); // Màu đỏ
        }
    }
    
    private void endSession() {
        int result = javax.swing.JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn kết thúc ca làm việc?", 
            "Xác nhận kết thúc ca", 
            javax.swing.JOptionPane.YES_NO_OPTION);
        
        if (result == javax.swing.JOptionPane.YES_OPTION) {
            sessionManager.endSession();
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Đã kết thúc ca làm việc!", 
                "Thông báo", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            // Disable tất cả chức năng
            disableAllFunctions();
        }
    }
    
    private void startNewSession() {
        // Hiển thị dialog chọn nhân viên
        EmployeeSelectionDialog dialog = new EmployeeSelectionDialog(null, true);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
        
        if (dialog.isSessionStarted()) {
            // Nếu chọn nhân viên thành công, enable lại tất cả chức năng
            enableAllFunctions();
            loadProductData();
            loadDailyOrders();
            loadDailyStats();
            updateSessionInfo();
        }
    }
    
    private void enableAllFunctions() {
        // Enable tất cả các chức năng bán hàng
        addProductButton.setEnabled(true);
        removeProductButton.setEnabled(true);
        clearOrderButton.setEnabled(true);
        createOrderButton.setEnabled(true);
        newProductButton.setEnabled(true);
        updateOrderStatusButton.setEnabled(true);
        updateDailyOrderStatusButton.setEnabled(true);
        refreshOrdersButton.setEnabled(true);
        endSessionButton.setEnabled(true);
        
        // Hide nút bắt đầu ca mới
        startNewSessionButton.setEnabled(false);
        startNewSessionButton.setVisible(false);
    }
    
    private void setupUI() {
        // Set background
        setBackground(new java.awt.Color(248, 249, 250));
        
        // Style title
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(new java.awt.Color(70, 130, 180));
        
        // Style current employee label
        currentEmployeeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        currentEmployeeLabel.setForeground(new java.awt.Color(231, 76, 60)); // Màu đỏ mặc định
        
        // Style form panel
        formPanel.setBackground(java.awt.Color.WHITE);
        formPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Style buttons
        addProductButton.setBackground(new java.awt.Color(46, 204, 113));
        addProductButton.setForeground(java.awt.Color.WHITE);
        addProductButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        removeProductButton.setBackground(new java.awt.Color(231, 76, 60));
        removeProductButton.setForeground(java.awt.Color.WHITE);
        removeProductButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        createOrderButton.setBackground(new java.awt.Color(52, 152, 219));
        createOrderButton.setForeground(java.awt.Color.WHITE);
        createOrderButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        clearOrderButton.setBackground(new java.awt.Color(155, 89, 182));
        clearOrderButton.setForeground(java.awt.Color.WHITE);
        clearOrderButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        newProductButton.setBackground(new java.awt.Color(46, 204, 113));
        newProductButton.setForeground(java.awt.Color.WHITE);
        newProductButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        updateOrderStatusButton.setBackground(new java.awt.Color(52, 152, 219));
        updateOrderStatusButton.setForeground(java.awt.Color.WHITE);
        updateOrderStatusButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        refreshOrdersButton.setBackground(new java.awt.Color(46, 204, 113));
        refreshOrdersButton.setForeground(java.awt.Color.WHITE);
        refreshOrdersButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        updateDailyOrderStatusButton.setBackground(new java.awt.Color(52, 152, 219));
        updateDailyOrderStatusButton.setForeground(java.awt.Color.WHITE);
        updateDailyOrderStatusButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        endSessionButton.setBackground(new java.awt.Color(231, 76, 60));
        endSessionButton.setForeground(java.awt.Color.WHITE);
        endSessionButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        startNewSessionButton.setBackground(new java.awt.Color(46, 204, 113));
        startNewSessionButton.setForeground(java.awt.Color.WHITE);
        startNewSessionButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        startNewSessionButton.setVisible(false);
        startNewSessionButton.setEnabled(false);
        
        // Style tables
        productTable.getTableHeader().setBackground(new java.awt.Color(70, 130, 180));
        productTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        productTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        orderTable.getTableHeader().setBackground(new java.awt.Color(70, 130, 180));
        orderTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        orderTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        dailyOrdersTable.getTableHeader().setBackground(new java.awt.Color(70, 130, 180));
        dailyOrdersTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        dailyOrdersTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        // Set table selection modes
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        orderTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        dailyOrdersTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Set preferred size for daily orders table to show 8 rows
        int rowHeight = dailyOrdersTable.getRowHeight();
        if (rowHeight <= 0) {
            rowHeight = 20; // Default row height if not set
        }
        dailyOrdersTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(dailyOrdersTable.getPreferredSize().width, 
                                 rowHeight * 8));
        
        // Set minimum size to ensure 8 rows are visible
        dailyOrdersTable.setMinimumSize(new java.awt.Dimension(400, rowHeight * 8));
        
    }
    
    private void initializeOrderTable() {
        orderTableModel = new DefaultTableModel(
            new Object[][] {},
            new String[] {"Tên sản phẩm", "Giá", "Số lượng", "Thành tiền"}
        ) {
            boolean[] canEdit = new boolean[] {false, false, false, false};
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        };
        orderTable.setModel(orderTableModel);
    }
    
    private void loadProductData() {
        // Show loading indicator
        productTable.setEnabled(false);
        
        try {
            // Execute database operation asynchronously
            DatabaseTaskManager.executeAsyncWithUIUpdate(
                () -> {
                    try {
                        return helper.getinfo("xuathang");
                    } catch (Exception e) {
                        logger.error("Error loading product data", e);
                        throw new RuntimeException("Failed to load product data", e);
                    }
                },
                (products) -> {
                    updateProductTable(products);
                }
            ).exceptionally(throwable -> {
                logger.error("Async product loading failed, using sequential fallback", throwable);
                // Fallback to sequential loading
                javax.swing.SwingUtilities.invokeLater(() -> {
                    loadProductDataSequential();
                });
                return null;
            });
        } catch (Exception e) {
            logger.error("Failed to start async product loading, using sequential fallback", e);
            // Fallback to sequential loading
            loadProductDataSequential();
        }
    }
    
    
    private void addProductToOrder() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sản phẩm!", 
                "Thông báo", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            String productName = model.getValueAt(selectedRow, 0).toString();
            
            // Parse price using utility
            String priceText = model.getValueAt(selectedRow, 1).toString();
            int price = CurrencyUtils.parseCurrencySafe(priceText, 0);
            if (price == 0 && !CurrencyUtils.isValidCurrency(priceText)) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi định dạng giá sản phẩm!", 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int quantity = (Integer) quantitySpinner.getValue();
            
            // Check if product already exists in order
            for (Map<String, Object> item : currentOrderItems) {
                if (item.get("name").equals(productName)) {
                    int currentQty = (Integer) item.get("quantity");
                    item.put("quantity", currentQty + quantity);
                    updateOrderTable();
                    updateTotalAmount();
                    return;
                }
            }
            
            // Add new product to order
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("name", productName);
            orderItem.put("price", price);
            orderItem.put("quantity", quantity);
            
            // Use long to avoid overflow, then convert back to int
            long total = (long) price * (long) quantity;
            if (total > Integer.MAX_VALUE) {
                logger.warn("Order item total too large: {}, capping to max value", total);
                total = Integer.MAX_VALUE;
            }
            orderItem.put("total", (int) total);
            
            currentOrderItems.add(orderItem);
            updateOrderTable();
            updateTotalAmount();
            
        } catch (Exception e) {
            logger.error("Error adding product to order", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi thêm sản phẩm: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void removeProductFromOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sản phẩm cần xóa!", 
                "Thông báo", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        currentOrderItems.remove(selectedRow);
        updateOrderTable();
        updateTotalAmount();
    }
    
    private void updateOrderTable() {
        orderTableModel.setRowCount(0);
        
        for (Map<String, Object> item : currentOrderItems) {
            orderTableModel.addRow(new Object[]{
                item.get("name"),
                formatCurrency((Integer) item.get("price")),
                item.get("quantity"),
                formatCurrency((Integer) item.get("total"))
            });
        }
    }
    
    private void updateTotalAmount() {
        long total = 0;
        for (Map<String, Object> item : currentOrderItems) {
            total += (Integer) item.get("total");
        }
        
        // Cap at reasonable value to avoid display issues
        if (total > Integer.MAX_VALUE) {
            logger.warn("Order total too large: {}, capping to max value", total);
            total = Integer.MAX_VALUE;
        }
        
        totalAmountLabel.setText("Tổng tiền: " + formatCurrency((int) total));
    }
    
    private void clearOrder() {
        currentOrderItems.clear();
        updateOrderTable();
        updateTotalAmount();
        noteTextArea.setText("");
        quantitySpinner.setValue(1);
    }
    
    private void createOrder() {
        if (currentOrderItems.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng thêm sản phẩm vào đơn hàng!", 
                "Thông báo", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!sessionManager.isSessionActive()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn nhân viên để bắt đầu ca làm việc!", 
                "Thông báo", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Calculate total amount using long to avoid overflow
            long totalAmount = 0;
            for (Map<String, Object> item : currentOrderItems) {
                totalAmount += (Integer) item.get("total");
            }
            
            // Cap at reasonable value
            if (totalAmount > Integer.MAX_VALUE) {
                logger.warn("Order total amount too large: {}, capping to max value", totalAmount);
                totalAmount = Integer.MAX_VALUE;
            }
            
            // Get employee ID from session
            Integer employeeId = sessionManager.getCurrentEmployeeId();
            
            // Create order content in format {"tensanpham": so_luong}
            Map<String, Object> orderContent = new HashMap<>();
            for (Map<String, Object> item : currentOrderItems) {
                String productName = (String) item.get("name");
                Integer quantity = (Integer) item.get("quantity");
                orderContent.put(productName, quantity);
            }
            
            // Create order with default status 0 (waiting for payment)
            int orderId = helper.create_order(orderContent, (int) totalAmount, 0, 
                noteTextArea.getText().trim(), employeeId);
            
            if (orderId > 0) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Tạo đơn hàng thành công!\nMã đơn hàng: " + orderId, 
                    "Thành công", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                clearOrder();
                // Reload daily orders and stats
                loadDailyOrders();
                loadDailyStats();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi khi tạo đơn hàng!", 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.error("Error creating order", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi tạo đơn hàng: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String formatCurrency(int amount) {
        return CurrencyUtils.formatCurrency(amount);
    }
    
    private void loadDailyOrders() {
        // Show loading indicator
        dailyOrdersTable.setEnabled(false);
        
        try {
            // Execute database operation asynchronously
            DatabaseTaskManager.executeAsyncWithUIUpdate(
                () -> {
                    try {
                        return helper.getinfo("donhang");
                    } catch (Exception e) {
                        logger.error("Error loading daily orders", e);
                        throw new RuntimeException("Failed to load daily orders", e);
                    }
                },
                (orders) -> {
                    updateDailyOrdersTable(orders);
                }
            ).exceptionally(throwable -> {
                logger.error("Async daily orders loading failed, using sequential fallback", throwable);
                // Fallback to sequential loading
                javax.swing.SwingUtilities.invokeLater(() -> {
                    loadDailyOrdersSequential();
                });
                return null;
            });
        } catch (Exception e) {
            logger.error("Failed to start async daily orders loading, using sequential fallback", e);
            // Fallback to sequential loading
            loadDailyOrdersSequential();
        }
    }
    
    private void configureDailyOrdersTableSize() {
        // Set the table to show 8 rows
        int rowHeight = dailyOrdersTable.getRowHeight();
        if (rowHeight <= 0) {
            rowHeight = 20; // Default row height
        }
        
        // Calculate total height needed for 8 rows + header + buttons
        int headerHeight = 30; // Approximate header height
        int buttonHeight = 40; // Approximate button area height
        int totalHeight = headerHeight + buttonHeight + (rowHeight * 8) + 20; // 20 for padding
        
        // Set preferred viewport size for 8 rows
        dailyOrdersTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(dailyOrdersTable.getPreferredSize().width, 
                                 rowHeight * 8));
        
        // Force the scroll pane to update its size
        if (dailyOrdersScrollPane != null) {
            dailyOrdersScrollPane.setPreferredSize(
                new java.awt.Dimension(dailyOrdersScrollPane.getPreferredSize().width, 
                                     rowHeight * 8));
        }
        
        // Set the panel size to accommodate 8 rows
        if (dailyOrdersPanel != null) {
            dailyOrdersPanel.setPreferredSize(
                new java.awt.Dimension(dailyOrdersPanel.getPreferredSize().width, 
                                     totalHeight));
            dailyOrdersPanel.setMinimumSize(
                new java.awt.Dimension(dailyOrdersPanel.getMinimumSize().width, 
                                     totalHeight));
            
            // Force revalidation and repaint
            dailyOrdersPanel.revalidate();
            dailyOrdersPanel.repaint();
        }
        
        // Also force the main panel to revalidate
        this.revalidate();
        this.repaint();
    }
    
    private void loadDailyStats() {
        try {
            List<Object[]> orders = helper.getinfo("donhang");
            
            // Get today's date
            java.time.LocalDate today = java.time.LocalDate.now();
            
            int totalOrders = 0;
            int totalRevenue = 0;
            int paidOrders = 0;
            int pendingOrders = 0;
            int cancelledOrders = 0;
            
            for (Object[] order : orders) {
                if (order.length >= 8) {
                    // Check if order is from today (date is in order[6])
                    Object dateObj = order[6];
                    java.time.LocalDate orderDate = null;
                    
                    // Skip if date is null
                    if (dateObj == null) {
                        logger.warn("Order {} has null date, skipping", order[0]);
                        continue;
                    }
                    
                    if (dateObj instanceof java.sql.Timestamp) {
                        orderDate = ((java.sql.Timestamp) dateObj).toLocalDateTime().toLocalDate();
                    } else if (dateObj instanceof java.sql.Date) {
                        orderDate = ((java.sql.Date) dateObj).toLocalDate();
                    } else if (dateObj instanceof java.util.Date) {
                        orderDate = ((java.util.Date) dateObj).toInstant()
                            .atZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();
                    } else if (dateObj instanceof String) {
                        try {
                            orderDate = java.time.LocalDate.parse(dateObj.toString());
                        } catch (Exception e) {
                            logger.warn("Error parsing date: {}", dateObj, e);
                            continue;
                        }
                    }
                    
                    if (orderDate != null && orderDate.equals(today)) {
                        totalOrders++;
                        
                        int status = (Integer) order[4];
                        int amount = (Integer) order[3];
                        
                        switch (status) {
                            case 0: 
                                pendingOrders++; 
                                // Không tính vào doanh thu
                                break;
                            case 1: 
                                paidOrders++; 
                                totalRevenue += amount; // Chỉ tính đơn hàng đã thanh toán
                                break;
                            case -1: 
                                cancelledOrders++; 
                                // Không tính vào doanh thu
                                break;
                        }
                    }
                }
            }
            
            StringBuilder stats = new StringBuilder();
            stats.append("=== THỐNG KÊ BÁN HÀNG HÔM NAY ===\n");
            stats.append("Tổng số đơn hàng: ").append(totalOrders).append("\n");
            stats.append("Doanh thu (đã thanh toán): ").append(formatCurrency(totalRevenue)).append("\n");
            stats.append("Đã thanh toán: ").append(paidOrders).append("\n");
            stats.append("Chờ thanh toán: ").append(pendingOrders).append("\n");
            stats.append("Đã hủy: ").append(cancelledOrders).append("\n");
            stats.append("Tỷ lệ thanh toán: ").append(totalOrders > 0 ? String.format("%.1f%%", (paidOrders * 100.0 / totalOrders)) : "0%");
            
            dailyStatsTextArea.setText(stats.toString());
            
        } catch (Exception e) {
            logger.error("Error loading daily stats", e);
            dailyStatsTextArea.setText("Lỗi khi tải thống kê: " + e.getMessage());
        }
    }
    
    private void updateDailyOrderStatuses() {
        try {
            DefaultTableModel model = (DefaultTableModel) dailyOrdersTable.getModel();
            int updatedCount = 0;
            
            // Lưu trữ trạng thái ban đầu để so sánh
            Map<String, Integer> originalStatuses = new HashMap<>();
            List<Object[]> orders = helper.getinfo("donhang");
            
            // Lấy trạng thái ban đầu từ database
            for (Object[] order : orders) {
                if (order.length >= 8) {
                    String orderCode = (String) order[1];
                    Integer originalStatus = (Integer) order[4];
                    originalStatuses.put(orderCode, originalStatus);
                }
            }
            
            for (int i = 0; i < model.getRowCount(); i++) {
                String orderCode = (String) model.getValueAt(i, 1); // Order code is in column 1
                String statusText = (String) model.getValueAt(i, 3); // Status is in column 3
                
                int newStatus;
                switch (statusText) {
                    case "Chờ thanh toán": newStatus = 0; break;
                    case "Đã thanh toán": newStatus = 1; break;
                    case "Hủy": newStatus = -1; break;
                    default: continue;
                }
                
                // Chỉ update nếu trạng thái thay đổi
                Integer originalStatus = originalStatuses.get(orderCode);
                if (originalStatus != null && !originalStatus.equals(newStatus)) {
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("trangthai", newStatus);
                    boolean success = helper.update_order(orderCode, updateData);
                    if (success) {
                        updatedCount++;
                        logger.info("Updated order {} status from {} to {}", orderCode, originalStatus, newStatus);
                    }
                }
            }
            
            if (updatedCount > 0) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Đã cập nhật " + updatedCount + " đơn hàng có thay đổi!", 
                    "Thành công", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                // Auto-reload data to reflect changes
                loadDailyOrders();
                loadDailyStats();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Không có thay đổi nào để cập nhật!", 
                    "Thông báo", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.error("Error updating daily order statuses", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi cập nhật trạng thái: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showNewProductDialog() {
        javax.swing.JDialog dialog = new javax.swing.JDialog();
        dialog.setTitle("Thêm sản phẩm mới");
        dialog.setModal(true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        
        // Product name
        javax.swing.JLabel nameLabel = new javax.swing.JLabel("Tên sản phẩm:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        panel.add(nameLabel, gbc);
        
        javax.swing.JTextField nameField = new javax.swing.JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        
        // Product price
        javax.swing.JLabel priceLabel = new javax.swing.JLabel("Giá:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        panel.add(priceLabel, gbc);
        
        javax.swing.JTextField priceField = new javax.swing.JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panel.add(priceField, gbc);
        
        // Product ingredients
        javax.swing.JLabel ingredientsLabel = new javax.swing.JLabel("Nguyên liệu:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        panel.add(ingredientsLabel, gbc);
        
        javax.swing.JTextField ingredientsField = new javax.swing.JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        panel.add(ingredientsField, gbc);
        
        // Buttons
        javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
        javax.swing.JButton saveButton = new javax.swing.JButton("Lưu");
        javax.swing.JButton cancelButton = new javax.swing.JButton("Hủy");
        
        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                int price = Integer.parseInt(priceField.getText().trim());
                String ingredients = ingredientsField.getText().trim();
                
                if (name.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên sản phẩm!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (price <= 0) {
                    javax.swing.JOptionPane.showMessageDialog(dialog, "Giá sản phẩm phải lớn hơn 0!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (ingredients.isEmpty()) {
                    javax.swing.JOptionPane.showMessageDialog(dialog, "Vui lòng nhập nguyên liệu!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Add product to database using xuathang_add method
                boolean success = helper.xuathang_add(name, price, ingredients);
                if (success) {
                    javax.swing.JOptionPane.showMessageDialog(dialog, "Thêm sản phẩm thành công!", "Thành công", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    loadProductData(); // Reload product list
                    dialog.dispose();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm sản phẩm!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException ex) {
                javax.swing.JOptionPane.showMessageDialog(dialog, "Vui lòng nhập giá hợp lệ!", "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                logger.error("Error adding new product", ex);
                javax.swing.JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm sản phẩm: " + ex.getMessage(), "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        panel.add(buttonPanel, gbc);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private void showUpdateOrderStatusDialog() {
        try {
            // Get today's orders
            List<Object[]> orders = helper.getinfo("banhang");
            if (orders.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(this, "Không có đơn hàng nào trong ngày!", "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            javax.swing.JDialog dialog = new javax.swing.JDialog();
            dialog.setTitle("Cập nhật trạng thái đơn hàng");
            dialog.setModal(true);
            dialog.setSize(600, 400);
            dialog.setLocationRelativeTo(this);
            
            javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.BorderLayout());
            
            // Create table for orders
            String[] columnNames = {"ID", "Ngày tạo", "Tổng tiền", "Trạng thái", "Ghi chú"};
            DefaultTableModel orderModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 3; // Only status column is editable
                }
            };
            
            javax.swing.JTable orderTable = new javax.swing.JTable(orderModel);
            orderTable.getColumnModel().getColumn(3).setCellEditor(new javax.swing.DefaultCellEditor(new javax.swing.JComboBox<>(new String[]{"Chờ thanh toán", "Đã thanh toán", "Hủy"})));
            
            // Populate table
            for (Object[] order : orders) {
                if (order.length >= 8) {
                    int status = (Integer) order[4]; // status is at index 4
                    String statusText;
                    switch (status) {
                        case 0: statusText = "Chờ thanh toán"; break;
                        case 1: statusText = "Đã thanh toán"; break;
                        case -1: statusText = "Hủy"; break;
                        default: statusText = "Không xác định"; break;
                    }
                    
                    // Format date properly
                    String dateStr = "Không xác định";
                    Object dateObj = order[6];
                    if (dateObj == null) {
                        dateStr = "NULL";
                    } else if (dateObj instanceof java.sql.Timestamp) {
                        dateStr = ((java.sql.Timestamp) dateObj).toString();
                    } else if (dateObj instanceof java.sql.Date) {
                        dateStr = ((java.sql.Date) dateObj).toString();
                    } else if (dateObj instanceof java.util.Date) {
                        dateStr = ((java.util.Date) dateObj).toString();
                    } else {
                        dateStr = dateObj.toString();
                    }
                    
                    orderModel.addRow(new Object[]{
                        order[0], // ID
                        dateStr, // Date
                        formatCurrency((Integer) order[3]), // Total
                        statusText, // Status
                        order[7], // Note
                        order[1] // Order code (madonhang)
                    });
                }
            }
            
            javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(orderTable);
            panel.add(scrollPane, java.awt.BorderLayout.CENTER);
            
            // Buttons
            javax.swing.JPanel buttonPanel = new javax.swing.JPanel(new java.awt.FlowLayout());
            javax.swing.JButton saveButton = new javax.swing.JButton("Lưu thay đổi");
            javax.swing.JButton cancelButton = new javax.swing.JButton("Hủy");
            
            saveButton.addActionListener(e -> {
                try {
                    int updatedCount = 0;
                    for (int i = 0; i < orderModel.getRowCount(); i++) {
                        String orderCode = (String) orderModel.getValueAt(i, 5); // Get order code from column 5
                        String statusText = (String) orderModel.getValueAt(i, 3);
                        
                        int newStatus;
                        switch (statusText) {
                            case "Chờ thanh toán": newStatus = 0; break;
                            case "Đã thanh toán": newStatus = 1; break;
                            case "Hủy": newStatus = -1; break;
                            default: continue;
                        }
                        
                        // Update order status in database
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("trangthai", newStatus);
                        boolean success = helper.update_order(orderCode, updateData);
                        if (success) {
                            updatedCount++;
                        }
                    }
                    
                    javax.swing.JOptionPane.showMessageDialog(dialog, 
                        "Đã cập nhật " + updatedCount + " đơn hàng!", 
                        "Thành công", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    
                } catch (Exception ex) {
                    logger.error("Error updating order status", ex);
                    javax.swing.JOptionPane.showMessageDialog(dialog, 
                        "Lỗi khi cập nhật trạng thái: " + ex.getMessage(), 
                        "Lỗi", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            });
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            panel.add(buttonPanel, java.awt.BorderLayout.SOUTH);
            
            dialog.add(panel);
            dialog.setVisible(true);
            
        } catch (Exception e) {
            logger.error("Error showing update order status dialog", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải danh sách đơn hàng: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        formPanel = new javax.swing.JPanel();
        currentEmployeeLabel = new javax.swing.JLabel();
        noteLabel = new javax.swing.JLabel();
        noteScrollPane = new javax.swing.JScrollPane();
        noteTextArea = new javax.swing.JTextArea();
        createOrderButton = new javax.swing.JButton();
        clearOrderButton = new javax.swing.JButton();
        updateOrderStatusButton = new javax.swing.JButton();
        orderPanel = new javax.swing.JPanel();
        orderLabel = new javax.swing.JLabel();
        totalAmountLabel = new javax.swing.JLabel();
        removeProductButton = new javax.swing.JButton();
        orderScrollPane = new javax.swing.JScrollPane();
        orderTable = new javax.swing.JTable();
        productPanel = new javax.swing.JPanel();
        productLabel = new javax.swing.JLabel();
        quantityLabel = new javax.swing.JLabel();
        quantitySpinner = new javax.swing.JSpinner();
        addProductButton = new javax.swing.JButton();
        newProductButton = new javax.swing.JButton();
        productScrollPane = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        dailyOrdersPanel = new javax.swing.JPanel();
        dailyOrdersLabel = new javax.swing.JLabel();
        refreshOrdersButton = new javax.swing.JButton();
        updateDailyOrderStatusButton = new javax.swing.JButton();
        endSessionButton = new javax.swing.JButton();
        startNewSessionButton = new javax.swing.JButton();
        dailyOrdersTable = new javax.swing.JTable();
        dailyOrdersScrollPane = new javax.swing.JScrollPane();
        dailyStatsPanel = new javax.swing.JPanel();
        dailyStatsLabel = new javax.swing.JLabel();
        dailyStatsTextArea = new javax.swing.JTextArea();
        dailyStatsScrollPane = new javax.swing.JScrollPane();

        titleLabel.setText("QUẢN LÝ BÁN HÀNG");

        formPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        currentEmployeeLabel.setText("Chưa có nhân viên ca làm việc");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        formPanel.add(currentEmployeeLabel, gbc);

        noteLabel.setText("Ghi chú:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(noteLabel, gbc);

        noteTextArea.setColumns(20);
        noteTextArea.setRows(3);
        noteScrollPane.setViewportView(noteTextArea);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        formPanel.add(noteScrollPane, gbc);

        createOrderButton.setText("Tạo đơn hàng");
        createOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createOrderButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        formPanel.add(createOrderButton, gbc);

        clearOrderButton.setText("Làm mới");
        clearOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearOrderButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        formPanel.add(clearOrderButton, gbc);

        updateOrderStatusButton.setText("Cập nhật trạng thái đơn hàng");
        updateOrderStatusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateOrderStatusButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        formPanel.add(updateOrderStatusButton, gbc);

        orderPanel.setLayout(new java.awt.BorderLayout());

        orderLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        orderLabel.setText("Đơn hàng hiện tại");

        totalAmountLabel.setText("Tổng tiền: 0 VNĐ");

        removeProductButton.setText("Xóa sản phẩm");
        removeProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeProductButtonActionPerformed(evt);
            }
        });

        javax.swing.JPanel orderTopPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        orderTopPanel.add(orderLabel);
        orderTopPanel.add(totalAmountLabel);
        orderTopPanel.add(removeProductButton);
        orderPanel.add(orderTopPanel, java.awt.BorderLayout.NORTH);

        orderTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên sản phẩm", "Giá", "Số lượng", "Thành tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        orderScrollPane.setViewportView(orderTable);

        orderPanel.add(orderScrollPane, java.awt.BorderLayout.CENTER);

        // Daily orders panel
        dailyOrdersPanel = new javax.swing.JPanel();
        dailyOrdersPanel.setLayout(new java.awt.BorderLayout());
        
        dailyOrdersLabel = new javax.swing.JLabel();
        dailyOrdersLabel.setFont(new java.awt.Font("Arial", 1, 16));
        dailyOrdersLabel.setText("Đơn hàng trong ngày");
        
        refreshOrdersButton = new javax.swing.JButton();
        refreshOrdersButton.setText("Làm mới");
        refreshOrdersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshOrdersButtonActionPerformed(evt);
            }
        });
        
        updateDailyOrderStatusButton = new javax.swing.JButton();
        updateDailyOrderStatusButton.setText("Cập nhật trạng thái");
        updateDailyOrderStatusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateDailyOrderStatusButtonActionPerformed(evt);
            }
        });
        
        endSessionButton = new javax.swing.JButton();
        endSessionButton.setText("Kết thúc ca");
        endSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endSessionButtonActionPerformed(evt);
            }
        });
        
        startNewSessionButton = new javax.swing.JButton();
        startNewSessionButton.setText("Bắt đầu ca mới");
        startNewSessionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startNewSessionButtonActionPerformed(evt);
            }
        });
        
        javax.swing.JPanel dailyOrdersTopPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        dailyOrdersTopPanel.add(dailyOrdersLabel);
        dailyOrdersTopPanel.add(refreshOrdersButton);
        dailyOrdersTopPanel.add(updateDailyOrderStatusButton);
        dailyOrdersTopPanel.add(endSessionButton);
        dailyOrdersTopPanel.add(startNewSessionButton);
        dailyOrdersPanel.add(dailyOrdersTopPanel, java.awt.BorderLayout.NORTH);
        
        dailyOrdersTable = new javax.swing.JTable();
        dailyOrdersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Mã đơn", "Tổng tiền", "Trạng thái", "Nhân viên", "Ghi chú"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dailyOrdersTable.getColumnModel().getColumn(3).setCellEditor(new javax.swing.DefaultCellEditor(new javax.swing.JComboBox<>(new String[]{"Chờ thanh toán", "Đã thanh toán", "Hủy"})));
        
        dailyOrdersScrollPane = new javax.swing.JScrollPane();
        dailyOrdersScrollPane.setViewportView(dailyOrdersTable);
        dailyOrdersPanel.add(dailyOrdersScrollPane, java.awt.BorderLayout.CENTER);
        
        // Daily stats panel
        dailyStatsPanel = new javax.swing.JPanel();
        dailyStatsPanel.setLayout(new java.awt.BorderLayout());
        
        dailyStatsLabel = new javax.swing.JLabel();
        dailyStatsLabel.setFont(new java.awt.Font("Arial", 1, 16));
        dailyStatsLabel.setText("Thống kê bán hàng hôm nay");
        
        dailyStatsTextArea = new javax.swing.JTextArea();
        dailyStatsTextArea.setEditable(false);
        dailyStatsTextArea.setRows(5);
        dailyStatsScrollPane = new javax.swing.JScrollPane();
        dailyStatsScrollPane.setViewportView(dailyStatsTextArea);
        
        dailyStatsPanel.add(dailyStatsLabel, java.awt.BorderLayout.NORTH);
        dailyStatsPanel.add(dailyStatsScrollPane, java.awt.BorderLayout.CENTER);

        productPanel.setLayout(new java.awt.BorderLayout());

        productLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        productLabel.setText("Danh sách sản phẩm");

        quantityLabel.setText("Số lượng:");

        quantitySpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 999, 1));

        addProductButton.setText("Thêm vào đơn hàng");
        addProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductButtonActionPerformed(evt);
            }
        });

        newProductButton.setText("Thêm sản phẩm mới");
        newProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProductButtonActionPerformed(evt);
            }
        });

        javax.swing.JPanel productTopPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        
        // Top row with label
        javax.swing.JPanel labelPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        labelPanel.add(productLabel);
        
        // Bottom row with controls and buttons
        javax.swing.JPanel controlPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        controlPanel.add(quantityLabel);
        controlPanel.add(quantitySpinner);
        controlPanel.add(addProductButton);
        controlPanel.add(newProductButton);
        
        productTopPanel.add(labelPanel, java.awt.BorderLayout.NORTH);
        productTopPanel.add(controlPanel, java.awt.BorderLayout.CENTER);
        
        productPanel.add(productTopPanel, java.awt.BorderLayout.NORTH);

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tên sản phẩm", "Giá", "Nguyên liệu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        productScrollPane.setViewportView(productTable);

        productPanel.add(productScrollPane, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addComponent(formPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(productPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(orderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dailyOrdersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(dailyStatsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addGap(18, 18, 18)
                .addComponent(formPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(productPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(orderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(dailyOrdersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(dailyStatsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductButtonActionPerformed
        addProductToOrder();
    }//GEN-LAST:event_addProductButtonActionPerformed

    private void removeProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeProductButtonActionPerformed
        removeProductFromOrder();
    }//GEN-LAST:event_removeProductButtonActionPerformed

    private void createOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createOrderButtonActionPerformed
        createOrder();
    }//GEN-LAST:event_createOrderButtonActionPerformed

    private void clearOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearOrderButtonActionPerformed
        clearOrder();
    }//GEN-LAST:event_clearOrderButtonActionPerformed

    private void newProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProductButtonActionPerformed
        showNewProductDialog();
    }//GEN-LAST:event_newProductButtonActionPerformed

    private void updateOrderStatusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateOrderStatusButtonActionPerformed
        showUpdateOrderStatusDialog();
    }//GEN-LAST:event_updateOrderStatusButtonActionPerformed

    private void refreshOrdersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshOrdersButtonActionPerformed
        loadDailyOrders();
        loadDailyStats();
    }//GEN-LAST:event_refreshOrdersButtonActionPerformed

    private void updateDailyOrderStatusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateDailyOrderStatusButtonActionPerformed
        updateDailyOrderStatuses();
    }//GEN-LAST:event_updateDailyOrderStatusButtonActionPerformed

    private void endSessionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endSessionButtonActionPerformed
        endSession();
    }//GEN-LAST:event_endSessionButtonActionPerformed

    private void startNewSessionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startNewSessionButtonActionPerformed
        startNewSession();
    }//GEN-LAST:event_startNewSessionButtonActionPerformed


    /**
     * Load all data asynchronously in parallel for better performance
     */
    private void loadAllDataAsync() {
        try {
            // Load product data async
            DatabaseTaskManager.executeAsyncWithUIUpdate(
                () -> {
                    try {
                        return helper.getinfo("xuathang");
                    } catch (Exception e) {
                        logger.error("Error loading product data", e);
                        throw new RuntimeException("Failed to load product data", e);
                    }
                },
                (result) -> {
                    try {
                        @SuppressWarnings("unchecked")
                        List<Object[]> products = (List<Object[]>) result;
                        updateProductTable(products);
                        logger.info("Product data loaded successfully");
                    } catch (Exception e) {
                        logger.error("Error updating product table", e);
                    }
                }
            );
            
            // Load daily orders async
            DatabaseTaskManager.executeAsyncWithUIUpdate(
                () -> {
                    try {
                        return helper.getinfo("donhang");
                    } catch (Exception e) {
                        logger.error("Error loading daily orders", e);
                        throw new RuntimeException("Failed to load daily orders", e);
                    }
                },
                (result) -> {
                    try {
                        @SuppressWarnings("unchecked")
                        List<Object[]> orders = (List<Object[]>) result;
                        updateDailyOrdersTable(orders);
                        // Load daily stats after orders are loaded
                        loadDailyStats();
                        logger.info("Daily orders data loaded successfully");
                    } catch (Exception e) {
                        logger.error("Error updating daily orders table", e);
                    }
                }
            );
            
        } catch (Exception e) {
            logger.error("Failed to start async loading, using sequential fallback", e);
            // Fallback to sequential loading
            loadProductDataSequential();
            loadDailyOrdersSequential();
            loadDailyStats();
        }
    }
    
    /**
     * Fallback method for sequential product data loading
     */
    private void loadProductDataSequential() {
        try {
            List<Object[]> products = helper.getinfo("xuathang");
            updateProductTable(products);
            logger.info("Product data loaded sequentially: {} products", products.size());
        } catch (Exception e) {
            logger.error("Error loading product data sequentially", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu sản phẩm: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Fallback method for sequential daily orders loading
     */
    private void loadDailyOrdersSequential() {
        try {
            List<Object[]> orders = helper.getinfo("donhang");
            updateDailyOrdersTable(orders);
            logger.info("Daily orders loaded sequentially: {} orders", orders.size());
        } catch (Exception e) {
            logger.error("Error loading daily orders sequentially", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải đơn hàng trong ngày: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update product table with loaded data
     */
    private void updateProductTable(List<Object[]> products) {
        try {
            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            model.setRowCount(0);
            
            for (Object[] product : products) {
                if (product.length >= 4) {
                    try {
                        int price = (Integer) product[2];
                        String nguyenlieu = (String) product[3];
                        
                        model.addRow(new Object[]{
                            product[1], // name
                            formatCurrency(price), // price
                            nguyenlieu // nguyên liệu
                        });
                    } catch (Exception e) {
                        logger.error("Error processing product: {}", product[1], e);
                        model.addRow(new Object[]{
                            product[1], // name
                            "Lỗi", // price
                            "Lỗi" // nguyên liệu
                        });
                    }
                }
            }
            
            productTable.setEnabled(true);
            logger.info("Product table updated: {} products", products.size());
            
        } catch (Exception e) {
            logger.error("Error updating product table", e);
            productTable.setEnabled(true);
        }
    }
    
    /**
     * Update daily orders table with loaded data
     */
    private void updateDailyOrdersTable(List<Object[]> orders) {
        try {
            DefaultTableModel model = (DefaultTableModel) dailyOrdersTable.getModel();
            model.setRowCount(0);
            
            // Get today's date
            java.time.LocalDate today = java.time.LocalDate.now();
            
            for (Object[] order : orders) {
                if (order.length >= 8) {
                    // Check if order is from today (date is in order[6])
                    Object dateObj = order[6];
                    java.time.LocalDate orderDate = null;
                    
                    // Skip if date is null
                    if (dateObj == null) {
                        logger.warn("Order {} has null date, skipping", order[0]);
                        continue;
                    }
                    
                    if (dateObj instanceof java.sql.Timestamp) {
                        orderDate = ((java.sql.Timestamp) dateObj).toLocalDateTime().toLocalDate();
                    } else if (dateObj instanceof java.sql.Date) {
                        orderDate = ((java.sql.Date) dateObj).toLocalDate();
                    } else if (dateObj instanceof java.util.Date) {
                        orderDate = ((java.util.Date) dateObj).toInstant()
                            .atZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();
                    } else if (dateObj instanceof String) {
                        try {
                            orderDate = java.time.LocalDate.parse(dateObj.toString());
                        } catch (Exception e) {
                            logger.warn("Error parsing date: {}", dateObj, e);
                            continue;
                        }
                    }
                    
                    if (orderDate != null && orderDate.equals(today)) {
                        int status = (Integer) order[4];
                        String statusText;
                        switch (status) {
                            case 0: statusText = "Chờ thanh toán"; break;
                            case 1: statusText = "Đã thanh toán"; break;
                            case -1: statusText = "Hủy"; break;
                            default: statusText = "Không xác định"; break;
                        }
                        
                        // Get employee name
                        String employeeName = "Không xác định";
                        try {
                            Map<String, String> employees = helper.nhanvien_list();
                            String empId = order[5].toString();
                            employeeName = employees.getOrDefault(empId, "Không xác định");
                        } catch (Exception e) {
                            logger.warn("Error getting employee name", e);
                        }
                        
                        model.addRow(new Object[]{
                            order[0], // ID
                            order[1], // Order code (madonhang)
                            formatCurrency((Integer) order[3]), // Total amount
                            statusText, // Status
                            employeeName, // Employee name
                            order[7] // Note
                        });
                    }
                }
            }
            
            // Ensure table shows 8 rows even if there are fewer orders
            configureDailyOrdersTableSize();
            
            // Add empty rows to make it show 8 rows total
            int currentRowCount = model.getRowCount();
            int targetRowCount = 8;
            for (int i = currentRowCount; i < targetRowCount; i++) {
                model.addRow(new Object[]{"", "", "", "", "", ""});
            }
            
            dailyOrdersTable.setEnabled(true);
            logger.info("Daily orders table updated: {} orders", orders.size());
            
        } catch (Exception e) {
            logger.error("Error updating daily orders table", e);
            dailyOrdersTable.setEnabled(true);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addProductButton;
    private javax.swing.JButton clearOrderButton;
    private javax.swing.JButton createOrderButton;
    private javax.swing.JPanel dailyOrdersPanel;
    private javax.swing.JLabel dailyOrdersLabel;
    private javax.swing.JScrollPane dailyOrdersScrollPane;
    private javax.swing.JTable dailyOrdersTable;
    private javax.swing.JPanel dailyStatsPanel;
    private javax.swing.JLabel dailyStatsLabel;
    private javax.swing.JScrollPane dailyStatsScrollPane;
    private javax.swing.JTextArea dailyStatsTextArea;
    private javax.swing.JLabel currentEmployeeLabel;
    private javax.swing.JPanel formPanel;
    private javax.swing.JButton newProductButton;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JScrollPane noteScrollPane;
    private javax.swing.JTextArea noteTextArea;
    private javax.swing.JPanel orderPanel;
    private javax.swing.JLabel orderLabel;
    private javax.swing.JScrollPane orderScrollPane;
    private javax.swing.JTable orderTable;
    private javax.swing.JPanel productPanel;
    private javax.swing.JLabel productLabel;
    private javax.swing.JScrollPane productScrollPane;
    private javax.swing.JTable productTable;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JSpinner quantitySpinner;
    private javax.swing.JButton refreshOrdersButton;
    private javax.swing.JButton removeProductButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel totalAmountLabel;
    private javax.swing.JButton updateDailyOrderStatusButton;
    private javax.swing.JButton updateOrderStatusButton;
    private javax.swing.JButton endSessionButton;
    private javax.swing.JButton startNewSessionButton;
    // End of variables declaration//GEN-END:variables
}