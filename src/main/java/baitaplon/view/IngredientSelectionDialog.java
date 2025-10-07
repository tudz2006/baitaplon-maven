package baitaplon.view;

import baitaplon.backend.helper;
import baitaplon.utils.CurrencyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Dialog for selecting ingredients and quantities for a product
 */
public class IngredientSelectionDialog extends JDialog {
    private static final Logger logger = LoggerFactory.getLogger(IngredientSelectionDialog.class);
    
    private final helper ihelper;
    private final Gson gson;
    
    // UI Components
    private JTable ingredientTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> ingredientComboBox;
    private JSpinner quantitySpinner;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton saveButton;
    private JButton cancelButton;
    
    // Data
    private List<Object[]> availableIngredients;
    private Map<String, Integer> selectedIngredients;
    private String originalJson;
    
    public IngredientSelectionDialog(JFrame parent, String currentJson) {
        super(parent, "Chọn Nguyên Liệu", true);
        this.ihelper = new helper();
        this.gson = new Gson();
        this.originalJson = currentJson;
        this.selectedIngredients = new HashMap<>();
        
        initializeComponents();
        setupLayout();
        loadAvailableIngredients();
        loadCurrentIngredients();
        setupEventHandlers();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void initializeComponents() {
        // Table for selected ingredients
        String[] columnNames = {"Tên Nguyên Liệu", "Số Lượng", "Đơn Vị", "Giá"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        ingredientTable = new JTable(tableModel);
        ingredientTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        ingredientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
        
        // ComboBox for ingredient selection
        ingredientComboBox = new JComboBox<>();
        ingredientComboBox.setPreferredSize(new Dimension(200, 25));
        
        // Quantity spinner
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        quantitySpinner.setPreferredSize(new Dimension(80, 25));
        
        // Buttons
        addButton = new JButton("Thêm");
        editButton = new JButton("Sửa");
        deleteButton = new JButton("Xóa");
        saveButton = new JButton("Lưu");
        cancelButton = new JButton("Hủy");
        
        // Initially disable edit and delete buttons
        updateButtonStates();
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Top panel - ingredient selection
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Nguyên liệu:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        topPanel.add(ingredientComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        topPanel.add(new JLabel("Số lượng:"), gbc);
        
        gbc.gridx = 3; gbc.gridy = 0;
        topPanel.add(quantitySpinner, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0;
        topPanel.add(addButton, gbc);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center panel - table
        JScrollPane scrollPane = new JScrollPane(ingredientTable);
        scrollPane.setPreferredSize(new Dimension(580, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel - action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(editButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(new JLabel("  ")); // Spacer
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private void loadAvailableIngredients() {
        try {
            availableIngredients = ihelper.getinfo("nhaphang");
            ingredientComboBox.removeAllItems();
            
            for (Object[] ingredient : availableIngredients) {
                if (ingredient.length >= 2 && ingredient[1] != null) {
                    String name = ingredient[1].toString();
                    ingredientComboBox.addItem(name);
                }
            }
            
            logger.info("Loaded {} available ingredients", availableIngredients.size());
            
        } catch (Exception e) {
            logger.error("Error loading available ingredients", e);
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải danh sách nguyên liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadCurrentIngredients() {
        try {
            if (originalJson != null && !originalJson.trim().isEmpty()) {
                java.lang.reflect.Type type = new TypeToken<Map<String, Integer>>(){}.getType();
                Map<String, Integer> current = gson.fromJson(originalJson, type);
                if (current != null) {
                    selectedIngredients.putAll(current);
                    updateTable();
                }
            }
        } catch (Exception e) {
            logger.error("Error loading current ingredients", e);
            // If JSON is invalid, start with empty selection
            selectedIngredients.clear();
        }
    }
    
    private void updateTable() {
        tableModel.setRowCount(0);
        
        for (Map.Entry<String, Integer> entry : selectedIngredients.entrySet()) {
            String ingredientName = entry.getKey();
            Integer quantity = entry.getValue();
            
            // Find ingredient details from available ingredients
            String unit = "kg";
            String price = "0";
            
            for (Object[] ingredient : availableIngredients) {
                if (ingredient.length >= 4 && ingredient[1] != null && 
                    ingredientName.equals(ingredient[1].toString())) {
                    unit = ingredient.length > 2 && ingredient[2] != null ? 
                           ingredient[2].toString() : "kg";
                    price = ingredient.length > 3 && ingredient[3] != null ? 
                           CurrencyUtils.formatCurrency(Integer.parseInt(ingredient[3].toString())) : "0";
                    break;
                }
            }
            
            tableModel.addRow(new Object[]{
                ingredientName,
                quantity,
                unit,
                price
            });
        }
        
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        boolean hasSelection = ingredientTable.getSelectedRow() >= 0;
        editButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }
    
    private void setupEventHandlers() {
        addButton.addActionListener(e -> addIngredient());
        editButton.addActionListener(e -> editIngredient());
        deleteButton.addActionListener(e -> deleteIngredient());
        saveButton.addActionListener(e -> saveAndClose());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void addIngredient() {
        String selectedIngredient = (String) ingredientComboBox.getSelectedItem();
        if (selectedIngredient == null || selectedIngredient.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn nguyên liệu", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer quantity = (Integer) quantitySpinner.getValue();
        
        // Check if ingredient already exists
        if (selectedIngredients.containsKey(selectedIngredient)) {
            int result = JOptionPane.showConfirmDialog(this,
                "Nguyên liệu '" + selectedIngredient + "' đã tồn tại. Bạn có muốn cập nhật số lượng?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                selectedIngredients.put(selectedIngredient, quantity);
                updateTable();
            }
        } else {
            selectedIngredients.put(selectedIngredient, quantity);
            updateTable();
        }
    }
    
    private void editIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn nguyên liệu cần sửa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ingredientName = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Show input dialog for new quantity
        String input = JOptionPane.showInputDialog(this,
            "Nhập số lượng mới cho '" + ingredientName + "':",
            "Sửa số lượng",
            JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.trim().isEmpty()) {
            try {
                int newQuantity = Integer.parseInt(input.trim());
                if (newQuantity > 0) {
                    selectedIngredients.put(ingredientName, newQuantity);
                    updateTable();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Số lượng phải lớn hơn 0", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, 
                    "Số lượng không hợp lệ", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void deleteIngredient() {
        int selectedRow = ingredientTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn nguyên liệu cần xóa", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ingredientName = (String) tableModel.getValueAt(selectedRow, 0);
        
        int result = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn xóa nguyên liệu '" + ingredientName + "'?",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            selectedIngredients.remove(ingredientName);
            updateTable();
        }
    }
    
    private void saveAndClose() {
        try {
            String json = gson.toJson(selectedIngredients);
            // Store the result in a static variable or use a callback
            IngredientSelectionDialog.result = json;
            dispose();
        } catch (Exception e) {
            logger.error("Error saving ingredients", e);
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi lưu nguyên liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Static variable to store result
    private static String result = null;
    
    /**
     * Get the selected ingredients as JSON string
     */
    public static String getResult() {
        return result;
    }
    
    /**
     * Clear the result (call this after getting the result)
     */
    public static void clearResult() {
        result = null;
    }
}
