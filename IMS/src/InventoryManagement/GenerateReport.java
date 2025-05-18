package InventoryManagement;

import InventoryManagement.sql.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class GenerateReport {
    private DefaultTableModel tableModel;
    private JTable ordersTable;
    private JComboBox<String> filterComboBox;

    // Add a date filter field and calendar button as class members
    private JTextField dateField;
    private JButton calendarButton;

    /**
     * Main method to launch the Generate Report application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Proceed to the Generate Report page
            JFrame frame = new JFrame("Generate Report");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

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

            frame.getContentPane().add(navBar, BorderLayout.WEST);

            JPanel mainContent = new JPanel(new BorderLayout());
            mainContent.setBackground(Color.WHITE);
            mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel lblReports = new JLabel("Generate Report", SwingConstants.LEFT);
            lblReports.setFont(new Font("Arial", Font.BOLD, 18));

            // Create filter panel
            JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel filterLabel = new JLabel("Filter by: ");
            
            // Add "Deleted Items" filter option
            String[] filterOptions = {"All Orders", "Current Orders", "Completed Orders", "All Items", "Deleted Items"};
            JComboBox<String> filterComboBox = new JComboBox<>(filterOptions);
            filterPanel.add(filterLabel);
            filterPanel.add(filterComboBox);
            filterPanel.add(new JLabel("Date:"));
            JTextField dateField = new JTextField(10);
            dateField.setPreferredSize(new Dimension(200, 30));
            JButton calendarButton = new JButton("📅");
            filterPanel.add(dateField);
            filterPanel.add(calendarButton);
            

            // Add a search field and button to the filter panel
            JPanel searchPanel = new JPanel(new BorderLayout());
            JTextField searchField = new JTextField();
            searchField.setPreferredSize(new Dimension(200, 30) );
            JButton searchButton = new JButton("Search");
            searchPanel.add(searchField, BorderLayout.CENTER);
            searchPanel.add(searchButton, BorderLayout.EAST);
            filterPanel.add(searchPanel, BorderLayout.SOUTH);

            // Add filter panel to the top of the content area
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(lblReports, BorderLayout.NORTH);
            topPanel.add(filterPanel, BorderLayout.CENTER);
            mainContent.add(topPanel, BorderLayout.NORTH);

            String[] columnNames = { "Order ID", "Date", "Requested By", "Store", "Item ID", "Name", "Stocks(in boxes)"};
            DefaultTableModel tableModel = new DefaultTableModel(null, columnNames) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};

            JTable ordersTable = new JTable(tableModel);
            ordersTable.setRowHeight(40);
            ordersTable.setShowGrid(false);
            ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            ordersTable.setBackground(Color.WHITE);

            JTableHeader header = ordersTable.getTableHeader();
            header.setBackground(Color.WHITE);
            header.setFont(new Font("Arial", Font.BOLD, 12));

            JScrollPane scrollPane = new JScrollPane(ordersTable);
            scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            mainContent.add(scrollPane, BorderLayout.CENTER);

            // Load initial data (All Orders)
            GenerateReport report = new GenerateReport();
            report.tableModel = tableModel;
            report.ordersTable = ordersTable;
            report.filterComboBox = filterComboBox;
            report.dateField = dateField;
            report.calendarButton = calendarButton;
            report.loadData("All Orders");

            // Add filter action listener
            filterComboBox.addActionListener(e -> {
                String selectedFilter = (String) filterComboBox.getSelectedItem();
                String dateText = dateField.getText().trim();
                report.loadDataWithDate(selectedFilter, dateText);
            });
            calendarButton.addActionListener(e -> {
                // Show a simple date picker dialog
                String input =  dateField.getText();
                if (input != null && !input.trim().isEmpty()) {
                    dateField.setText(input.trim());
                    String selectedFilter = (String) filterComboBox.getSelectedItem();
                    report.loadDataWithDate(selectedFilter, input.trim());
                }
                else {
                    String selectedFilter = (String) filterComboBox.getSelectedItem();
                    report.loadDataWithDate(selectedFilter, "");
                }
            });

            JButton exportCsvButton = new JButton("Export to CSV");
            exportCsvButton.setFont(new Font("Arial", Font.PLAIN, 14));
            exportCsvButton.setBackground(new Color(110, 0, 220));
            exportCsvButton.setForeground(Color.WHITE);
            exportCsvButton.setFocusPainted(false);
            exportCsvButton.setBounds(20, 520, 160, 35);
            mainContent.add(exportCsvButton, BorderLayout.SOUTH);

            exportCsvButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Save CSV File");
                int userSelection = fileChooser.showSaveDialog(frame);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!filePath.endsWith(".csv")) filePath += ".csv";
                    try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
                        // Write header
                        for (int col = 0; col < tableModel.getColumnCount(); col++) {
                            pw.print(tableModel.getColumnName(col));
                            if (col < tableModel.getColumnCount() - 1) pw.print(",");
                        }
                        pw.println();
                        // Write data
                        for (int row = 0; row < tableModel.getRowCount(); row++) {
                            for (int col = 0; col < tableModel.getColumnCount(); col++) {
                                Object value = tableModel.getValueAt(row, col);
                                String text = value == null ? "" : value.toString().replaceAll(",", " ");
                                pw.print(text);
                                if (col < tableModel.getColumnCount() - 1) pw.print(",");
                            }
                            pw.println();
                        }
                        JOptionPane.showMessageDialog(frame, "CSV file exported successfully!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(frame, "Error exporting to CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            // Implement the search functionality
            searchButton.addActionListener(e -> {
                String searchText = searchField.getText().trim();
                if (searchText.isEmpty()) {
                    report.loadData("All Orders"); // Reload all data if search is cleared
                    return;
                }

                tableModel.setRowCount(0); // Clear the table
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(
                         "SELECT * FROM (" +
                         "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, status FROM orderItems " +
                         "UNION ALL " +
                         "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, 'Completed' as status FROM completed) AS combined " +
                         "WHERE order_id LIKE ? OR requested_by LIKE ? OR sales_channel LIKE ? OR item_id LIKE ? OR name LIKE ? OR quantity LIKE ? OR status LIKE ?")
                ) {
                    String queryParam = "%" + searchText + "%";
                    for (int i = 1; i <= 7; i++) {
                        preparedStatement.setString(i, queryParam);
                    }

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        while (resultSet.next()) {
                            Object[] row = {
                                resultSet.getInt("order_id"),
                                resultSet.getString("date"),
                                resultSet.getString("requested_by"),
                                resultSet.getString("sales_channel"),
                                resultSet.getInt("item_id"),
                                resultSet.getString("name"),
                                resultSet.getInt("quantity"),
                                resultSet.getString("status")
                            };
                            tableModel.addRow(row);
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Error performing search: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            frame.getContentPane().add(mainContent, BorderLayout.CENTER);

          

            frame.setVisible(true);
        });
    }

    // Helper method to format date strings
    private String formatDatabaseDate(String dateStr, List<DateTimeFormatter> inputFormatters, DateTimeFormatter outputFormatter) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return ""; // Handle null or empty date strings
        }
        for (DateTimeFormatter formatter : inputFormatters) {
            try {
                LocalDateTime dateTime = LocalDateTime.parse(dateStr, formatter);
                return dateTime.format(outputFormatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }
        System.err.println("Could not parse date string with any formatter: " + dateStr); // Log the error
        return dateStr; // Return original string if parsing fails
    }

    /**
     * Loads report data from the database and populates the table.
     */
    // Method to load data based on filter selection
 private void loadData(String filter) {
    // Clear existing data
    tableModel.setRowCount(0);

    // Define potential input date formatters
    List<DateTimeFormatter> inputFormatters = new ArrayList<>();
    inputFormatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    inputFormatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    inputFormatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
    inputFormatters.add(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
    inputFormatters.add(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    inputFormatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    inputFormatters.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));


    // Define output date formatter
    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("E, dd, MMM, yyyy hh:mm a");
    DateTimeFormatter completedOutputFormatter = DateTimeFormatter.ofPattern("E, dd, MMM, yyyy hh:mm a");

    try (Connection conn = DatabaseConnection.getConnection()) {
        String query;
        PreparedStatement pstmt;

        switch (filter) {
            case "Current Orders":
                query = "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, status FROM orderItems WHERE status != 'completed'";
                // Reset column names for orders (include status)
                String[] currentOrderColumns = { "Order ID", "Date", "Requested By", "Store", "Item ID", "Name", "Stocks(in boxes)", "Status"};
                tableModel.setColumnIdentifiers(currentOrderColumns);
                pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    String dateStr = rs.getString("date");
                    String formattedDate = formatDatabaseDate(dateStr, inputFormatters, outputFormatter);

                    Object[] row = {
                        rs.getInt("order_id"),
                        formattedDate,
                        rs.getString("requested_by"),
                        rs.getString("sales_channel"),
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getString("status")
                    };
                    tableModel.addRow(row);
                }
                break;
            case "Completed Orders":
                query = "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, 'Completed' as status, completed_at FROM completed";
                String[] completedOrderColumns = { "Order ID", "Date", "Requested By", "Store", "Item ID", "Name", "Stocks(in boxes)", "Status", "Completed Date"};
                tableModel.setColumnIdentifiers(completedOrderColumns);
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                while (rs.next()) {
                    String dateStr = rs.getString("date");
                    String completedAtStr = rs.getString("completed_at");
                    String formattedDate = formatDatabaseDate(dateStr, inputFormatters, outputFormatter);
                    String formattedCompletedAt = formatDatabaseDate(completedAtStr, inputFormatters, completedOutputFormatter);

                    Object[] row = {
                        rs.getInt("order_id"),
                        formattedDate,
                        rs.getString("requested_by"),
                        rs.getString("sales_channel"),
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getInt("quantity"),
                        rs.getString("status"),
                        formattedCompletedAt
                    };
                    tableModel.addRow(row);
                }
                break;
            case "All Items":
                query = "SELECT item_id, name, category, quantity, sales_channel, price FROM items";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();

                // Update column names for items (no status column)
                String[] itemColumns = {"Item ID", "Name", "Category", "Stocks(in boxes)", "Store", "Price"};
                tableModel.setColumnIdentifiers(itemColumns);

                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getString("sales_channel"),
                        rs.getDouble("price")
                    };
                    tableModel.addRow(row);
                }
                break;
            case "Deleted Items":
                query = "SELECT item_id, name, category, quantity, sales_channel, price, deleted_by, deleted_at FROM deleted";
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                String[] deletedColumns = {"Item ID", "Name", "Category", "Quantity", "Store", "Price", "Deleted By", "Deleted At"};
                tableModel.setColumnIdentifiers(deletedColumns);
                while (rs.next()) {
                    Object[] row = {
                        rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getString("sales_channel"),
                        rs.getDouble("price"),
                        rs.getString("deleted_by"),
                        rs.getTimestamp("deleted_at")
                    };
                    tableModel.addRow(row);
                }
                rs.close();
                pstmt.close();
                break;
            default: // All Orders
                // Fetch data from orderItems and completed separately to handle date formatting differently
                // Fetch from orderItems (do not parse date)
                String orderItemsQuery = "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, status FROM orderItems";
                 String[] allOrderColumns = { "Order ID", "Date", "Requested By", "Store", "Item ID", "Name", "Stocks(in boxes)", "Status"};
                tableModel.setColumnIdentifiers(allOrderColumns);
                pstmt = conn.prepareStatement(orderItemsQuery);
                ResultSet rsOrderItems = pstmt.executeQuery();

                while (rsOrderItems.next()) {
                    String dateStr = rsOrderItems.getString("date");
                    String formattedDate = formatDatabaseDate(dateStr, inputFormatters, outputFormatter);

                    Object[] row = {
                        rsOrderItems.getInt("order_id"),
                        formattedDate,
                        rsOrderItems.getString("requested_by"),
                        rsOrderItems.getString("sales_channel"),
                        rsOrderItems.getInt("item_id"),
                        rsOrderItems.getString("name"),
                        rsOrderItems.getInt("quantity"),
                        rsOrderItems.getString("status")
                    };
                    tableModel.addRow(row);
                }
                rsOrderItems.close();
                pstmt.close();

                // Fetch from completed (parse date and completed_at)
                String completedQuery = "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, 'Completed' as status, completed_at FROM completed";
                // Ensure columns match the table model before adding rows
                 String[] completedColumnsForUnion = { "Order ID", "Date", "Requested By", "Store", "Item ID", "Name", "Stocks(in boxes)", "Status"};
                 tableModel.setColumnIdentifiers(completedColumnsForUnion); // Temporarily set to match before adding rows

                pstmt = conn.prepareStatement(completedQuery);
                ResultSet rsCompleted = pstmt.executeQuery();

                while (rsCompleted.next()) {
                    String dateStr = rsCompleted.getString("date");
                    String completedAtStr = rsCompleted.getString("completed_at");
                    String formattedDate = formatDatabaseDate(dateStr, inputFormatters, outputFormatter);
                    formatDatabaseDate(completedAtStr, inputFormatters, completedOutputFormatter); // Format completed_at, but don't use it in the row

                    Object[] row = {
                        rsCompleted.getInt("order_id"),
                        formattedDate,
                        rsCompleted.getString("requested_by"),
                        rsCompleted.getString("sales_channel"),
                        rsCompleted.getInt("item_id"),
                        rsCompleted.getString("name"),
                        rsCompleted.getInt("quantity"),
                        rsCompleted.getString("status")
                    };
                    tableModel.addRow(row);
                }
                rsCompleted.close();
                pstmt.close();

                // Reset column identifiers back to the original for "All Orders" if they were changed
                 tableModel.setColumnIdentifiers(allOrderColumns);

                break;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
    // Method to load data based on filter selection and date
    private void loadDataWithDate(String filter, String dateText) {
        tableModel.setRowCount(0);
        List<DateTimeFormatter> inputFormatters = new ArrayList<>();
        inputFormatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        inputFormatters.add(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        inputFormatters.add(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        inputFormatters.add(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
        inputFormatters.add(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        inputFormatters.add(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        inputFormatters.add(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("E, dd, MMM, yyyy hh:mm a");
        DateTimeFormatter completedOutputFormatter = DateTimeFormatter.ofPattern("E, dd, MMM, yyyy hh:mm a");
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query;
            PreparedStatement pstmt;
            String dateWhere = (dateText != null && !dateText.isEmpty()) ? " AND date LIKE ?" : "";
            switch (filter) {
                case "Current Orders":
                    query = "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, status FROM orderItems WHERE status != 'completed'" + dateWhere;
                    String[] currentOrderColumns = { "Order ID", "Date", "Requested By", "Store", "Item ID", "Name", "Stocks(in boxes)", "Status"};
                    tableModel.setColumnIdentifiers(currentOrderColumns);
                    pstmt = conn.prepareStatement(query);
                    if (!dateWhere.isEmpty()) pstmt.setString(1, dateText + "%");
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        String dateStr = rs.getString("date");
                        String formattedDate = formatDatabaseDate(dateStr, inputFormatters, outputFormatter);
                        Object[] row = {
                            rs.getInt("order_id"),
                            formattedDate,
                            rs.getString("requested_by"),
                            rs.getString("sales_channel"),
                            rs.getInt("item_id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getString("status")
                        };
                        tableModel.addRow(row);
                    }
                    break;
                case "Completed Orders":
                    query = "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, 'Completed' as status, completed_at FROM completed WHERE 1=1" + (dateText != null && !dateText.isEmpty() ? " AND date LIKE ?" : "");
                    String[] completedOrderColumns = { "Order ID", "Date", "Requested By", "Store", "Item ID", "Name", "Stocks(in boxes)", "Status", "Completed Date"};
                    tableModel.setColumnIdentifiers(completedOrderColumns);
                    pstmt = conn.prepareStatement(query);
                    if (dateText != null && !dateText.isEmpty()) pstmt.setString(1, dateText + "%");
                    rs = pstmt.executeQuery();
                    while (rs.next()) {
                        String dateStr = rs.getString("date");
                        String completedAtStr = rs.getString("completed_at");
                        String formattedDate = formatDatabaseDate(dateStr, inputFormatters, outputFormatter);
                        String formattedCompletedAt = formatDatabaseDate(completedAtStr, inputFormatters, completedOutputFormatter);
                        Object[] row = {
                            rs.getInt("order_id"),
                            formattedDate,
                            rs.getString("requested_by"),
                            rs.getString("sales_channel"),
                            rs.getInt("item_id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getString("status"),
                            formattedCompletedAt
                        };
                        tableModel.addRow(row);
                    }
                    break;
                case "All Items":
                    query = "SELECT item_id, name, category, quantity, sales_channel, price FROM items";
                    pstmt = conn.prepareStatement(query);
                    rs = pstmt.executeQuery();
                    String[] itemColumns = {"Item ID", "Name", "Category", "Quantity", "Store", "Price"};
                    tableModel.setColumnIdentifiers(itemColumns);
                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("item_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getString("sales_channel"),
                            rs.getDouble("price")
                        };
                        tableModel.addRow(row);
                    }
                    break;
                case "Deleted Items":
                    query = "SELECT item_id, name, category, quantity, sales_channel, price, deleted_by, deleted_at FROM deleted";
                    pstmt = conn.prepareStatement(query);
                    rs = pstmt.executeQuery();
                    String[] deletedColumns = {"Item ID", "Name", "Category", "Quantity", "Store", "Price", "Deleted By", "Deleted At"};
                    tableModel.setColumnIdentifiers(deletedColumns);
                    while (rs.next()) {
                        Object[] row = {
                            rs.getInt("item_id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getInt("quantity"),
                            rs.getString("sales_channel"),
                            rs.getDouble("price"),
                            rs.getString("deleted_by"),
                            rs.getTimestamp("deleted_at")
                        };
                        tableModel.addRow(row);
                    }
                    break;
                default: // All Orders
                    String orderItemsQuery = "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, status FROM orderItems WHERE 1=1" + (dateText != null && !dateText.isEmpty() ? " AND date LIKE ?" : "");
                    String[] allOrderColumns = { "Order ID", "Date", "Requested By", "Store", "Item ID", "Name", "Stocks(in boxes)", "Status"};
                    tableModel.setColumnIdentifiers(allOrderColumns);
                    pstmt = conn.prepareStatement(orderItemsQuery);
                    if (dateText != null && !dateText.isEmpty()) pstmt.setString(1, dateText + "%");
                    ResultSet rsOrderItems = pstmt.executeQuery();
                    while (rsOrderItems.next()) {
                        String dateStr = rsOrderItems.getString("date");
                        String formattedDate = formatDatabaseDate(dateStr, inputFormatters, outputFormatter);
                        Object[] row = {
                            rsOrderItems.getInt("order_id"),
                            formattedDate,
                            rsOrderItems.getString("requested_by"),
                            rsOrderItems.getString("sales_channel"),
                            rsOrderItems.getInt("item_id"),
                            rsOrderItems.getString("name"),
                            rsOrderItems.getInt("quantity"),
                            rsOrderItems.getString("status")
                        };
                        tableModel.addRow(row);
                    }
                    rsOrderItems.close();
                    pstmt.close();
                    // Fetch from completed (parse date and completed_at)
                    String completedQuery = "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, 'Completed' as status, completed_at FROM completed WHERE 1=1" + (dateText != null && !dateText.isEmpty() ? " AND date LIKE ?" : "");
                    String[] completedColumnsForUnion = { "Order ID", "Date", "Requested By", "Store", "Item ID", "Name", "Stocks(in boxes)", "Status"};
                    tableModel.setColumnIdentifiers(completedColumnsForUnion);
                    pstmt = conn.prepareStatement(completedQuery);
                    if (dateText != null && !dateText.isEmpty()) pstmt.setString(1, dateText + "%");
                    ResultSet rsCompleted = pstmt.executeQuery();
                    while (rsCompleted.next()) {
                        String dateStr = rsCompleted.getString("date");
                        String completedAtStr = rsCompleted.getString("completed_at");
                        String formattedDate = formatDatabaseDate(dateStr, inputFormatters, outputFormatter);
                        formatDatabaseDate(completedAtStr, inputFormatters, completedOutputFormatter);
                        Object[] row = {
                            rsCompleted.getInt("order_id"),
                            formattedDate,
                            rsCompleted.getString("requested_by"),
                            rsCompleted.getString("sales_channel"),
                            rsCompleted.getInt("item_id"),
                            rsCompleted.getString("name"),
                            rsCompleted.getInt("quantity"),
                            rsCompleted.getString("status")
                        };
                        tableModel.addRow(row);
                    }
                    rsCompleted.close();
                    pstmt.close();
                    tableModel.setColumnIdentifiers(allOrderColumns);
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Add a method to dynamically load content into the Dashboard
    public JPanel getContentPanel() {
        dateField = new JTextField(10);
        calendarButton = new JButton("📅");

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblReports = new JLabel("Generate Report", SwingConstants.LEFT);
        lblReports.setFont(new Font("Arial", Font.BOLD, 18));

        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel filterLabel = new JLabel("Filter by: ");
        String[] filterOptions = {"All Orders", "Current Orders", "Completed Orders", "All Items", "Deleted Items"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterPanel.add(filterLabel);
        filterPanel.add(filterComboBox);
        filterPanel.add(new JLabel("Date:"));
        dateField.setPreferredSize(new Dimension(200, 30));
        filterPanel.add(dateField);
        filterPanel.add(calendarButton);

        // Add a search field and button to the filter panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        JButton searchButton = new JButton("Search");
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        filterPanel.add(searchPanel, BorderLayout.SOUTH);

        // Add filter panel to the top of the content area
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(lblReports, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        mainContent.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Order ID", "Date", "Requested By", "Store", "Item ID", "Name","Stocks(in boxes)", "Status"};
tableModel = new DefaultTableModel(null, columnNames) {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
};
        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(40);
        ordersTable.setShowGrid(false);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setBackground(Color.WHITE);

        JTableHeader header = ordersTable.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        mainContent.add(scrollPane, BorderLayout.CENTER);

        // Load initial data
        loadData("All Orders");

        // Add filter action listener
        filterComboBox.addActionListener(e -> {
            String selectedFilter = (String) filterComboBox.getSelectedItem();
            String dateText = dateField.getText().trim();
            loadDataWithDate(selectedFilter, dateText);
        });
        calendarButton.addActionListener(e -> {
            String input = dateField.getText();
            if (input != null && !input.trim().isEmpty()) {
                dateField.setText(input.trim());
                String selectedFilter = (String) filterComboBox.getSelectedItem();
                loadDataWithDate(selectedFilter, input.trim());
            }
            else {
                String selectedFilter = (String) filterComboBox.getSelectedItem();
                loadDataWithDate(selectedFilter, "");
            }
        });

        JButton exportCsvButton = new JButton("Export to CSV");
        exportCsvButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exportCsvButton.setBackground(new Color(110, 0, 220));
        exportCsvButton.setForeground(Color.WHITE);
        exportCsvButton.setFocusPainted(false);
        exportCsvButton.setBounds(20, 520, 160, 35);
        mainContent.add(exportCsvButton, BorderLayout.SOUTH);

        exportCsvButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save CSV File");
            int userSelection = fileChooser.showSaveDialog(null);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.endsWith(".csv")) filePath += ".csv";
                try (PrintWriter pw = new PrintWriter(new FileWriter(filePath))) {
                    // Write header
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        pw.print(tableModel.getColumnName(col));
                        if (col < tableModel.getColumnCount() - 1) pw.print(",");
                    }
                    pw.println();
                    // Write data
                    for (int row = 0; row < tableModel.getRowCount(); row++) {
                        for (int col = 0; col < tableModel.getColumnCount(); col++) {
                            Object value = tableModel.getValueAt(row, col);
                            String text = value == null ? "" : value.toString().replaceAll(",", " ");
                            pw.print(text);
                            if (col < tableModel.getColumnCount() - 1) pw.print(",");
                        }
                        pw.println();
                    }
                    JOptionPane.showMessageDialog(null, "CSV file exported successfully!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error exporting to CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Implement the search functionality
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                loadData("All Orders"); // Reload all data if search is cleared
                return;
            }

            tableModel.setRowCount(0); // Clear the table
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM (" +
                     "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, status FROM orderItems " +
                     "UNION ALL " +
                     "SELECT order_id, date, requested_by, sales_channel, item_id, name, quantity, 'Completed' as status FROM completed) AS combined " +
                     "WHERE order_id LIKE ? OR requested_by LIKE ? OR sales_channel LIKE ? OR item_id LIKE ? OR name LIKE ? OR quantity LIKE ? OR status LIKE ?")
            ) {
                String queryParam = "%" + searchText + "%";
                for (int i = 1; i <= 7; i++) {
                    preparedStatement.setString(i, queryParam);
                }

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Object[] row = {
                            resultSet.getInt("order_id"),
                            resultSet.getString("date"),
                            resultSet.getString("requested_by"),
                            resultSet.getString("sales_channel"),
                            resultSet.getInt("item_id"),
                            resultSet.getString("name"),
                            resultSet.getInt("quantity"),
                            resultSet.getString("status")
                        };
                        tableModel.addRow(row);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error performing search: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add color coding for status in the table for all views
        ordersTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                int statusCol = -1;
                for (int i = 0; i < table.getColumnCount(); i++) {
                    if ("Status".equalsIgnoreCase(table.getColumnName(i))) {
                        statusCol = i;
                        break;
                    }
                }
                if (statusCol != -1 && column == statusCol) {
                    String status = value != null ? value.toString() : "";
                    if ("Completed".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(78, 188, 137));
                        c.setForeground(Color.WHITE);
                    } else if ("Pending".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(198, 224, 118));
                        c.setForeground(Color.BLACK);
                    } else if ("Cancelled".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(255, 99, 71));
                        c.setForeground(Color.WHITE);
                    } else if ("Shipped".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(135, 206, 250));
                        c.setForeground(Color.BLACK);
                    } else if ("Delivered".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(144, 238, 144));
                        c.setForeground(Color.BLACK);
                    } else if ("Processing".equalsIgnoreCase(status)) {
                        c.setBackground(new Color(255, 215, 0));
                        c.setForeground(Color.BLACK);
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground());
                    }
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(Color.BLACK);
                    if (isSelected) {
                        c.setBackground(table.getSelectionBackground());
                        c.setForeground(table.getSelectionForeground());
                    }
                }
                if (c instanceof JComponent) {
                    ((JComponent) c).setOpaque(true);
                }
                return c;
            }
        });

        return mainContent;
    }
}
