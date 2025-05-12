package InventoryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import InventoryManagement.sql.DatabaseConnection;

public class InventoryOverview {
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

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

        // Table setup
        String[] columnNames = {"Item ID", "Name", "Category", "Quantity", "Price", "Sales Channel"};
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
}
