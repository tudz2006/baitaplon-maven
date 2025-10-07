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
public class lichsu extends javax.swing.JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(lichsu.class);
    private ihelper helper = new helper();

    /**
     * Creates new form lichsu
     */
    public lichsu() {
        initComponents();
        setupUI();
        loadHistoryData();
    }
    
    private void setupUI() {
        // Set background
        setBackground(new java.awt.Color(248, 249, 250));
        
        // Style title
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(new java.awt.Color(70, 130, 180));
        
        // Style filter panel
        filterPanel.setBackground(java.awt.Color.WHITE);
        filterPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Style buttons
        searchButton.setBackground(new java.awt.Color(52, 152, 219));
        searchButton.setForeground(java.awt.Color.WHITE);
        searchButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        refreshButton.setBackground(new java.awt.Color(155, 89, 182));
        refreshButton.setForeground(java.awt.Color.WHITE);
        refreshButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        exportButton.setBackground(new java.awt.Color(46, 204, 113));
        exportButton.setForeground(java.awt.Color.WHITE);
        exportButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        updateStatusButton.setBackground(new java.awt.Color(52, 152, 219));
        updateStatusButton.setForeground(java.awt.Color.WHITE);
        updateStatusButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        // Style table
        historyTable.getTableHeader().setBackground(new java.awt.Color(70, 130, 180));
        historyTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        historyTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        // Set table selection mode
        historyTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Add table selection listener
        historyTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedOrder();
            }
        });
        
        // Set up date choosers
        fromDateChooser.setDate(new Date());
        toDateChooser.setDate(new Date());
    }
    
    private void loadHistoryData() {
        try {
            List<Object[]> orders = helper.getinfo("donhang");
            
            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
            model.setRowCount(0);
            
            for (Object[] order : orders) {
                if (order.length >= 6) {
                    String status = getOrderStatusText((Integer) order[4]); // trangthai
                    String statusClass = getOrderStatusClass((Integer) order[4]);
                    
                    model.addRow(new Object[]{
                        order[1], // madonhang
                        formatCurrency((Integer) order[3]), // giatien
                        status,
                        order[5] != null ? order[5] : "", // ghichu
                        order[6] != null ? order[6] : "", // nhanvien_id
                        getFormattedDate()
                    });
                }
            }
            
            // Update total orders label
            totalOrdersLabel.setText("Tổng đơn hàng: " + orders.size());
            
        } catch (Exception e) {
            logger.error("Error loading history data", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSelectedOrder() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
            String orderId = model.getValueAt(selectedRow, 0).toString();
            
            try {
                Map<String, Object> orderDetails = helper.get_order(orderId);
                if (orderDetails != null) {
                    showOrderDetails(orderDetails);
                }
            } catch (Exception e) {
                logger.error("Error loading order details", e);
            }
        }
    }
    
    private void showOrderDetails(Map<String, Object> order) {
        StringBuilder details = new StringBuilder();
        details.append("Chi tiết đơn hàng:\n");
        details.append("Mã đơn hàng: ").append(order.get("madonhang")).append("\n");
        details.append("Giá tiền: ").append(formatCurrency((Integer) order.get("giatien"))).append("\n");
        details.append("Trạng thái: ").append(getOrderStatusText((Integer) order.get("trangthai"))).append("\n");
        details.append("Ghi chú: ").append(order.get("ghichu") != null ? order.get("ghichu") : "").append("\n");
        details.append("Nhân viên ID: ").append(order.get("nhanvien_id"));
        
        javax.swing.JOptionPane.showMessageDialog(this, 
            details.toString(), 
            "Chi tiết đơn hàng", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
    
    private String getOrderStatusText(int status) {
        switch (status) {
            case 0: return "Đang chờ";
            case 1: return "Hoàn thành";
            case 2: return "Đã hủy";
            default: return "Không xác định";
        }
    }
    
    private String getOrderStatusClass(int status) {
        switch (status) {
            case 0: return "warning";
            case 1: return "success";
            case 2: return "danger";
            default: return "secondary";
        }
    }
    
    private String formatCurrency(int amount) {
        return CurrencyUtils.formatCurrency(amount);
    }
    
    private void updateSelectedOrderStatus() {
        int selectedRow = historyTable.getSelectedRow();
        if (selectedRow < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn đơn hàng cần cập nhật trạng thái!", 
                "Thông báo", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        String orderCode = model.getValueAt(selectedRow, 0).toString();
        String currentStatus = model.getValueAt(selectedRow, 2).toString();
        
        // Hiển thị dialog chọn trạng thái mới
        String[] statusOptions = {"Đang chờ", "Hoàn thành", "Đã hủy"};
        String newStatus = (String) javax.swing.JOptionPane.showInputDialog(
            this,
            "Chọn trạng thái mới cho đơn hàng " + orderCode + ":\nTrạng thái hiện tại: " + currentStatus,
            "Cập nhật trạng thái",
            javax.swing.JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus
        );
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            try {
                // Convert status text to number
                int statusNumber = getStatusNumber(newStatus);
                
                // Update order status
                Map<String, Object> updateData = new HashMap<>();
                updateData.put("trangthai", statusNumber);
                
                boolean success = helper.update_order(orderCode, updateData);
                
                if (success) {
                    // Update table
                    model.setValueAt(newStatus, selectedRow, 2);
                    
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Cập nhật trạng thái thành công!", 
                        "Thành công", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Lỗi khi cập nhật trạng thái!", 
                        "Lỗi", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                logger.error("Error updating order status", e);
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi khi cập nhật trạng thái: " + e.getMessage(), 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private int getStatusNumber(String statusText) {
        switch (statusText) {
            case "Đang chờ": return 0;
            case "Hoàn thành": return 1;
            case "Đã hủy": return -1;
            default: return 0;
        }
    }
    
    private void searchOrders() {
        try {
            List<Object[]> orders = helper.getinfo("donhang");
            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
            model.setRowCount(0);
            
            String searchText = searchField.getText().trim().toLowerCase();
            String statusFilter = (String) statusComboBox.getSelectedItem();
            Date fromDate = fromDateChooser.getDate();
            Date toDate = toDateChooser.getDate();
            
            int filteredCount = 0;
            
            for (Object[] order : orders) {
                if (order.length >= 6) {
                    String orderId = order[1].toString().toLowerCase();
                    String note = order[5] != null ? order[5].toString().toLowerCase() : "";
                    String status = getOrderStatusText((Integer) order[4]);
                    
                    boolean matchesSearch = searchText.isEmpty() || 
                        orderId.contains(searchText) || note.contains(searchText);
                    boolean matchesStatus = statusFilter.equals("Tất cả") || status.equals(statusFilter);
                    
                    if (matchesSearch && matchesStatus) {
                        model.addRow(new Object[]{
                            order[1], // madonhang
                            formatCurrency((Integer) order[3]), // giatien
                            status,
                            order[5] != null ? order[5] : "", // ghichu
                            order[6] != null ? order[6] : "", // nhanvien_id
                            getFormattedDate()
                        });
                        filteredCount++;
                    }
                }
            }
            
            totalOrdersLabel.setText("Kết quả tìm kiếm: " + filteredCount + " đơn hàng");
            
        } catch (Exception e) {
            logger.error("Error searching orders", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi tìm kiếm: " + e.getMessage(), 
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
        filterPanel = new javax.swing.JPanel();
        searchLabel = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        statusLabel = new javax.swing.JLabel();
        statusComboBox = new javax.swing.JComboBox<>();
        fromDateLabel = new javax.swing.JLabel();
        fromDateChooser = new com.toedter.calendar.JDateChooser();
        toDateLabel = new javax.swing.JLabel();
        toDateChooser = new com.toedter.calendar.JDateChooser();
        searchButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        updateStatusButton = new javax.swing.JButton();
        historyPanel = new javax.swing.JPanel();
        historyLabel = new javax.swing.JLabel();
        totalOrdersLabel = new javax.swing.JLabel();
        historyScrollPane = new javax.swing.JScrollPane();
        historyTable = new javax.swing.JTable();

        titleLabel.setText("LỊCH SỬ GIAO DỊCH");

        filterPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        searchLabel.setText("Tìm kiếm:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        filterPanel.add(searchLabel, gbc);

        searchField.setColumns(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        filterPanel.add(searchField, gbc);

        statusLabel.setText("Trạng thái:");
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        filterPanel.add(statusLabel, gbc);

        statusComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Đang chờ", "Hoàn thành", "Đã hủy" }));
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        filterPanel.add(statusComboBox, gbc);

        fromDateLabel.setText("Từ ngày:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        filterPanel.add(fromDateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        filterPanel.add(fromDateChooser, gbc);

        toDateLabel.setText("Đến ngày:");
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        filterPanel.add(toDateLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.0;
        filterPanel.add(toDateChooser, gbc);

        searchButton.setText("Tìm kiếm");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        filterPanel.add(searchButton, gbc);

        refreshButton.setText("Làm mới");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        filterPanel.add(refreshButton, gbc);

        exportButton.setText("Xuất Excel");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        filterPanel.add(exportButton, gbc);

        updateStatusButton.setText("Cập nhật trạng thái");
        updateStatusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateStatusButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        filterPanel.add(updateStatusButton, gbc);

        historyPanel.setLayout(new java.awt.BorderLayout());

        historyLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        historyLabel.setText("Lịch sử đơn hàng");

        totalOrdersLabel.setText("Tổng đơn hàng: 0");

        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        topPanel.add(historyLabel);
        topPanel.add(totalOrdersLabel);
        historyPanel.add(topPanel, java.awt.BorderLayout.NORTH);

        historyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã đơn hàng", "Giá tiền", "Trạng thái", "Ghi chú", "Nhân viên", "Thời gian"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        historyScrollPane.setViewportView(historyTable);

        historyPanel.add(historyScrollPane, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addComponent(filterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(historyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addGap(18, 18, 18)
                .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(historyPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        searchOrders();
    }//GEN-LAST:event_searchButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        loadHistoryData();
        searchField.setText("");
        statusComboBox.setSelectedIndex(0);
        fromDateChooser.setDate(new Date());
        toDateChooser.setDate(new Date());
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Dữ liệu đã được làm mới!", 
            "Thông báo", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Tính năng xuất Excel sẽ được phát triển trong phiên bản tiếp theo!", 
            "Thông báo", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_exportButtonActionPerformed

    private void updateStatusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateStatusButtonActionPerformed
        updateSelectedOrderStatus();
    }//GEN-LAST:event_updateStatusButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.toedter.calendar.JDateChooser fromDateChooser;
    private javax.swing.JLabel fromDateLabel;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel historyPanel;
    private javax.swing.JLabel historyLabel;
    private javax.swing.JScrollPane historyScrollPane;
    private javax.swing.JTable historyTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JComboBox<String> statusComboBox;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel totalOrdersLabel;
    private com.toedter.calendar.JDateChooser toDateChooser;
    private javax.swing.JLabel toDateLabel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton updateStatusButton;
    
    /**
     * Helper method to get formatted date (convert from UTC to Vietnam timezone)
     */
    private String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date()); // placeholder for actual date
    }
    
    // End of variables declaration//GEN-END:variables
}