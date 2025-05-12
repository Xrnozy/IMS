package InventoryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
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

            // Add action listeners
            dashboardButton.addActionListener(e -> {
                mainContent.removeAll();
                Dashboard dashboard = new Dashboard();
                mainContent.add(dashboard.getContentPanel(), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

            productManagementButton.addActionListener(e -> {
                mainContent.removeAll();
                ProductManagement productManagement = new ProductManagement();
                mainContent.add(productManagement.getContentPanel(), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

            inventoryOverviewButton.addActionListener(e -> {
                mainContent.removeAll();
                InventoryOverview inventoryOverview = new InventoryOverview();
                mainContent.add(inventoryOverview.getContentPanel(), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

            orderOverviewButton.addActionListener(e -> {
                mainContent.removeAll();
                OrderOverview orderOverview = new OrderOverview();
                mainContent.add(orderOverview.getContentPanel(), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

            generateReportButton.addActionListener(e -> {
                mainContent.removeAll();
                GenerateReport generateReport = new GenerateReport();
                mainContent.add(generateReport.getContentPanel(), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

            productsOverviewButton.addActionListener(e -> {
                mainContent.removeAll();
                ProductsOverview productsOverview = new ProductsOverview();
                mainContent.add(productsOverview.getContentPanel(), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

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

        // Update the table layout to match OrderManagementSystem
        String[] columns = {"", "Product ID", "Product Name", "Category", "Sales Channel", "Products in Stock", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only the "Action" column is editable
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setBackground(Color.WHITE);

        // Adjust column widths to match OrderManagementSystem
        table.getColumnModel().getColumn(0).setPreferredWidth(30);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);

        // Update the "Action" column to use a modernized button renderer similar to OrderManagementSystem
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
                int rowIndex = table.getSelectedRow();
                showEditProductDialog(rowIndex, tableModel);
                return super.getCellEditorValue();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Fetch items from the database
        fetchItemsFromDatabase();

        return panel;
    }

    private void fetchItemsFromDatabase() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM items")) {

            while (resultSet.next()) {
                Object[] row = {
                    Boolean.FALSE, // Checkbox column
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

        JTextField productIdField = new JTextField(model.getValueAt(rowIndex, 1).toString(), 10);
        JTextField productNameField = new JTextField(model.getValueAt(rowIndex, 2).toString(), 20);
        JTextField categoryField = new JTextField(model.getValueAt(rowIndex, 3).toString(), 20);
        JTextField salesChannelField = new JTextField(model.getValueAt(rowIndex, 4).toString(), 20);
        JTextField stockField = new JTextField(model.getValueAt(rowIndex, 5).toString(), 5);

        JPanel productIdPanel = createLabeledField("Product ID:", productIdField);
        JPanel productNamePanel = createLabeledField("Product Name:", productNameField);
        JPanel categoryPanel = createLabeledField("Category:", categoryField);
        JPanel salesChannelPanel = createLabeledField("Sales Channel:", salesChannelField);
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
                        // Update the table model
                        model.setValueAt(productNameField.getText().trim(), rowIndex, 2);
                        model.setValueAt(categoryField.getText().trim(), rowIndex, 3);
                        model.setValueAt(salesChannelField.getText().trim(), rowIndex, 4);
                        model.setValueAt(stock, rowIndex, 5);

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
