/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package baitaplon.view;

import baitaplon.backend.IProductService;
import baitaplon.backend.ProductService;
import baitaplon.utils.CurrencyUtils;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Product Management Panel
 * Handles UI for product CRUD operations
 * @author HI
 */
public class QuanLySanPham extends javax.swing.JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(QuanLySanPham.class);
    private final IProductService productService;

    /**
     * Creates new form QuanLySanPham
     */
    public QuanLySanPham() {
        this.productService = new ProductService();
        initComponents();
        setupUI();
        loadProductData();
    }
    
    private void setupUI() {
        // Set background
        setBackground(new java.awt.Color(248, 249, 250));
        
        // Style title
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(new java.awt.Color(70, 130, 180));
        
        // Style control panel
        controlPanel.setBackground(java.awt.Color.WHITE);
        controlPanel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
            javax.swing.BorderFactory.createLineBorder(new java.awt.Color(200, 200, 200)),
            javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Style buttons
        addButton.setBackground(new java.awt.Color(46, 204, 113));
        addButton.setForeground(java.awt.Color.WHITE);
        addButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        editButton.setBackground(new java.awt.Color(52, 152, 219));
        editButton.setForeground(java.awt.Color.WHITE);
        editButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        deleteButton.setBackground(new java.awt.Color(231, 76, 60));
        deleteButton.setForeground(java.awt.Color.WHITE);
        deleteButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        refreshButton.setBackground(new java.awt.Color(155, 89, 182));
        refreshButton.setForeground(java.awt.Color.WHITE);
        refreshButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        // Style table
        productTable.getTableHeader().setBackground(new java.awt.Color(70, 130, 180));
        productTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        productTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        // Set table selection mode
        productTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        // Add table selection listener
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedProduct();
            }
        });
    }
    
    private void loadProductData() {
        try {
            List<Object[]> products = productService.getAllProducts();
            
            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            model.setRowCount(0);
            
            if (products != null) {
                for (Object[] product : products) {
                    if (product.length >= 4) {
                        model.addRow(new Object[]{
                            product[0], // id
                            product[1], // name
                            formatCurrency((Integer) product[2]), // price
                            product[3] != null ? product[3] : "" // nguyenlieu
                        });
                    }
                }
                
                // Update total products label
                totalProductsLabel.setText("Tổng sản phẩm: " + products.size());
            } else {
                totalProductsLabel.setText("Tổng sản phẩm: 0");
            }
            
        } catch (Exception e) {
            logger.error("Error loading product data", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu sản phẩm: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            DefaultTableModel model = (DefaultTableModel) productTable.getModel();
            String productName = model.getValueAt(selectedRow, 1).toString();
            String price = model.getValueAt(selectedRow, 2).toString();
            String nguyenlieu = model.getValueAt(selectedRow, 3).toString();
            
            // Fill form with selected product data
            productNameField.setText(productName);
            priceField.setText(price);
            noteField.setText(nguyenlieu);
        }
    }
    
    private void addProduct() {
        if (validateInput()) {
            try {
                String productName = productNameField.getText().trim();
                int price = CurrencyUtils.parseCurrencySafe(priceField.getText(), 0);
                String nguyenlieu = noteField.getText().trim();
                
                boolean success = productService.addProduct(productName, price, nguyenlieu);
                
                if (success) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Thêm sản phẩm thành công!", 
                        "Thành công", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    
                    clearForm();
                    loadProductData();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Lỗi khi thêm sản phẩm!", 
                        "Lỗi", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                logger.error("Error adding product", e);
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi khi thêm sản phẩm: " + e.getMessage(), 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sản phẩm cần sửa!", 
                "Thông báo", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (validateInput()) {
            try {
                DefaultTableModel model = (DefaultTableModel) productTable.getModel();
                Integer productId = (Integer) model.getValueAt(selectedRow, 0);
                
                String productName = productNameField.getText().trim();
                int price = CurrencyUtils.parseCurrencySafe(priceField.getText(), 0);
                String nguyenlieu = noteField.getText().trim();
                
                boolean success = productService.updateProduct(productId, productName, price, nguyenlieu);
                
                if (success) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Sửa sản phẩm thành công!", 
                        "Thành công", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    
                    clearForm();
                    loadProductData();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Lỗi khi sửa sản phẩm!", 
                        "Lỗi", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                logger.error("Error editing product", e);
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi khi sửa sản phẩm: " + e.getMessage(), 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow < 0) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn sản phẩm cần xóa!", 
                "Thông báo", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        String productName = model.getValueAt(selectedRow, 1).toString();
        
        int confirm = javax.swing.JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa sản phẩm '" + productName + "'?", 
            "Xác nhận xóa", 
            javax.swing.JOptionPane.YES_NO_OPTION);
        
        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
            try {
                Integer productId = (Integer) model.getValueAt(selectedRow, 0);
                
                boolean success = productService.deleteProduct(productId);
                
                if (success) {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Xóa sản phẩm thành công!", 
                        "Thành công", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    
                    clearForm();
                    loadProductData();
                } else {
                    javax.swing.JOptionPane.showMessageDialog(this, 
                        "Lỗi khi xóa sản phẩm!", 
                        "Lỗi", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                logger.error("Error deleting product", e);
                javax.swing.JOptionPane.showMessageDialog(this, 
                    "Lỗi khi xóa sản phẩm: " + e.getMessage(), 
                    "Lỗi", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private boolean validateInput() {
        String productName = productNameField.getText().trim();
        String priceText = priceField.getText().trim();
        String nguyenlieu = noteField.getText().trim();
        
        if (productName.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập tên sản phẩm!", 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            productNameField.requestFocus();
            return false;
        }
        
        if (priceText.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập giá sản phẩm!", 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        if (!CurrencyUtils.isValidCurrency(priceText)) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Giá sản phẩm không hợp lệ!", 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        if (nguyenlieu.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn nguyên liệu!", 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            selectIngredients();
            return false;
        }
        
        return true;
    }
    
    
    private void clearForm() {
        productNameField.setText("");
        priceField.setText("");
        noteField.setText("");
        productTable.clearSelection();
    }
    
    /**
     * Open ingredient selection dialog
     */
    private void selectIngredients() {
        try {
            String currentJson = noteField.getText().trim();
            IngredientSelectionDialog dialog = new IngredientSelectionDialog(
                (javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this), 
                currentJson
            );
            dialog.setVisible(true);
            
            // Get result from dialog
            String result = IngredientSelectionDialog.getResult();
            if (result != null) {
                noteField.setText(result);
                // Clear the result to avoid memory leaks
                IngredientSelectionDialog.clearResult();
            }
        } catch (Exception e) {
            logger.error("Error opening ingredient selection dialog", e);
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khi mở dialog chọn nguyên liệu: " + e.getMessage(),
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private String formatCurrency(int amount) {
        return CurrencyUtils.formatCurrency(amount);
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
        controlPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        formPanel = new javax.swing.JPanel();
        productNameLabel = new javax.swing.JLabel();
        productNameField = new javax.swing.JTextField();
        priceLabel = new javax.swing.JLabel();
        priceField = new javax.swing.JTextField();
        noteLabel = new javax.swing.JLabel();
        noteField = new javax.swing.JTextField();
        clearButton = new javax.swing.JButton();
        productPanel = new javax.swing.JPanel();
        productLabel = new javax.swing.JLabel();
        totalProductsLabel = new javax.swing.JLabel();
        productScrollPane = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();

        titleLabel.setText("QUẢN LÝ SẢN PHẨM BÁN RA");

        controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        addButton.setText("Thêm sản phẩm");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        controlPanel.add(addButton);

        editButton.setText("Sửa sản phẩm");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        controlPanel.add(editButton);

        deleteButton.setText("Xóa sản phẩm");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        controlPanel.add(deleteButton);

        refreshButton.setText("Làm mới");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        controlPanel.add(refreshButton);

        helpButton = new javax.swing.JButton();
        helpButton.setText("Chọn nguyên liệu");
        helpButton.setBackground(new java.awt.Color(52, 152, 219));
        helpButton.setForeground(java.awt.Color.WHITE);
        helpButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });
        controlPanel.add(helpButton);

        formPanel.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        productNameLabel.setText("Tên sản phẩm:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        formPanel.add(productNameLabel, gbc);

        productNameField.setColumns(20);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(productNameField, gbc);

        priceLabel.setText("Giá bán:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(priceLabel, gbc);

        priceField.setColumns(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(priceField, gbc);

        noteLabel.setText("Nguyên liệu:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(noteLabel, gbc);

        // Create panel for ingredient selection
        javax.swing.JPanel ingredientPanel = new javax.swing.JPanel(new java.awt.BorderLayout());
        ingredientPanel.setBackground(java.awt.Color.WHITE);
        
        // Ingredient display field (read-only)
        noteField.setColumns(20);
        noteField.setEditable(false);
        noteField.setBackground(new java.awt.Color(248, 249, 250));
        noteField.setToolTipText("Hiển thị nguyên liệu đã chọn");
        ingredientPanel.add(noteField, java.awt.BorderLayout.CENTER);
        
        // Select ingredients button
        javax.swing.JButton selectIngredientsButton = new javax.swing.JButton("Chọn...");
        selectIngredientsButton.setBackground(new java.awt.Color(52, 152, 219));
        selectIngredientsButton.setForeground(java.awt.Color.WHITE);
        selectIngredientsButton.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        selectIngredientsButton.setPreferredSize(new java.awt.Dimension(80, 25));
        selectIngredientsButton.addActionListener(e -> selectIngredients());
        ingredientPanel.add(selectIngredientsButton, java.awt.BorderLayout.EAST);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        formPanel.add(ingredientPanel, gbc);

        clearButton.setText("Xóa form");
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        formPanel.add(clearButton, gbc);

        productPanel.setLayout(new java.awt.BorderLayout());

        productLabel.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        productLabel.setText("Danh sách sản phẩm");

        totalProductsLabel.setText("Tổng sản phẩm: 0");

        javax.swing.JPanel topPanel = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        topPanel.add(productLabel);
        topPanel.add(totalProductsLabel);
        productPanel.add(topPanel, java.awt.BorderLayout.NORTH);

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Tên sản phẩm", "Giá bán", "Nguyên liệu"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
                    .addComponent(controlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(formPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(productPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addGap(18, 18, 18)
                .addComponent(controlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(formPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(productPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        addProduct();
    }//GEN-LAST:event_addButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        editProduct();
    }//GEN-LAST:event_editButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        deleteProduct();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        loadProductData();
        clearForm();
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Dữ liệu đã được làm mới!", 
            "Thông báo", 
            javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        clearForm();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpButtonActionPerformed
        selectIngredients();
    }//GEN-LAST:event_helpButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel formPanel;
    private javax.swing.JButton helpButton;
    private javax.swing.JTextField noteField;
    private javax.swing.JLabel noteLabel;
    private javax.swing.JLabel productLabel;
    private javax.swing.JPanel productPanel;
    private javax.swing.JScrollPane productScrollPane;
    private javax.swing.JTable productTable;
    private javax.swing.JTextField priceField;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JTextField productNameField;
    private javax.swing.JLabel productNameLabel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel totalProductsLabel;
    // End of variables declaration//GEN-END:variables
}
