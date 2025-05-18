package InventoryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import InventoryManagement.sql.DatabaseConnection;

public class InventoryOverview {
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

 
    // Default constructor for legacy usage
    public InventoryOverview() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Inventory Overview");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);

            InventoryOverview inventoryOverview = new InventoryOverview();
            frame.setContentPane(inventoryOverview.createContentPane());

            frame.setVisible(true);
        });
    }

    public JPanel createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Inventory Overview", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        
        // Summary Panels Container
        JPanel summaryContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        
        // Left Summary Panel (Total Items and Categories)
        JPanel leftSummaryPanel = new JPanel();
        leftSummaryPanel.setLayout(new BoxLayout(leftSummaryPanel, BoxLayout.Y_AXIS));
        leftSummaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(25, 30, 25, 30) // More margin
        ));
        leftSummaryPanel.setBackground(Color.WHITE);

        // Right Summary Panel (Sales Channels)
        JPanel rightSummaryPanel = new JPanel();
        rightSummaryPanel.setLayout(new BoxLayout(rightSummaryPanel, BoxLayout.Y_AXIS));
        rightSummaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(25, 30, 25, 30) // More margin
        ));
        rightSummaryPanel.setBackground(Color.WHITE);

        updateSummaryPanels(leftSummaryPanel, rightSummaryPanel);
        
        summaryContainer.add(leftSummaryPanel);
        summaryContainer.add(rightSummaryPanel);
        
        // Add some space between summary and table
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0)); // Add bottom margin
        topSection.add(titleLabel, BorderLayout.NORTH);
        topSection.add(Box.createVerticalStrut(20), BorderLayout.CENTER);
        topSection.add(summaryContainer, BorderLayout.SOUTH);
        
        // Add margin below summary panels
        JPanel summaryWithMargin = new JPanel(new BorderLayout());
        summaryWithMargin.add(topSection, BorderLayout.NORTH);
        summaryWithMargin.add(Box.createVerticalStrut(25), BorderLayout.CENTER);
        contentPane.add(summaryWithMargin, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Item ID", "Name", "Category", "Stocks(in boxes)", "Price", "Sales Channel"};
        tableModel = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        itemsTable = new JTable(tableModel);
        itemsTable.setRowHeight(30);
        sorter = new TableRowSorter<>(tableModel);
        itemsTable.setRowSorter(sorter);

        JTableHeader header = itemsTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add Item");
        JButton deleteButton = new JButton("Delete Item");

        addButton.addActionListener(e -> showAddItemDialog());
        deleteButton.addActionListener(e -> deleteSelectedItem());

        buttonsPanel.add(addButton);
        buttonsPanel.add(deleteButton);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);

        // Fetch items from database
        fetchItemsFromDatabase();

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
        contentPane.add(filterPanel, BorderLayout.SOUTH);

        // --- POPULATE FILTERS ---
        categoryCombo.removeAllItems();
        shopCombo.removeAllItems();
        categoryCombo.addItem("All");
        shopCombo.addItem("All");
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT DISTINCT category, sales_channel FROM items")) {
            java.util.Set<String> categories = new java.util.HashSet<>();
            java.util.Set<String> shops = new java.util.HashSet<>();
            while (resultSet.next()) {
                String cat = resultSet.getString("category");
                String shop = resultSet.getString("sales_channel");
                if (cat != null && !cat.trim().isEmpty()) categories.add(cat);
                if (shop != null && !shop.trim().isEmpty()) shops.add(shop);
            }
            for (String cat : categories) categoryCombo.addItem(cat);
            for (String shop : shops) shopCombo.addItem(shop);
        } catch (Exception e) { /* ignore for now */ }

        // --- FILTER LOGIC ---
        searchButton.addActionListener(e -> applyInventoryFilters(categoryCombo, shopCombo, stockCombo, searchField.getText().trim()));
        categoryCombo.addActionListener(e -> applyInventoryFilters(categoryCombo, shopCombo, stockCombo, searchField.getText().trim()));
        shopCombo.addActionListener(e -> applyInventoryFilters(categoryCombo, shopCombo, stockCombo, searchField.getText().trim()));
        stockCombo.addActionListener(e -> applyInventoryFilters(categoryCombo, shopCombo, stockCombo, searchField.getText().trim()));
for (int i = 0; i < itemsTable.getColumnCount(); i++) {
            itemsTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    return label;
                }
            });
        }
        // Color coding for stock level (like ProductManagement)
        itemsTable.getColumnModel().getColumn(3).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                int quantity = 0;
                try { quantity = Integer.parseInt(value.toString()); } catch (Exception ignored) {}
                if (quantity == 0) {
                    label.setBackground(new Color(139, 0, 0));
                    label.setForeground(Color.WHITE);
                    label.setText("Out of Stock");
                } else if (quantity <= 10) {
                    label.setBackground(new Color(255, 99, 71));
                    label.setForeground(Color.WHITE);
                } else if (quantity <= 30) {
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

        // --- DOUBLE-CLICK LOW STOCK TO ORDER/AUTOFILL ---
        itemsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
                    int row = itemsTable.rowAtPoint(e.getPoint());
                    int col = itemsTable.columnAtPoint(e.getPoint());
                    if (row != -1 && col == 3) { // Stocks(in boxes) column
                        Object value = tableModel.getValueAt(row, 3);
                        int quantity = 0;
                        try { quantity = Integer.parseInt(value.toString()); } catch (Exception ignored) {}
                        if (quantity <= 30) {
                            String productId = tableModel.getValueAt(row, 0).toString();
                            String productName = tableModel.getValueAt(row, 1).toString();
                            String category = tableModel.getValueAt(row, 2).toString();
                            String shop = tableModel.getValueAt(row, 5).toString();
                            contentPane.removeAll();
                            OrderManagementSystem orderManagement = new OrderManagementSystem();
                            contentPane.add(orderManagement.getMainPanel(), BorderLayout.CENTER);
                            contentPane.revalidate();
                            contentPane.repaint();
                            // Show autofill after panel is swapped
                            SwingUtilities.invokeLater(() -> {
                                orderManagement.getContentPanelWithAutofill(productId, productName, category, shop);
                            });
                        }
                    }
                }
            }
        });

        return contentPane;
    }

    public JPanel getContentPanel() {
        return createContentPane();
    }

    private void fetchItemsFromDatabase() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM items")) {

            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getString("item_id"),
                    resultSet.getString("name"),
                    resultSet.getString("category"),
                    resultSet.getInt("quantity"),
                    resultSet.getDouble("price"),
                    resultSet.getString("sales_channel")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching items from the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSummaryPanels(JPanel leftPanel, JPanel rightPanel) {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement()) {
            
            // Get total items count
            ResultSet totalResult = statement.executeQuery("SELECT COUNT(*) as total, SUM(quantity) as total_quantity FROM items");
            int totalItems = totalResult.next() ? totalResult.getInt("total") : 0;
            int totalQuantity = totalResult.getInt("total_quantity");
            
            // Get category counts
            ResultSet categoryResult = statement.executeQuery(
                "SELECT category, COUNT(*) as count FROM items GROUP BY category");
            Map<String, Integer> categoryCount = new HashMap<>();
            while (categoryResult.next()) {
                categoryCount.put(
                    categoryResult.getString("category"),
                    categoryResult.getInt("count")
                );
            }
            
            // Get sales channel counts
            ResultSet channelResult = statement.executeQuery(
                "SELECT sales_channel, COUNT(*) as count FROM items GROUP BY sales_channel");
            Map<String, Integer> channelCount = new HashMap<>();
            while (channelResult.next()) {
                channelCount.put(
                    channelResult.getString("sales_channel"),
                    channelResult.getInt("count")
                );
            }            // Update left panel
            JLabel totalItemsLabel = new JLabel("Total Items: " + totalItems);
            totalItemsLabel.setFont(new Font("Arial", Font.BOLD, 18));
            totalItemsLabel.setForeground(new Color(41, 128, 185));
            totalItemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftPanel.add(totalItemsLabel);
            leftPanel.add(Box.createVerticalStrut(10));
            JLabel totalQuantityLabel = new JLabel("Total Quantity: " + totalQuantity);
            totalQuantityLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            totalQuantityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftPanel.add(totalQuantityLabel);
            leftPanel.add(Box.createVerticalStrut(15));
            
            // Show total number of categories
            int totalCategories = categoryCount.size();
            JLabel categoriesCountLabel = new JLabel("Categories: " + totalCategories);
            categoriesCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
            categoriesCountLabel.setForeground(new Color(110, 0, 220));
            categoriesCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            leftPanel.add(categoriesCountLabel);

            // Update right panel
            rightPanel.removeAll();
            
            // Show total number of sales channels
            int totalChannels = channelCount.size();
            JLabel channelsCountLabel = new JLabel("Stores: " + totalChannels);
            channelsCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
            channelsCountLabel.setForeground(new Color(110, 0, 220));
            channelsCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightPanel.add(channelsCountLabel);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error fetching summary data: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAddItemDialog() {
        JDialog addItemDialog = new JDialog((Frame) null, "Add New Item", true);
        addItemDialog.setSize(400, 300);
        addItemDialog.setLayout(new GridLayout(0, 2, 10, 10));

        JTextField itemIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField salesChannelField = new JTextField();

        addItemDialog.add(new JLabel("Item ID:"));
        addItemDialog.add(itemIdField);
        addItemDialog.add(new JLabel("Name:"));
        addItemDialog.add(nameField);
        addItemDialog.add(new JLabel("Category:"));
        addItemDialog.add(categoryField);
        addItemDialog.add(new JLabel("Quantity:"));
        addItemDialog.add(quantityField);
        addItemDialog.add(new JLabel("Price:"));
        addItemDialog.add(priceField);
        addItemDialog.add(new JLabel("Sales Channel:"));
        addItemDialog.add(salesChannelField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String itemId = itemIdField.getText().trim();
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            int quantity;
            double price;
            String salesChannel = salesChannelField.getText().trim();

            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
                price = Double.parseDouble(priceField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addItemDialog, "Invalid quantity or price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection connection = DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {

                String insertSQL = String.format(
                    "INSERT INTO items (item_id, name, category, quantity, price, sales_channel) VALUES ('%s', '%s', '%s', %d, %.2f, '%s')",
                    itemId, name, category, quantity, price, salesChannel
                );
                statement.executeUpdate(insertSQL);

                tableModel.addRow(new Object[]{itemId, name, category, quantity, price, salesChannel});
                addItemDialog.dispose();
                JOptionPane.showMessageDialog(null, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addItemDialog, "Error adding item to the database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addItemDialog.dispose());

        addItemDialog.add(saveButton);
        addItemDialog.add(cancelButton);

        addItemDialog.setVisible(true);
    }

    private void deleteSelectedItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select an item to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = itemsTable.convertRowIndexToModel(selectedRow);
        String itemId = tableModel.getValueAt(modelRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this item?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {

                String deleteSQL = String.format("DELETE FROM items WHERE item_id = '%s'", itemId);
                statement.executeUpdate(deleteSQL);

                tableModel.removeRow(modelRow);
                JOptionPane.showMessageDialog(null, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error deleting item from the database: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyInventoryFilters(JComboBox<String> categoryCombo, JComboBox<String> shopCombo, JComboBox<String> stockCombo, String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        java.util.List<RowFilter<Object, Object>> filters = new java.util.ArrayList<>();
        if (categoryCombo.getSelectedItem() != null && !"All".equals(categoryCombo.getSelectedItem().toString())) {
            filters.add(RowFilter.regexFilter("^" + categoryCombo.getSelectedItem().toString() + "$", 2));
        }
        if (shopCombo.getSelectedItem() != null && !"All".equals(shopCombo.getSelectedItem().toString())) {
            filters.add(RowFilter.regexFilter("^" + shopCombo.getSelectedItem().toString() + "$", 5));
        }
        if (searchText != null && !searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText, 1)); // Search by Name
        }
        // Stock level filter
        String stockLevel = stockCombo.getSelectedItem() != null ? stockCombo.getSelectedItem().toString() : "All";
        if (!"All".equals(stockLevel)) {
            filters.add(new RowFilter<Object, Object>() {
                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    int quantity = 0;
                    try { quantity = Integer.parseInt(entry.getStringValue(3)); } catch (Exception ignored) {}
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
        itemsTable.setRowSorter(sorter);
    }
}
