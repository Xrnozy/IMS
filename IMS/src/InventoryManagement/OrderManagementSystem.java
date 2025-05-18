package InventoryManagement;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.ListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import javax.swing.Timer;

import InventoryManagement.sql.DatabaseConnection;

public class OrderManagementSystem extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField searchField;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton btnNewOrders;
    private JButton btnClearFilter;
    private JComboBox<String> comboBoxSales;
    private JComboBox<String> comboBoxStatus; // Added JComboBox for Status
    private List<Object[]> originalData = new ArrayList<>();
    private Timer refreshTimer;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    OrderManagementSystem frame = new OrderManagementSystem();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public OrderManagementSystem(String user) {
        setTitle("Order Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 10)); // Use BorderLayout for responsive resizing

        JLabel lblOrders = new JLabel("Order Management");
        lblOrders.setFont(new Font("Arial", Font.BOLD, 18));
        lblOrders.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Create a panel for the top controls (search, filters, buttons)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(lblOrders, BorderLayout.NORTH);

        // Filter/search panel (use FlowLayout for responsiveness)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        searchPanel.setPreferredSize(new Dimension(0, 40));

        searchField = new JTextField(15);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setToolTipText("Search order ID");
        searchField.setBackground(new Color(255, 255, 255));
        searchField.setBorder(BorderFactory.createLineBorder(new Color(110, 110, 110)));

        JButton searchButton = new JButton("🔍");
        searchButton.setBorder(null);
        searchButton.setBackground(Color.WHITE);
        searchButton.setFocusPainted(false);

        

        comboBoxSales = new JComboBox<>();
        comboBoxSales.setModel(new DefaultComboBoxModel<>(fetchSalesChannelsFromDatabase()));
        comboBoxSales.setPreferredSize(new Dimension(150, 30));

        comboBoxStatus = new JComboBox<>();
        comboBoxStatus.setModel(new DefaultComboBoxModel<>(fetchStatusesFromDatabase()));
        comboBoxStatus.setPreferredSize(new Dimension(100, 30));

        JButton btnFilter = new JButton("Filter");
        btnFilter.setBackground(new Color(110, 0, 220));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setBorder(null);
        btnFilter.setPreferredSize(new Dimension(80, 30));

        btnClearFilter = new JButton("Clear");
        btnClearFilter.setBackground(Color.WHITE);
        btnClearFilter.setBorder(BorderFactory.createLineBorder(new Color(110, 0, 220)));
        btnClearFilter.setForeground(new Color(110, 0, 220));
        btnClearFilter.setPreferredSize(new Dimension(80, 30));

        btnNewOrders = new JButton("+ New Orders");
        btnNewOrders.setBackground(new Color(110, 0, 220));
        btnNewOrders.setForeground(Color.WHITE);
        btnNewOrders.setBorder(null);
        btnNewOrders.setPreferredSize(new Dimension(122, 30));
        btnNewOrders.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddOrderDialog();
            }
        });

        // Add controls to searchPanel
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(comboBoxSales);
        searchPanel.add(comboBoxStatus);
        searchPanel.add(btnFilter);
        searchPanel.add(btnClearFilter);
        searchPanel.add(btnNewOrders);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        contentPane.add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {"Order ID", "Date", "Sales channel", "Item ID", "Name", "Stocks(in boxes)", "Price", "Category", "Status"};
        tableModel = new DefaultTableModel(null, columnNames) {
            private static final long serialVersionUID = 1L;
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 6) {
                    return Double.class;
                }
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // No editable columns
            }
        };

        fetchOrdersFromDatabase();

        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(40);
        ordersTable.setShowGrid(false);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setBackground(Color.WHITE);
        ordersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS); // Make columns auto-resize

        sorter = new TableRowSorter<>(tableModel);
        ordersTable.setRowSorter(sorter);

        JTableHeader header = ordersTable.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));

        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(50);
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(8).setPreferredWidth(100);

        ordersTable.getColumnModel().getColumn(8).setCellRenderer(new StatusRenderer());
        for (int i = 0; i < ordersTable.getColumnCount()-1; i++) {
            ordersTable.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    return label;
                }
            });
        }
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        contentPane.add(scrollPane, BorderLayout.CENTER);

        btnFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });

        btnClearFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFilters();
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchOrders();
            }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchOrders();
                }
            }
        });

        populateComboBoxSales(); // Dynamically populate comboBoxSales during initialization
        populateComboBoxStatus(); // Dynamically populate comboBoxStatus during initialization

        // Add real-time refresh timer
        refreshTimer = new Timer(5000, e -> fetchOrdersFromDatabase()); // 5 seconds
        refreshTimer.setCoalesce(true);
        refreshTimer.start();
    }

    private void fetchOrdersFromDatabase() {
        tableModel.setRowCount(0); // Clear table before adding new rows
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM orderItems")) {
            while (resultSet.next()) {
                Object[] row = {
                    resultSet.getString("order_id"),
                    resultSet.getString("date"),
                    resultSet.getString("sales_channel"),
                    resultSet.getString("item_id"),
                    resultSet.getString("name"),
                    resultSet.getInt("quantity"),
                    resultSet.getDouble("price"),
                    resultSet.getString("category"),
                    resultSet.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching data from the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String[] fetchSalesChannelsFromDatabase() {
        List<String> salesChannels = new ArrayList<>();
        salesChannels.add("All Sales"); // Default option
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT DISTINCT sales_channel FROM orderItems")) {
            while (resultSet.next()) {
                String channel = resultSet.getString("sales_channel");
                if (channel != null && !channel.trim().isEmpty() && !salesChannels.contains(channel)) {
                    salesChannels.add(channel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching sales channels from the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return salesChannels.toArray(new String[0]);
    }

    // Added method to fetch distinct statuses from the database
    private String[] fetchStatusesFromDatabase() {
        List<String> statuses = new ArrayList<>();
        statuses.add("All Status"); // Default option
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT DISTINCT status FROM orderItems")) {
            while (resultSet.next()) {
                String status = resultSet.getString("status");
                if (status != null && !status.trim().isEmpty() && !statuses.contains(status)) {
                    statuses.add(status);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching statuses from the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return statuses.toArray(new String[0]);
    }

    private void populateComboBoxSales() {
        String[] salesChannels = fetchSalesChannelsFromDatabase();
        comboBoxSales.setModel(new DefaultComboBoxModel<>(salesChannels));
    }

    // Added method to populate the status combobox
    private void populateComboBoxStatus() {
        String[] statuses = fetchStatusesFromDatabase();
        comboBoxStatus.setModel(new DefaultComboBoxModel<>(statuses));
    }

    private void applyFilters() {
        String salesFilter = comboBoxSales.getSelectedItem() != null ? comboBoxSales.getSelectedItem().toString() : "All Sales";
        String statusFilter = comboBoxStatus.getSelectedItem() != null ? comboBoxStatus.getSelectedItem().toString() : "All Status"; // Get selected status
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!"All Sales".equals(salesFilter)) {
            RowFilter<Object, Object> salesRowFilter = RowFilter.regexFilter("^" + salesFilter + "$", 2);
            filters.add(salesRowFilter);
        }
        // Add status filter if not "All Status"
        if (!"All Status".equals(statusFilter)) {
            RowFilter<Object, Object> statusRowFilter = RowFilter.regexFilter("^" + statusFilter + "$", 8);
            filters.add(statusRowFilter);
        }
        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filters.size() == 1) {
            sorter.setRowFilter(filters.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
        int visibleRows = ordersTable.getRowCount();
        if (visibleRows == 0) {
            JOptionPane.showMessageDialog(this,
                    "No orders match the selected filters.",
                    "Filter Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String message = "Showing " + visibleRows + " order" + (visibleRows > 1 ? "s" : "");
            if (!"All Sales".equals(salesFilter)) message += " from " + salesFilter;
            if (!"All Status".equals(statusFilter)) message += " with status: " + statusFilter; // Include status in message
            JOptionPane.showMessageDialog(this, message, "Filter Applied", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearFilters() {
        comboBoxSales.setSelectedItem("All Sales");
        comboBoxStatus.setSelectedItem("All Status"); // Clear status filter
        sorter.setRowFilter(null);
        JOptionPane.showMessageDialog(this, "Filters cleared.", "Filters", JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchOrders() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter an order ID to search",
                    "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        sorter.setRowFilter(null);

        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String orderId = tableModel.getValueAt(i, 0).toString().toLowerCase();
            if (orderId.contains(searchText)) {
                int modelRow = i;
                if (ordersTable.getRowSorter() != null) {
                    modelRow = ordersTable.convertRowIndexToView(i);
                }

                if (modelRow >= 0) {
                    ordersTable.setRowSelectionInterval(modelRow, modelRow);
                    ordersTable.scrollRectToVisible(ordersTable.getCellRect(modelRow, 0, true));
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this,
                    "No orders found with ID: " + searchText,
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void showAddOrderDialog() {
        JDialog addOrderDialog = new JDialog(this, "Add New Order", true);
        addOrderDialog.setSize(450, 500);
        addOrderDialog.setLocationRelativeTo(this);
        addOrderDialog.setLayout(new GridLayout(0, 1, 10, 10));
        addOrderDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField salesChannelField = new JTextField(20);
        JTextField itemIdField = new JTextField(10);
        JTextField nameField = new JTextField(20);
        JTextField quantityField = new JTextField(5);
        JTextField priceField = new JTextField(10);
        JTextField categoryField = new JTextField(20); // New category field
        JTextField requestedByField = new JTextField(UserSession.getLoggedInUser());
        requestedByField.setEditable(false); // Make the requested_by field uneditable

        JPanel salesChannelPanel = createLabeledField("Sales channel:", salesChannelField);
        JPanel itemIdPanel = createLabeledField("Item ID:", itemIdField);
        JPanel namePanel = createLabeledField("Name:", nameField);
        JPanel quantityPanel = createLabeledField("Quantity:", quantityField);
        JPanel pricePanel = createLabeledField("Price:", priceField);
        JPanel categoryPanel = createLabeledField("Category:", categoryField); // New panel
        JPanel requestedByPanel = createLabeledField("Requested By:", requestedByField);

        addOrderDialog.add(salesChannelPanel);
        addOrderDialog.add(itemIdPanel);
        addOrderDialog.add(namePanel);
        addOrderDialog.add(quantityPanel);
        addOrderDialog.add(pricePanel);
        addOrderDialog.add(categoryPanel); // Add to dialog
        addOrderDialog.add(requestedByPanel); // Add the requested_by field to the dialog

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> addOrderDialog.dispose());

        saveButton.addActionListener(e -> {
            if (
                salesChannelField.getText().trim().isEmpty() ||
                itemIdField.getText().trim().isEmpty() ||
                nameField.getText().trim().isEmpty() ||
                quantityField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty() ||
                categoryField.getText().trim().isEmpty()
            ) {
                JOptionPane.showMessageDialog(addOrderDialog,
                        "All fields are required!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int quantity;
            double price;
            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addOrderDialog,
                        "Quantity must be a positive number!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addOrderDialog,
                        "Price must be a non-negative number!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            Object[] newRow = {
                null, // order_id will be auto-generated
                today,
                salesChannelField.getText().trim(),
                itemIdField.getText().trim(),
                nameField.getText().trim(),
                quantity,
                price,
                categoryField.getText().trim(),
                "Pending" // Default status
            };

            // Insert the new order into the database
            try (Connection connection = DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {

                String insertSQL = String.format(
                    "INSERT INTO orderItems (date, requested_by, sales_channel, item_id, name, quantity, price, category, status) " +
                    "VALUES ('%s','%s', '%s', %s, '%s', %d, %f, '%s', 'Pending')",
                    today,
                    UserSession.getLoggedInUser(), // Fetch the username dynamically
                    salesChannelField.getText().trim(),
                    itemIdField.getText().trim(),
                    nameField.getText().trim(), quantity, price,
                    categoryField.getText().trim()
                );

                statement.executeUpdate(insertSQL);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(addOrderDialog,
                        "Error saving the order to the database: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            originalData.add(newRow);
            tableModel.addRow(newRow);

            if (sorter != null) {
                sorter.setRowFilter(sorter.getRowFilter());
            }

            addOrderDialog.dispose();

            int newRowIndex = tableModel.getRowCount() - 1;
            ordersTable.setRowSelectionInterval(newRowIndex, newRowIndex);
            ordersTable.scrollRectToVisible(ordersTable.getCellRect(newRowIndex, 0, true));

            JOptionPane.showMessageDialog(OrderManagementSystem.this,
                    "New order added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        addOrderDialog.add(buttonPanel);

        addOrderDialog.setVisible(true);
    }

    private JPanel createLabeledField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.add(label);
        panel.add(field);
        return panel;
    }

    private JPanel createLabeledField(String labelText, JComboBox<String> combo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.add(label);
        panel.add(combo);
        return panel;
    }

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);

           if ("Completed".equals(value)) {
                    label.setBackground(new Color(78, 188, 137));
                    label.setForeground(Color.WHITE);
                } else if ("Pending".equals(value)) {
                    label.setBackground(new Color(198, 224, 118));
                    label.setForeground(Color.BLACK);
                } else if ("Cancelled".equals(value)) {
                    label.setBackground(new Color(255, 99, 71)); // Tomato color for cancelled
                    label.setForeground(Color.WHITE);
                } else if ("Shipped".equals(value)) {
                    label.setBackground(new Color(135, 206, 250)); // Light blue for shipped
                    label.setForeground(Color.BLACK);
                } else if ("Delivered".equals(value)) {
                    label.setBackground(new Color(144, 238, 144)); // Light green for delivered
                    label.setForeground(Color.BLACK);
                } else if ("Processing".equals(value)) {
                    label.setBackground(new Color(255, 215, 0)); // Gold for processing
                    label.setForeground(Color.BLACK);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
                }

            label.setOpaque(true);
            return label;
        }
    }

    /**
     * Returns the main panel of the OrderManagementSystem.
     * @return JPanel representing the main content pane.
     */
    public JPanel getMainPanel() {
        return contentPane;
    }

    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        super.dispose();
    }
    /**
     * Returns a JPanel for creating a new order, with fields pre-filled from the given product details.
     * @param productId The product/item ID
     * @param productName The product name
     * @param category The product category
     * @param shop The sales channel/shop
     * @return JPanel with autofilled order form
     */
    public JPanel getContentPanelWithAutofill(String productId, String productName, String category, String shop) {
        // Create a dialog just like showAddOrderDialog, but autofilled
        JDialog autofillDialog = new JDialog(this, "Add New Order (Autofilled)", true);
        autofillDialog.setSize(450, 500);
        autofillDialog.setLocationRelativeTo(this);
        autofillDialog.setLayout(new GridLayout(0, 1, 10, 10));
        autofillDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField salesChannelField = new JTextField(shop, 20);
        JTextField itemIdField = new JTextField(productId, 10);
        JTextField nameField = new JTextField(productName, 20);
        JTextField quantityField = new JTextField("", 5); // Not autofilled
        JTextField priceField = new JTextField("", 10); // Will autofill below
        JTextField categoryField = new JTextField(category, 20);
        JTextField requestedByField = new JTextField(20);
        requestedByField.setEditable(false); // Make the requested_by field uneditable

        // Fetch the requested_by value from InventoryOverview or ProductManagement
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT requested_by FROM items WHERE item_id = '" + productId + "'");) {
            if (resultSet.next()) {
                requestedByField.setText(resultSet.getString("requested_by"));
            }
        } catch (Exception ex) {
            requestedByField.setText("Unknown"); // Default value if not found
        }

        // Autofill price from product table if possible
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT price FROM items WHERE item_id = '" + productId + "'")) {
            if (resultSet.next()) {
                priceField.setText(String.valueOf(resultSet.getDouble("price")));
            }
        } catch (Exception ex) {
            // If price can't be autofilled, leave blank
        }

        // Make itemId, name, and category not editable
        itemIdField.setEditable(false);
        nameField.setEditable(false);
        categoryField.setEditable(false);
        priceField.setEditable(false); // Make price field not editable

        JPanel salesChannelPanel = createLabeledField("Sales channel:", salesChannelField);
        JPanel itemIdPanel = createLabeledField("Item ID:", itemIdField);
        JPanel namePanel = createLabeledField("Name:", nameField);
        JPanel quantityPanel = createLabeledField("Quantity:", quantityField);
        JPanel pricePanel = createLabeledField("Price:", priceField);
        JPanel categoryPanel = createLabeledField("Category:", categoryField);
        JPanel requestedByPanel = createLabeledField("Requested By:", requestedByField);

        autofillDialog.add(salesChannelPanel);
        autofillDialog.add(itemIdPanel);
        autofillDialog.add(namePanel);
        autofillDialog.add(quantityPanel);
        autofillDialog.add(pricePanel);
        autofillDialog.add(categoryPanel);
        autofillDialog.add(requestedByPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        autofillDialog.add(buttonPanel);

        cancelButton.addActionListener(e -> autofillDialog.dispose());

        saveButton.addActionListener(e -> {
            if (
                salesChannelField.getText().trim().isEmpty() ||
                itemIdField.getText().trim().isEmpty() ||
                nameField.getText().trim().isEmpty() ||
                quantityField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty() ||
                categoryField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(autofillDialog,
                        "All fields are required!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int quantity;
            double price;
            try {
                quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(autofillDialog,
                        "Quantity must be a positive number!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                price = Double.parseDouble(priceField.getText().trim());
                if (price < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(autofillDialog,
                        "Price must be a non-negative number!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String today = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
            try (Connection connection = DatabaseConnection.getConnection();
                 Statement statement = connection.createStatement()) {
                String insertSQL = String.format(
                    "INSERT INTO orderitems (date, requested_by, sales_channel, item_id, name, quantity, price, category, status) " +
                    "VALUES ('%s', '%s', '%s', '%s', '%s', %d, %f, '%s', 'Pending')",
                    today,
                    requestedByField.getText().trim(), // Use the dynamically fetched value
                    salesChannelField.getText().trim(),
                    itemIdField.getText().trim(),
                    nameField.getText().trim(),
                    quantity,
                    price,
                    categoryField.getText().trim()
                );
                statement.executeUpdate(insertSQL);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(autofillDialog,
                        "Error saving the order to the database: " + ex.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(autofillDialog,
                    "New order added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
            autofillDialog.dispose();
        });
        autofillDialog.setVisible(true);
        // Return a dummy panel (not used, but required for compatibility)
        return new JPanel();
    }

    public OrderManagementSystem() {
        this("Default User"); // Fallback for cases where no user is provided
    }
}
