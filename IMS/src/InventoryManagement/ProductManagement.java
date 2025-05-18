package InventoryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import InventoryManagement.sql.DatabaseConnection;

public class ProductManagement {
    private DefaultTableModel tableModel;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Proceed to the Product Management page
            JFrame frame = new JFrame("Product Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JLabel label = new JLabel("Product Management Page", SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 24));

            // Add navigation bar similar to Dashboard
            JPanel navBar = new JPanel();
            navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));
            navBar.setBackground(new Color(45, 45, 45));
            navBar.setPreferredSize(new Dimension(250, 800));

            JButton dashboardButton = new JButton("Dashboard");
            JButton productManagementButton = new JButton("Product Management");
            JButton inventoryOverviewButton = new JButton("Inventory Overview");
            JButton orderOverviewButton = new JButton("Order Overview");
            JButton generateReportButton = new JButton("Generate Report");
            JButton productsOverviewButton = new JButton("Products Overview");

            // Style buttons
            JButton[] buttons = {dashboardButton, productManagementButton, inventoryOverviewButton, orderOverviewButton, generateReportButton, productsOverviewButton};
            for (JButton button : buttons) {
                button.setFocusPainted(false);
                button.setContentAreaFilled(false);
                button.setBorderPainted(false);
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Arial", Font.BOLD, 16));
                button.setAlignmentX(Component.CENTER_ALIGNMENT);
                navBar.add(Box.createVerticalStrut(20));
                navBar.add(button);
            }

            JPanel mainContent = new JPanel(new BorderLayout());
            mainContent.add(label, BorderLayout.CENTER);

          

           

            frame.getContentPane().add(navBar, BorderLayout.WEST);
            frame.getContentPane().add(mainContent, BorderLayout.CENTER);
            frame.setVisible(true);
        });
    }

    public JPanel getContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Edit Inventory", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // --- FILTERS PANEL ---
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryCombo = new JComboBox<>();
        JLabel shopLabel = new JLabel("Shop:");
        JComboBox<String> shopCombo = new JComboBox<>();
        JLabel stockLabel = new JLabel("Stock Level:");
        JComboBox<String> stockCombo = new JComboBox<>(new String[]{"All", "Out of Stock", "Warning (Low)", "Warning (Medium)", "Normal"});
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("Search");
        filterPanel.add(categoryLabel);
        filterPanel.add(categoryCombo);
        filterPanel.add(shopLabel);
        filterPanel.add(shopCombo);
        filterPanel.add(stockLabel);
        filterPanel.add(stockCombo);
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        panel.add(filterPanel, BorderLayout.SOUTH);

        // --- TABLE SETUP ---
        String[] columns = {"Product ID", "Product Name", "Category", "Store", "Stocks(in boxes)", "Added Date/Time", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return Integer.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only the "Action" column is editable
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 200));
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Color.WHITE);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80); // Product ID
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Product Name
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Category
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Sales Channel
        table.getColumnModel().getColumn(4).setPreferredWidth(80); // Products in Stock
        table.getColumnModel().getColumn(5).setPreferredWidth(160); // Added Date/Time
        table.getColumnModel().getColumn(6).setPreferredWidth(150); // Action

        // Color coding for stock level (like Dashboard)
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                int quantity = 0;
                try { quantity = Integer.parseInt(value.toString()); } catch (Exception ignored) {}
                double percentage = (quantity / 100.0) * 100;
                if (quantity == 0) {
                    label.setBackground(new Color(139, 0, 0));
                    label.setForeground(Color.WHITE);
                    label.setText("Out of Stock");
                } else if (percentage <= 10) {
                    label.setBackground(new Color(255, 99, 71));
                    label.setForeground(Color.WHITE);
                    
                } else if (percentage <= 30) {
                    label.setBackground(new Color(255, 215, 0));
                    label.setForeground(Color.BLACK);
             
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
                }
                label.setOpaque(true);
                return label;
            }
        });

        // Action button renderer/editor
        class ModernButtonRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JButton button = new JButton("Click to Edit/Update");
                button.setBackground(new Color(110, 0, 220));
                button.setForeground(Color.WHITE);
                button.setFocusPainted(false);
                button.setBorder(null);
                button.setFont(new Font("Arial", Font.BOLD, 12));
                return button;
            }
        }
        table.getColumnModel().getColumn(6).setCellRenderer(new ModernButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()) {
            @Override
            public Object getCellEditorValue() {
                int viewRow = table.getEditingRow();
                if (viewRow != -1) {
                    int modelRow = table.convertRowIndexToModel(viewRow);
                    showEditProductDialog(modelRow, tableModel);
                }
                return super.getCellEditorValue();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // --- DOUBLE-CLICK LOW STOCK TO ORDER ---
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = table.rowAtPoint(e.getPoint());
                    int col = table.columnAtPoint(e.getPoint());
                    if (row != -1 && col == 4) { // Stocks column double-click
                        int modelRow = table.convertRowIndexToModel(row);
                        Object value = tableModel.getValueAt(modelRow, 4);
                        int quantity = 0;
                        try { quantity = Integer.parseInt(value.toString()); } catch (Exception ignored) {}
                        if (quantity <= 30) {
                            String productId = tableModel.getValueAt(modelRow, 0).toString();
                            String productName = tableModel.getValueAt(modelRow, 1).toString();
                            String category = tableModel.getValueAt(modelRow, 2).toString();
                            String shop = tableModel.getValueAt(modelRow, 3).toString();
                            // Change the ProductManagement panel to OrderManagementSystem and show autofill
                            panel.removeAll();
                            OrderManagementSystem orderManagement = new OrderManagementSystem();
                            panel.add(orderManagement.getMainPanel(), BorderLayout.CENTER);
                            panel.revalidate();
                            panel.repaint();
                            // Show autofill after panel is swapped
                            SwingUtilities.invokeLater(() -> {
                                orderManagement.getContentPanelWithAutofill(productId, productName, category, shop);
                            });
                        }
                    }
                }
            }
        });
        // --- POPULATE FILTERS AND TABLE ---
        fetchItemsAndPopulateFilters(categoryCombo, shopCombo);

        // --- FILTER LOGIC ---
        searchButton.addActionListener(e -> applyProductFilters(categoryCombo, shopCombo, stockCombo, searchField.getText().trim()));
        categoryCombo.addActionListener(e -> applyProductFilters(categoryCombo, shopCombo, stockCombo, searchField.getText().trim()));
        shopCombo.addActionListener(e -> applyProductFilters(categoryCombo, shopCombo, stockCombo, searchField.getText().trim()));
        stockCombo.addActionListener(e -> applyProductFilters(categoryCombo, shopCombo, stockCombo, searchField.getText().trim()));

        return panel;
    }

    private void fetchItemsAndPopulateFilters(JComboBox<String> categoryCombo, JComboBox<String> shopCombo) {
        tableModel.setRowCount(0);
        java.util.Set<String> categories = new java.util.HashSet<>();
        java.util.Set<String> shops = new java.util.HashSet<>();
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT i.*, c.completed_at FROM items i LEFT JOIN completed c ON i.item_id = c.item_id")) {
            while (resultSet.next()) {
                String category = resultSet.getString("category");
                String shop = resultSet.getString("sales_channel");
                categories.add(category);
                shops.add(shop);
                String completedAt = resultSet.getString("completed_at");
                String formattedDate = "";
                if (completedAt != null && !completedAt.isEmpty()) {
                    try {
                        java.time.LocalDateTime dt = java.time.LocalDateTime.parse(completedAt.replace(' ', 'T'));
                        formattedDate = dt.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd, MMM, yyyy, h:mm a")).replace("AM", "A.M").replace("PM", "P.M");
                    } catch (Exception ex) {
                        formattedDate = completedAt; // fallback
                    }
                }
                Object[] row = {
                    resultSet.getString("item_id"),
                    resultSet.getString("name"),
                    category,
                    shop,
                    resultSet.getInt("quantity"),
                    formattedDate,
                    "Click to Edit/Update"
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching items from the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        // Populate filters
        categoryCombo.removeAllItems();
        shopCombo.removeAllItems();
        categoryCombo.addItem("All");
        shopCombo.addItem("All");
        for (String cat : categories) categoryCombo.addItem(cat);
        for (String shop : shops) shopCombo.addItem(shop);
    }

    private void applyProductFilters(JComboBox<String> categoryCombo, JComboBox<String> shopCombo, JComboBox<String> stockCombo, String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();
        if (categoryCombo.getSelectedItem() != null && !"All".equals(categoryCombo.getSelectedItem().toString())) {
            filters.add(RowFilter.regexFilter("^" + categoryCombo.getSelectedItem().toString() + "$", 2));
        }
        if (shopCombo.getSelectedItem() != null && !"All".equals(shopCombo.getSelectedItem().toString())) {
            filters.add(RowFilter.regexFilter("^" + shopCombo.getSelectedItem().toString() + "$", 3));
        }
        if (searchText != null && !searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 1)); // Search by Product Name
        }
        // Stock level filter
        String stockLevel = stockCombo.getSelectedItem() != null ? stockCombo.getSelectedItem().toString() : "All";
        if (!"All".equals(stockLevel)) {
            filters.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    int quantity = 0;
                    try { quantity = Integer.parseInt(entry.getStringValue(4)); } catch (Exception ignored) {}
                    if ("Out of Stock".equals(stockLevel)) return quantity == 0;
                    if ("Warning (Low)".equals(stockLevel)) return quantity > 0 && quantity <= 10;
                    if ("Warning (Medium)".equals(stockLevel)) return quantity > 10 && quantity <= 30;
                    if ("Normal".equals(stockLevel)) return quantity > 30;
                    return true;
                }
            });
        }
        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filters.size() == 1) {
            sorter.setRowFilter(filters.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
        // Attach sorter to table
        JTable table = null;
        for (Component comp : categoryCombo.getParent().getParent().getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                JViewport viewport = scrollPane.getViewport();
                for (Component c : viewport.getComponents()) {
                    if (c instanceof JTable) {
                        table = (JTable) c;
                        break;
                    }
                }
            }
        }
        if (table != null) table.setRowSorter(sorter);
    }

    private void fetchItemsFromDatabase() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM items")) {

            while (resultSet.next()) {
                Object[] row = {
                    // Removed Checkbox column
                    resultSet.getString("item_id"),
                    resultSet.getString("name"),
                    resultSet.getString("category"),
                    resultSet.getString("sales_channel"),
                    resultSet.getInt("quantity"),
                    "Click to Edit/Update" // Action column
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching items from the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEditProductDialog(int rowIndex, DefaultTableModel model) {
        JDialog editProductDialog = new JDialog((Frame) null, "Edit Product", true);
        editProductDialog.setSize(450, 400);
        editProductDialog.setLocationRelativeTo(null);
        editProductDialog.setLayout(new GridLayout(0, 1, 10, 10));
        editProductDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Adjusted column indices for accessing data from the model
        JTextField productIdField = new JTextField(model.getValueAt(rowIndex, 0).toString(), 10);
        JTextField productNameField = new JTextField(model.getValueAt(rowIndex, 1).toString(), 20);
        JTextField categoryField = new JTextField(model.getValueAt(rowIndex, 2).toString(), 20);
        JTextField salesChannelField = new JTextField(model.getValueAt(rowIndex, 3).toString(), 20);
        JTextField stockField = new JTextField(model.getValueAt(rowIndex, 4).toString(), 5);

        JPanel productIdPanel = createLabeledField("Product ID:", productIdField);
        JPanel productNamePanel = createLabeledField("Product Name:", productNameField);
        JPanel categoryPanel = createLabeledField("Category:", categoryField);
        JPanel salesChannelPanel = createLabeledField("Store:", salesChannelField);
        JPanel stockPanel = createLabeledField("Stock:", stockField);

        editProductDialog.add(productIdPanel);
        editProductDialog.add(productNamePanel);
        editProductDialog.add(categoryPanel);
        editProductDialog.add(salesChannelPanel);
        editProductDialog.add(stockPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> editProductDialog.dispose());

        saveButton.addActionListener(e -> {
            if (productIdField.getText().trim().isEmpty() ||
                productNameField.getText().trim().isEmpty() ||
                categoryField.getText().trim().isEmpty() ||
                salesChannelField.getText().trim().isEmpty() ||
                stockField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(editProductDialog,
                        "All fields are required!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int stock = Integer.parseInt(stockField.getText().trim());
                if (stock < 0) {
                    throw new NumberFormatException();
                }

                // Debugging log for SQL query
                String updateSQL = String.format(
                    "UPDATE items SET name = '%s', category = '%s', sales_channel = '%s', quantity = %d WHERE item_id = '%s'",
                    productNameField.getText().trim(),
                    categoryField.getText().trim(),
                    salesChannelField.getText().trim(),
                    stock,
                    productIdField.getText().trim()
                );
                System.out.println("Executing SQL: " + updateSQL);

                // Update the database
                try (Connection connection = DatabaseConnection.getConnection();
                     Statement statement = connection.createStatement()) {

                    int rowsAffected = statement.executeUpdate(updateSQL);
                    System.out.println("Rows affected: " + rowsAffected);

                    if (rowsAffected > 0) {
                        // Update the table model - Adjusted column indices
                        model.setValueAt(productNameField.getText().trim(), rowIndex, 1);
                        model.setValueAt(categoryField.getText().trim(), rowIndex, 2);
                        model.setValueAt(salesChannelField.getText().trim(), rowIndex, 3);
                        model.setValueAt(stock, rowIndex, 4);

                        editProductDialog.dispose();
                        JOptionPane.showMessageDialog(null,
                                "Product updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(editProductDialog,
                                "No rows were updated. Please check the item ID.",
                                "Update Failed", JOptionPane.WARNING_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editProductDialog,
                        "Stock must be a non-negative number!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(editProductDialog,
                        "Error updating product in the database: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        editProductDialog.add(buttonPanel);

        editProductDialog.setVisible(true);
    }

    private JPanel createLabeledField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.add(label);
        panel.add(field);
        return panel;
    }

    // ButtonRenderer class to render buttons in the table
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // ButtonEditor class to handle button clicks in the table
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            return super.stopCellEditing();
        }
    }
}
