/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package baitaplon.view;

import baitaplon.backend.helper;
import baitaplon.backend.ihelper;
import baitaplon.utils.CurrencyUtils;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author HI
 */
public class thongke extends javax.swing.JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(thongke.class);
    private ihelper helper = new helper();
    private java.time.LocalDate filterStartDate;
    private java.time.LocalDate filterEndDate;

    /**
     * Creates new form thongke
     */
    public thongke() {
        initComponents();
        setupUI();
        initializeDateFilter();
        loadData();
    }
    
    private void setupUI() {
        // Set background
        setBackground(new java.awt.Color(248, 249, 250));
        
        // Style title
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(new java.awt.Color(70, 130, 180));
        
        // Style cards
        styleCard(totalOrdersCard, "Tổng đơn hàng", "0", new java.awt.Color(52, 152, 219));
        styleCard(totalRevenueCard, "Doanh thu", "0 VNĐ", new java.awt.Color(46, 204, 113));
        styleCard(totalExpenseCard, "Chi tiêu nhập hàng", "0 VNĐ", new java.awt.Color(231, 76, 60));
        styleCard(totalProfitCard, "Lãi cuối cùng", "0 VNĐ", new java.awt.Color(155, 89, 182));
        styleCard(totalInventoryCard, "Tồn kho", "0 sản phẩm", new java.awt.Color(241, 196, 15));
        styleCard(totalEmployeesCard, "Nhân viên", "0 người", new java.awt.Color(52, 73, 94));
        
        // Style refresh button
        refreshButton.setBackground(new java.awt.Color(52, 152, 219));
        refreshButton.setForeground(java.awt.Color.WHITE);
        refreshButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        // Style table
        recentOrdersTable.getTableHeader().setBackground(new java.awt.Color(70, 130, 180));
        recentOrdersTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        recentOrdersTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        // Set table to show more rows
        int rowHeight = recentOrdersTable.getRowHeight();
        if (rowHeight <= 0) {
            rowHeight = 20; // Default row height
        }
        recentOrdersTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(recentOrdersTable.getPreferredSize().width, 
                                 rowHeight * 15)); // Show 15 rows
        
        // Style filter components
        timeFilterComboBox.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        startDatePicker.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        endDatePicker.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        applyFilterButton.setBackground(new java.awt.Color(52, 152, 219));
        applyFilterButton.setForeground(java.awt.Color.WHITE);
        applyFilterButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
    }
    
    private void initializeDateFilter() {
        // Set default to today
        java.time.LocalDate today = java.time.LocalDate.now();
        filterStartDate = today;
        filterEndDate = today;
        
        // Initialize date pickers
        startDatePicker.setDate(java.sql.Date.valueOf(today));
        endDatePicker.setDate(java.sql.Date.valueOf(today));
        
        // Set default filter
        timeFilterComboBox.setSelectedItem("Hôm nay");
    }
    
    private void styleCard(javax.swing.JPanel card, String title, String value, java.awt.Color color) {
        card.setBackground(color);
        card.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(color.darker(), 1),
            javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Find labels in card and style them
        for (java.awt.Component comp : card.getComponents()) {
            if (comp instanceof javax.swing.JLabel) {
                javax.swing.JLabel label = (javax.swing.JLabel) comp;
                if (label.getText().equals(title)) {
                    label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
                    label.setForeground(java.awt.Color.WHITE);
                } else {
                    label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 18));
                    label.setForeground(java.awt.Color.WHITE);
                }
            }
        }
    }
    
    private void loadData() {
        try {
            // Load statistics
            loadStatistics();
            
            // Load recent orders
            loadRecentOrders();
            
        } catch (Exception e) {
            logger.error("Error loading data", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadStatistics() {
        try {
            // Get total orders
            List<Object[]> orders = helper.getinfo("donhang");
            int totalOrders = 0;
            
            // Calculate total revenue (only paid orders - status = 1)
            int totalRevenue = 0;
            for (Object[] order : orders) {
                if (order.length >= 8 && order[3] != null && order[4] != null && order[6] != null) {
                    // Check if order is within date range
                    Object dateObj = order[6]; // date column
                    java.time.LocalDate orderDate = null;
                    
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
                    
                    if (isDateInRange(orderDate)) {
                        totalOrders++;
                        
                        int status = (Integer) order[4]; // trangthai column
                        if (status == 1) { // Only count paid orders
                            totalRevenue += (Integer) order[3]; // giatien column
                        }
                    }
                }
            }
            
            // Calculate total expenses from nhaphang
            List<Object[]> inventory = helper.getinfo("nhaphang");
            int totalExpense = 0;
            int totalInventory = 0;
            for (Object[] item : inventory) {
                if (item.length >= 4 && item[2] != null && item[3] != null) {
                    int price = (Integer) item[2]; // price column
                    int count = (Integer) item[3]; // count column
                    totalExpense += price * count; // total cost for this item
                    totalInventory += count; // total inventory count
                }
            }
            
            // Calculate profit (revenue - expenses)
            int totalProfit = totalRevenue - totalExpense;
            
            // Get total employees
            List<Object[]> employees = helper.getinfo("nhanvien");
            int totalEmployees = employees.size();
            
            // Update cards
            updateCardValue(totalOrdersCard, String.valueOf(totalOrders));
            updateCardValue(totalRevenueCard, formatCurrency(totalRevenue));
            updateCardValue(totalExpenseCard, formatCurrency(totalExpense));
            updateCardValue(totalProfitCard, formatCurrency(totalProfit));
            updateCardValue(totalInventoryCard, String.valueOf(totalInventory));
            updateCardValue(totalEmployeesCard, String.valueOf(totalEmployees));
            
        } catch (Exception e) {
            logger.error("Error loading statistics", e);
        }
    }
    
    private void updateCardValue(javax.swing.JPanel card, String value) {
        for (java.awt.Component comp : card.getComponents()) {
            if (comp instanceof javax.swing.JLabel) {
                javax.swing.JLabel label = (javax.swing.JLabel) comp;
                if (!label.getText().contains("Tổng") && !label.getText().contains("Doanh") && 
                    !label.getText().contains("Chi tiêu") && !label.getText().contains("Lãi") &&
                    !label.getText().contains("Tồn") && !label.getText().contains("Nhân")) {
                    label.setText(value);
                }
            }
        }
    }
    
    private void loadRecentOrders() {
        try {
            List<Object[]> orders = helper.getinfo("donhang");
            
            // Filter orders by date range and sort by ID descending
            List<Object[]> filteredOrders = new ArrayList<>();
            for (Object[] order : orders) {
                if (order.length >= 8 && order[6] != null) {
                    Object dateObj = order[6]; // date column
                    java.time.LocalDate orderDate = null;
                    
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
                    
                    if (isDateInRange(orderDate)) {
                        filteredOrders.add(order);
                    }
                }
            }
            
            // Sort by ID descending to get recent orders
            filteredOrders.sort((a, b) -> {
                Integer idA = (Integer) a[0];
                Integer idB = (Integer) b[0];
                return idB.compareTo(idA);
            });
            
            // Show all orders (no limit)
            // If you want to limit, you can uncomment the lines below
            // if (filteredOrders.size() > 50) {
            //     filteredOrders = filteredOrders.subList(0, 50);
            // }
            
            DefaultTableModel model = (DefaultTableModel) recentOrdersTable.getModel();
            model.setRowCount(0);
            
            for (Object[] order : filteredOrders) {
                if (order.length >= 8) {
                    String status = getOrderStatus((Integer) order[4]); // trangthai
                    
                    // Format the actual date from the order
                    Object dateObj = order[6];
                    String formattedDate = "N/A";
                    if (dateObj != null) {
                        if (dateObj instanceof java.sql.Timestamp) {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                            formattedDate = sdf.format((java.sql.Timestamp) dateObj);
                        } else if (dateObj instanceof java.sql.Date) {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                            formattedDate = sdf.format((java.sql.Date) dateObj);
                        } else if (dateObj instanceof java.util.Date) {
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                            formattedDate = sdf.format((java.util.Date) dateObj);
                        }
                    }
                    
                    model.addRow(new Object[]{
                        order[1], // madonhang
                        formatCurrency((Integer) order[3]), // giatien
                        status,
                        order[7] != null ? order[7] : "", // ghichu (column 7)
                        formattedDate
                    });
                }
            }
            
            // Configure table size after loading data
            configureRecentOrdersTableSize();
            
        } catch (Exception e) {
            logger.error("Error loading recent orders", e);
        }
    }
    
    private void configureRecentOrdersTableSize() {
        // Set the table to show more rows
        int rowHeight = recentOrdersTable.getRowHeight();
        if (rowHeight <= 0) {
            rowHeight = 20; // Default row height
        }
        
        // Calculate number of rows to show (minimum 15, or actual data size)
        DefaultTableModel model = (DefaultTableModel) recentOrdersTable.getModel();
        int dataRows = model.getRowCount();
        int rowsToShow = Math.max(15, Math.min(dataRows, 30)); // Show 15-30 rows
        
        recentOrdersTable.setPreferredScrollableViewportSize(
            new java.awt.Dimension(recentOrdersTable.getPreferredSize().width, 
                                 rowHeight * rowsToShow));
        
        // Force the scroll pane to update its size
        if (recentOrdersScrollPane != null) {
            recentOrdersScrollPane.setPreferredSize(
                new java.awt.Dimension(recentOrdersScrollPane.getPreferredSize().width, 
                                     rowHeight * rowsToShow));
        }
        
        // Force revalidation and repaint
        recentOrdersPanel.revalidate();
        recentOrdersPanel.repaint();
    }
    
    private String getOrderStatus(int status) {
        switch (status) {
            case 0: return "Đang chờ";
            case 1: return "Hoàn thành";
            case 2: return "Đã hủy";
            default: return "Không xác định";
        }
    }
    
    private String formatCurrency(int amount) {
        return CurrencyUtils.formatCurrency(amount);
    }
    
    private void applyTimeFilter() {
        String selectedFilter = (String) timeFilterComboBox.getSelectedItem();
        java.time.LocalDate today = java.time.LocalDate.now();
        
        switch (selectedFilter) {
            case "Hôm nay":
                filterStartDate = today;
                filterEndDate = today;
                break;
            case "Tuần này":
                filterStartDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
                filterEndDate = today;
                break;
            case "Tháng này":
                filterStartDate = today.withDayOfMonth(1);
                filterEndDate = today;
                break;
            case "Tùy chỉnh":
                // Use date picker values
                filterStartDate = startDatePicker.getDate().toInstant()
                    .atZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();
                filterEndDate = endDatePicker.getDate().toInstant()
                    .atZone(java.time.ZoneId.of("Asia/Ho_Chi_Minh")).toLocalDate();
                break;
        }
        
        // Update date pickers
        startDatePicker.setDate(java.sql.Date.valueOf(filterStartDate));
        endDatePicker.setDate(java.sql.Date.valueOf(filterEndDate));
        
        // Reload data with new filter
        loadData();
    }
    
    private boolean isDateInRange(java.time.LocalDate date) {
        if (date == null) return false;
        return !date.isBefore(filterStartDate) && !date.isAfter(filterEndDate);
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
        statisticsPanel = new javax.swing.JPanel();
        totalOrdersCard = new javax.swing.JPanel();
        totalOrdersLabel = new javax.swing.JLabel();
        totalOrdersValue = new javax.swing.JLabel();
        totalRevenueCard = new javax.swing.JPanel();
        totalRevenueLabel = new javax.swing.JLabel();
        totalRevenueValue = new javax.swing.JLabel();
        totalExpenseCard = new javax.swing.JPanel();
        totalExpenseLabel = new javax.swing.JLabel();
        totalExpenseValue = new javax.swing.JLabel();
        totalProfitCard = new javax.swing.JPanel();
        totalProfitLabel = new javax.swing.JLabel();
        totalProfitValue = new javax.swing.JLabel();
        totalInventoryCard = new javax.swing.JPanel();
        totalInventoryLabel = new javax.swing.JLabel();
        totalInventoryValue = new javax.swing.JLabel();
        totalEmployeesCard = new javax.swing.JPanel();
        totalEmployeesLabel = new javax.swing.JLabel();
        totalEmployeesValue = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        filterPanel = new javax.swing.JPanel();
        filterLabel = new javax.swing.JLabel();
        timeFilterComboBox = new javax.swing.JComboBox<>();
        startDatePicker = new com.toedter.calendar.JDateChooser();
        endDatePicker = new com.toedter.calendar.JDateChooser();
        applyFilterButton = new javax.swing.JButton();
        recentOrdersPanel = new javax.swing.JPanel();
        recentOrdersLabel = new javax.swing.JLabel();
        recentOrdersScrollPane = new javax.swing.JScrollPane();
        recentOrdersTable = new javax.swing.JTable();

        titleLabel.setText("THỐNG KÊ & BÁO CÁO");

        statisticsPanel.setLayout(new java.awt.GridLayout(3, 2, 15, 15));

        totalOrdersCard.setLayout(new java.awt.BorderLayout());

        totalOrdersLabel.setText("Tổng đơn hàng");
        totalOrdersCard.add(totalOrdersLabel, java.awt.BorderLayout.NORTH);

        totalOrdersValue.setText("0");
        totalOrdersValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalOrdersCard.add(totalOrdersValue, java.awt.BorderLayout.CENTER);

        statisticsPanel.add(totalOrdersCard);

        totalRevenueCard.setLayout(new java.awt.BorderLayout());

        totalRevenueLabel.setText("Doanh thu");
        totalRevenueCard.add(totalRevenueLabel, java.awt.BorderLayout.NORTH);

        totalRevenueValue.setText("0 VNĐ");
        totalRevenueValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalRevenueCard.add(totalRevenueValue, java.awt.BorderLayout.CENTER);

        statisticsPanel.add(totalRevenueCard);

        totalExpenseCard.setLayout(new java.awt.BorderLayout());

        totalExpenseLabel.setText("Chi tiêu nhập hàng");
        totalExpenseCard.add(totalExpenseLabel, java.awt.BorderLayout.NORTH);

        totalExpenseValue.setText("0 VNĐ");
        totalExpenseValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalExpenseCard.add(totalExpenseValue, java.awt.BorderLayout.CENTER);

        statisticsPanel.add(totalExpenseCard);

        totalProfitCard.setLayout(new java.awt.BorderLayout());

        totalProfitLabel.setText("Lãi cuối cùng");
        totalProfitCard.add(totalProfitLabel, java.awt.BorderLayout.NORTH);

        totalProfitValue.setText("0 VNĐ");
        totalProfitValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalProfitCard.add(totalProfitValue, java.awt.BorderLayout.CENTER);

        statisticsPanel.add(totalProfitCard);

        totalInventoryCard.setLayout(new java.awt.BorderLayout());

        totalInventoryLabel.setText("Tồn kho");
        totalInventoryCard.add(totalInventoryLabel, java.awt.BorderLayout.NORTH);

        totalInventoryValue.setText("0 sản phẩm");
        totalInventoryValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalInventoryCard.add(totalInventoryValue, java.awt.BorderLayout.CENTER);

        statisticsPanel.add(totalInventoryCard);

        totalEmployeesCard.setLayout(new java.awt.BorderLayout());

        totalEmployeesLabel.setText("Nhân viên");
        totalEmployeesCard.add(totalEmployeesLabel, java.awt.BorderLayout.NORTH);

        totalEmployeesValue.setText("0 người");
        totalEmployeesValue.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalEmployeesCard.add(totalEmployeesValue, java.awt.BorderLayout.CENTER);

        statisticsPanel.add(totalEmployeesCard);

        refreshButton.setText("Làm mới");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        filterPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(new java.awt.Color(248, 249, 250));
        filterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Bộ lọc thời gian"));

        filterLabel.setText("Khoảng thời gian:");
        filterPanel.add(filterLabel);

        timeFilterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hôm nay", "Tuần này", "Tháng này", "Tùy chỉnh" }));
        timeFilterComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeFilterComboBoxActionPerformed(evt);
            }
        });
        filterPanel.add(timeFilterComboBox);

        startDatePicker.setDateFormatString("dd/MM/yyyy");
        startDatePicker.setEnabled(false);
        filterPanel.add(startDatePicker);

        endDatePicker.setDateFormatString("dd/MM/yyyy");
        endDatePicker.setEnabled(false);
        filterPanel.add(endDatePicker);

        applyFilterButton.setText("Áp dụng");
        applyFilterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyFilterButtonActionPerformed(evt);
            }
        });
        filterPanel.add(applyFilterButton);

        recentOrdersPanel.setLayout(new java.awt.BorderLayout());

        recentOrdersLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        recentOrdersLabel.setText("Đơn hàng gần đây");

        recentOrdersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã đơn hàng", "Giá tiền", "Trạng thái", "Ghi chú", "Thời gian"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        recentOrdersScrollPane.setViewportView(recentOrdersTable);

        recentOrdersPanel.add(recentOrdersLabel, java.awt.BorderLayout.NORTH);
        recentOrdersPanel.add(recentOrdersScrollPane, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(titleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refreshButton))
                    .addComponent(statisticsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
                    .addComponent(filterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(recentOrdersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(titleLabel)
                    .addComponent(refreshButton))
                .addGap(18, 18, 18)
                .addComponent(statisticsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(recentOrdersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        loadData();
        javax.swing.JOptionPane.showMessageDialog(this, "Dữ liệu đã được làm mới!", 
            "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void timeFilterComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeFilterComboBoxActionPerformed
        String selectedFilter = (String) timeFilterComboBox.getSelectedItem();
        boolean isCustom = "Tùy chỉnh".equals(selectedFilter);
        
        startDatePicker.setEnabled(isCustom);
        endDatePicker.setEnabled(isCustom);
        
        if (!isCustom) {
            applyTimeFilter();
        }
    }//GEN-LAST:event_timeFilterComboBoxActionPerformed

    private void applyFilterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyFilterButtonActionPerformed
        applyTimeFilter();
    }//GEN-LAST:event_applyFilterButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton applyFilterButton;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JLabel filterLabel;
    private javax.swing.JComboBox<String> timeFilterComboBox;
    private com.toedter.calendar.JDateChooser startDatePicker;
    private com.toedter.calendar.JDateChooser endDatePicker;
    private javax.swing.JPanel recentOrdersPanel;
    private javax.swing.JLabel recentOrdersLabel;
    private javax.swing.JScrollPane recentOrdersScrollPane;
    private javax.swing.JTable recentOrdersTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JPanel statisticsPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel totalEmployeesCard;
    private javax.swing.JLabel totalEmployeesLabel;
    private javax.swing.JLabel totalEmployeesValue;
    private javax.swing.JPanel totalExpenseCard;
    private javax.swing.JLabel totalExpenseLabel;
    private javax.swing.JLabel totalExpenseValue;
    private javax.swing.JPanel totalInventoryCard;
    private javax.swing.JLabel totalInventoryLabel;
    private javax.swing.JLabel totalInventoryValue;
    private javax.swing.JPanel totalOrdersCard;
    private javax.swing.JLabel totalOrdersLabel;
    private javax.swing.JLabel totalOrdersValue;
    private javax.swing.JPanel totalProfitCard;
    private javax.swing.JLabel totalProfitLabel;
    private javax.swing.JLabel totalProfitValue;
    private javax.swing.JPanel totalRevenueCard;
    private javax.swing.JLabel totalRevenueLabel;
    private javax.swing.JLabel totalRevenueValue;
    // End of variables declaration//GEN-END:variables
}