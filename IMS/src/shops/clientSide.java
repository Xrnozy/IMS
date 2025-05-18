package shops;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import InventoryManagement.sql.DatabaseConnection;

public class clientSide extends JFrame {
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private Timer refreshTimer;

    public clientSide() {
        setTitle("Orders Table");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Table setup
        String[] columnNames = {"Order ID", "Date", "Requested by", "Item ID", "Item Name", "Category", "Quantity", "Price", "Status", "Action"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Only allow editing the Action column
            }
        };

        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(35);

        // Set up the status combo box column
        String[] statuses = {"Pending", "On the Way", "Completed"};
        ordersTable.getColumnModel().getColumn(9).setCellEditor(new DefaultCellEditor(new JComboBox<>(statuses)));

        // Style the table header
        JTableHeader header = ordersTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));

        // Add table directly to frame with scroll pane
        add(new JScrollPane(ordersTable));

        // Load orders
        loadOrders();

        // Add table cell editor listener for status updates
        ordersTable.getColumnModel().getColumn(9).getCellEditor().addCellEditorListener(
            new javax.swing.event.CellEditorListener() {
                public void editingStopped(javax.swing.event.ChangeEvent e) {
                    try {
                        int row = ordersTable.getSelectedRow();
                        if (row != -1) {  // Make sure a row is selected
                            String orderId = tableModel.getValueAt(row, 0).toString();
                            String newStatus = ordersTable.getValueAt(row, 9).toString();
                            updateOrderStatus(orderId, newStatus);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null,
                            "Error updating status: " + ex.getMessage(),
                            "Update Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }

                public void editingCanceled(javax.swing.event.ChangeEvent e) {}
            });

        // Initialize refresh timer (checks every 10 seconds for better performance)
        refreshTimer = new Timer(10000, e -> checkForUpdates());
        refreshTimer.setCoalesce(true);  // Combine multiple pending events into one
        refreshTimer.start();
    }

    /**
     * Loads orders from the database and populates the orders table.
     */
    private void loadOrders() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();

            // Get only necessary columns with ORDER BY for better performance
            ResultSet rs = stmt.executeQuery(
                "SELECT order_id, date, requested_by, item_id, name, category, quantity, price, status " +
                "FROM orderitems ORDER BY date DESC");

            // Create a temporary array for efficient batch updates
            Object[][] newData = new Object[100][10]; // Pre-allocate space for 100 rows
            int rowCount = 0;

            while (rs.next() && rowCount < newData.length) {
                newData[rowCount] = new Object[] {
                    rs.getString("order_id"),
                    rs.getString("date"),
                    rs.getString("requested_by"),
                    rs.getString("item_id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getString("status"),
                    rs.getString("status")
                };
                rowCount++;
            }

            // Update table model in one batch operation
            tableModel.setRowCount(0);
            for (int i = 0; i < rowCount; i++) {
                tableModel.addRow(newData[i]);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading orders: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Updates the status of a specific order in the database.
     * @param orderId The ID of the order to update.
     * @param newStatus The new status to set for the order.
     */
    private void updateOrderStatus(String orderId, String newStatus) {
        try {
            if ("Completed".equalsIgnoreCase(newStatus)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to mark this order as Completed? This will move it to completed orders and update inventory.",
                    "Confirm Completion", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (confirm != JOptionPane.YES_OPTION) {
                    // Revert the status selection in the table to previous value
                    int row = -1;
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        if (tableModel.getValueAt(i, 0).toString().equals(orderId)) {
                            row = i;
                            break;
                        }
                    }
                    if (row != -1) {
                        // Set back to previous status (column 8 is display status)
                        String prevStatus = tableModel.getValueAt(row, 8).toString();
                        tableModel.setValueAt(prevStatus, row, 9);
                    }
                    return;
                }
            }
            Connection conn = DatabaseConnection.getConnection();
            if ("Completed".equalsIgnoreCase(newStatus)) {
                // Get the order details
                PreparedStatement selectStmt = conn.prepareStatement(
                    "SELECT * FROM orderitems WHERE order_id = ?");
                selectStmt.setString(1, orderId);
                ResultSet rs = selectStmt.executeQuery();
                if (rs.next()) {
                    // Insert into items table (restock or add new item)
                    PreparedStatement insertItem = conn.prepareStatement(
                        "INSERT INTO items (item_id, name, category, quantity, sales_channel, price) VALUES (?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)");
                    insertItem.setInt(1, rs.getInt("item_id"));
                    insertItem.setString(2, rs.getString("name"));
                    insertItem.setString(3, rs.getString("category")); // category from order
                    insertItem.setInt(4, rs.getInt("quantity"));
                    insertItem.setString(5, rs.getString("sales_channel"));
                    insertItem.setDouble(6, rs.getDouble("price"));
                    insertItem.executeUpdate();
                    insertItem.close();
                    // Insert into completed table
                    PreparedStatement insertCompleted = conn.prepareStatement(
                        "INSERT INTO completed (date, requested_by, sales_channel, item_id, name, quantity, price, category, completed_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())");
                    insertCompleted.setString(1, rs.getString("date"));
                    insertCompleted.setString(2, rs.getString("requested_by"));
                    insertCompleted.setString(3, rs.getString("sales_channel"));
                    insertCompleted.setInt(4, rs.getInt("item_id"));
                    insertCompleted.setString(5, rs.getString("name"));
                    insertCompleted.setInt(6, rs.getInt("quantity"));
                    insertCompleted.setDouble(7, rs.getDouble("price"));
                    insertCompleted.setString(8, rs.getString("category"));
                    insertCompleted.executeUpdate();
                    insertCompleted.close();
                    // Delete from orderitems
                    PreparedStatement deleteStmt = conn.prepareStatement(
                        "DELETE FROM orderitems WHERE order_id = ?");
                    deleteStmt.setString(1, orderId);
                    deleteStmt.executeUpdate();
                    deleteStmt.close();
                }
                rs.close();
                selectStmt.close();
            } else {
                PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE orderitems SET status = ? WHERE order_id = ?");
                pstmt.setString(1, newStatus);
                pstmt.setString(2, orderId);
                pstmt.executeUpdate();
                pstmt.close();
            }
            // Update the display status column (index 8) as well
            int row = -1;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).toString().equals(orderId)) {
                    row = i;
                    break;
                }
            }
            if (row != -1) {
                if ("Completed".equalsIgnoreCase(newStatus)) {
                    tableModel.removeRow(row);
                } else {
                    tableModel.setValueAt(newStatus, row, 8);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error updating order status: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Periodically checks for updates in the orders.
     */
    private void checkForUpdates() {
        // Simply reload the table periodically to check for any changes
        loadOrders();
    }

    /**
     * Disposes of the client-side frame and releases resources.
     */
    @Override
    public void dispose() {
        refreshTimer.stop(); // Stop the timer when closing the window
        super.dispose();
    }

    /**
     * Main method to launch the client-side application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            clientSide frame = new clientSide();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}