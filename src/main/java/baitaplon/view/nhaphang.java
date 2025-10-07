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
public class nhaphang extends javax.swing.JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(nhaphang.class);
    private ihelper helper = new helper();

    /**
     * Creates new form nhaphang
     */
    public nhaphang() {
        initComponents();
        setupUI();
        loadInventoryData();
    }
    
    private void setupUI() {
        // Set background
        setBackground(new java.awt.Color(248, 249, 250));
        
        // Style title
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(new java.awt.Color(70, 130, 180));
        
        // Style form panel
        formPanel.setBackground(java.awt.Color.WHITE);
        formPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Style buttons
        addButton.setBackground(new java.awt.Color(46, 204, 113));
        addButton.setForeground(java.awt.Color.WHITE);
        addButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        updateButton.setBackground(new java.awt.Color(52, 152, 219));
        updateButton.setForeground(java.awt.Color.WHITE);
        updateButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        deleteButton.setBackground(new java.awt.Color(231, 76, 60));
        deleteButton.setForeground(java.awt.Color.WHITE);
        deleteButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        refreshButton.setBackground(new java.awt.Color(155, 89, 182));
        refreshButton.setForeground(java.awt.Color.WHITE);
        refreshButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        // Style table
        inventoryTable.getTableHeader().setBackground(new java.awt.Color(70, 130, 180));
        inventoryTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        inventoryTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        // Set table selection mode
        inventoryTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Add table selection listener
        inventoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedItem();
            }
        });
    }
    
    private void loadInventoryData() {
        try {
            List<Object[]> inventory = helper.getinfo("nhaphang");
            
            DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();
            model.setRowCount(0);
            
            for (Object[] item : inventory) {
                if (item.length >= 4) {
                    try {
                        int price = (Integer) item[2];
                        int count = (Integer) item[3];
                        
                        // Use long to avoid integer overflow
                        long totalValue = (long) price * (long) count;
                        
                        // Check for overflow and cap at reasonable value
                        if (totalValue > Integer.MAX_VALUE) {
                            logger.warn("Inventory item {} has very large total value: {}, capping to reasonable value", 
                                item[1], totalValue);
                            totalValue = Integer.MAX_VALUE;
                        }
                        
                        model.addRow(new Object[]{
                            item[0], // ID
                            item[1], // name
                            formatCurrency(price), // price
                            count, // count
                            formatCurrency((int) totalValue) // total value
                        });
                    } catch (Exception e) {
                        logger.error("Error processing inventory item: {}", item[1], e);
                        model.addRow(new Object[]{
                            item[0], // ID
                            item[1], // name
                            "Lỗi", // price
                            "Lỗi", // count
                            "Lỗi" // total value
                        });
                    }
                }
            }
            
            // Update total items label
            totalItemsLabel.setText("Tổng sản phẩm: " + inventory.size());
            
        } catch (Exception e) {
            logger.error("Error loading inventory data", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSelectedItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();
            
            // Load data into form
            idField.setText(model.getValueAt(selectedRow, 0).toString());
            nameField.setText(model.getValueAt(selectedRow, 1).toString());
            
            // Parse price using utility
            String priceText = model.getValueAt(selectedRow, 2).toString();
            int price = CurrencyUtils.parseCurrencySafe(priceText, 0);
            priceSpinner.setValue(price);
            
            countSpinner.setValue(model.getValueAt(selectedRow, 3));
        }
    }
    
    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        priceSpinner.setValue(0);
        countSpinner.setValue(1);
        inventoryTable.clearSelection();
    }
    
    private String formatCurrency(int amount) {
        return CurrencyUtils.formatCurrency(amount);
    }
    
    private boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập tên sản phẩm!", 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if ((Integer) priceSpinner.getValue() <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Giá sản phẩm phải lớn hơn 0!", 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            priceSpinner.requestFocus();
            return false;
        }
        
        if ((Integer) countSpinner.getValue() <= 0) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Số lượng phải lớn hơn 0!", 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            countSpinner.requestFocus();
            return false;
        }
        
        return true;
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
        idLabel = new javax.swing.JLabel();
        idField = new javax.swing.JTextField();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        priceLabel = new javax.swing.JLabel();
        priceSpinner = new javax.swing.JSpinner();
        countLabel = new javax.swing.JLabel();
        countSpinner = new javax.swing.JSpinner();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        inventoryPanel = new javax.swing.JPanel();
        inventoryLabel = new javax.swing.JLabel();
        totalItemsLabel = new javax.swing.JLabel();
        refreshButton = new javax.swing.JButton();
        inventoryScrollPane = new javax.swing.JScrollPane();
        inventoryTable = new javax.swing.JTable();

        titleLabel.setText("QUẢN LÝ NHẬP HÀNG");

        formPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        idLabel.setText("ID:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        formPanel.add(idLabel, gbc);

        idField.setEditable(false);
        idField.setText("Auto");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(idField, gbc);

        nameLabel.setText("Tên sản phẩm:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(nameField, gbc);

        priceLabel.setText("Giá (VNĐ):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(priceLabel, gbc);

        priceSpinner.setModel(new javax.swing.SpinnerNumberModel(0, 0, 999999999, 1000));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(priceSpinner, gbc);

        countLabel.setText("Số lượng:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(countLabel, gbc);

        countSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 9999, 1));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(countSpinner, gbc);

        addButton.setText("Thêm mới");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(addButton, gbc);

        updateButton.setText("Cập nhật");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(updateButton, gbc);

        deleteButton.setText("Xóa");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 4;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(deleteButton, gbc);

        clearButton.setText("Làm mới");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(clearButton, gbc);

        inventoryPanel.setLayout(new java.awt.BorderLayout());

        inventoryLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        inventoryLabel.setText("Danh sách sản phẩm");

        totalItemsLabel.setText("Tổng sản phẩm: 0");

        refreshButton.setText("Làm mới");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        topPanel.add(inventoryLabel);
        topPanel.add(totalItemsLabel);
        topPanel.add(refreshButton);
        inventoryPanel.add(topPanel, java.awt.BorderLayout.NORTH);

        inventoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Tên sản phẩm", "Giá", "Số lượng", "Thành tiền"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        inventoryScrollPane.setViewportView(inventoryTable);

        inventoryPanel.add(inventoryScrollPane, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel)
                    .addComponent(formPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(inventoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(inventoryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        if (!validateForm()) return;
        
        try {
            String name = nameField.getText().trim();
            int price = (Integer) priceSpinner.getValue();
            int count = (Integer) countSpinner.getValue();
            
            boolean success = helper.nhaphang_add(name, price, count);
            
            if (success) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Thêm sản phẩm thành công!", 
                    "Thành công", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadInventoryData();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi khi thêm sản phẩm!", 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.error("Error adding inventory item", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        if (idField.getText().isEmpty() || idField.getText().equals("Auto")) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sản phẩm cần cập nhật!", 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!validateForm()) return;
        
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText().trim();
            int price = (Integer) priceSpinner.getValue();
            int count = (Integer) countSpinner.getValue();
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("price", price);
            updates.put("count", count);
            
            boolean success = helper.nhaphang_update(id, updates);
            
            if (success) {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Cập nhật sản phẩm thành công!", 
                    "Thành công", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadInventoryData();
            } else {
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi khi cập nhật sản phẩm!", 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            logger.error("Error updating inventory item", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if (idField.getText().isEmpty() || idField.getText().equals("Auto")) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sản phẩm cần xóa!", 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa sản phẩm này?", 
            "Xác nhận xóa", 
            javax.swing.JOptionPane.YES_NO_OPTION);
        
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            try {
                int id = Integer.parseInt(idField.getText());
                boolean success = helper.nhaphang_delete(id);
                
                if (success) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Xóa sản phẩm thành công!", 
                        "Thành công", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                    loadInventoryData();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Lỗi khi xóa sản phẩm!", 
                        "Lỗi", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                logger.error("Error deleting inventory item", e);
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi: " + e.getMessage(), 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        loadInventoryData();
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Dữ liệu đã được làm mới!", 
            "Thông báo", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_refreshButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel countLabel;
    private javax.swing.JSpinner countSpinner;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel formPanel;
    private javax.swing.JTextField idField;
    private javax.swing.JLabel idLabel;
    private javax.swing.JPanel inventoryPanel;
    private javax.swing.JLabel inventoryLabel;
    private javax.swing.JScrollPane inventoryScrollPane;
    private javax.swing.JTable inventoryTable;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JSpinner priceSpinner;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel totalItemsLabel;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}