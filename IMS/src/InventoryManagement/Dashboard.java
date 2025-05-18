package InventoryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import InventoryManagement.sql.DatabaseConnection;

public class Dashboard extends JFrame {
    private Timer refreshTimer;
    private DefaultTableModel tableModel;
    private DefaultTableModel stockAlertTableModel;
    private JTable dashboardTable;
    private JTable stockAlertTable;

    /**
     * Custom renderer for displaying status with specific colors and alignment.
     */
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

    // Add a field to store the logged-in user's name
    private String loggedInUser;

    // Update the constructor to accept the logged-in user's name
    public Dashboard(String user) {
        this.loggedInUser = user;

        setTitle("Inventory Management Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);

        // Sidebar navigation
        JPanel navBar = new JPanel();
        navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));
        navBar.setBackground(new Color(45, 45, 45));
        navBar.setPreferredSize(new Dimension(250, getHeight()));

        JButton dashboardButton = new JButton("Dashboard");
        JButton productManagementButton = new JButton("Product Management");
        JButton inventoryOverviewButton = new JButton("Inventory Overview");
        JButton orderOverviewButton = new JButton("Order Overview");
        JButton generateReportButton = new JButton("Generate Report");
        

        // Style buttons
        JButton[] buttons = {dashboardButton, productManagementButton, inventoryOverviewButton, orderOverviewButton, generateReportButton};
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

        // Add user name and logout button at the bottom of the navbar
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setBackground(new Color(45, 45, 45));
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel userNameLabel = new JLabel("Logged in as: " + loggedInUser);
        userNameLabel.setForeground(Color.WHITE);
        userNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose(); // Close the dashboard
                new loginFrame().setVisible(true); // Redirect to login frame
            }
        });

        userPanel.add(userNameLabel);
        userPanel.add(Box.createVerticalStrut(10));
        userPanel.add(logoutButton);
        navBar.add(Box.createVerticalGlue()); // Push user panel to the bottom
        navBar.add(userPanel);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 60));

    

        JPanel headerRight = new JPanel();
        headerRight.setBackground(Color.WHITE);
    
        JPanel headerLeft = new JPanel();
        headerLeft.setBackground(Color.WHITE);
        headerLeft.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("Inventory Management System", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLeft.add(titleLabel, BorderLayout.CENTER);
        header.add(headerRight, BorderLayout.EAST);
        header.add(headerLeft, BorderLayout.WEST);

        // Main content
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(240, 240, 240));
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Set the initial view to the dashboard
        mainContent.add(getContentPanel(), BorderLayout.CENTER);

        // Add components to frame
        getContentPane().add(navBar, BorderLayout.WEST);
        getContentPane().add(header, BorderLayout.NORTH);
        getContentPane().add(mainContent, BorderLayout.CENTER);

        // Add action listeners to buttons to dynamically load content
        dashboardButton.addActionListener(e -> {
            refreshTimer.start(); // Start updates when returning to dashboard
            mainContent.removeAll();
            mainContent.add(getContentPanel(), BorderLayout.CENTER);
            mainContent.revalidate();
            mainContent.repaint();
        });
        productManagementButton.addActionListener(e -> {
            refreshTimer.stop(); // Stop updates when leaving dashboard
            mainContent.removeAll();
            ProductManagement productManagement = new ProductManagement();
            mainContent.add(productManagement.getContentPanel(), BorderLayout.CENTER);
            mainContent.revalidate();
            mainContent.repaint();
        });
        inventoryOverviewButton.addActionListener(e -> {
            refreshTimer.stop(); // Stop updates when leaving dashboard
            mainContent.removeAll();
            InventoryOverview inventoryOverview = new InventoryOverview();
            mainContent.add(inventoryOverview.getContentPanel(), BorderLayout.CENTER);
            mainContent.revalidate();
            mainContent.repaint();
        });
        orderOverviewButton.addActionListener(e -> {
            refreshTimer.stop(); // Stop updates when leaving dashboard
            mainContent.removeAll();
            OrderManagementSystem OrderManagementSystem = new OrderManagementSystem();
            mainContent.add(OrderManagementSystem.getMainPanel(), BorderLayout.CENTER);
            mainContent.revalidate();
            mainContent.repaint();
        });
        generateReportButton.addActionListener(e -> {
            refreshTimer.stop(); // Stop updates when leaving dashboard
            mainContent.removeAll();
            GenerateReport generateReport = new GenerateReport();
            mainContent.add(generateReport.getContentPanel(), BorderLayout.CENTER);
            mainContent.revalidate();
            mainContent.repaint();
        });

     
        // Initialize refresh timer (checks every 10 seconds)
        refreshTimer = new Timer(3000, e -> refreshDashboard());
        refreshTimer.setCoalesce(true); // Combine multiple events into one

        // Add a listener to start/stop timer based on panel visibility
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshTimer.start();
            }

            @Override
            public void componentHidden(java.awt.event.ComponentEvent e) {
                refreshTimer.stop();
            }
        });
    }

    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        super.dispose();
    }

    public JPanel getContentPanel() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(240, 240, 240));
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and top section
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        JLabel titleLabel = new JLabel("Dashboard", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topSection.add(titleLabel, BorderLayout.WEST);
        mainContent.add(topSection, BorderLayout.NORTH);

        // Create split pane for orders and stock alerts
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.7); // Give more space to orders table

        // Orders table panel
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Recent Orders"));
        
        // Orders table setup
        String[] columnNames = {"Order ID", "Date", "Store", "Item ID", "Name", "Stocks(in boxes)", "Price", "Category", "Status"};
        tableModel = new DefaultTableModel(null, columnNames) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 6) return Double.class; // Price column
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        dashboardTable = new JTable(tableModel);
        setupOrdersTable();
        ordersPanel.add(new JScrollPane(dashboardTable), BorderLayout.CENTER);

        // Stock Alerts table panel
        JPanel stockAlertsPanel = new JPanel(new BorderLayout());
        stockAlertsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Stock Alerts"));

        // Stock alerts table setup
        String[] stockColumns = {"Item ID", "Name", "Stocks(in boxes)", "Stock Level"};
        stockAlertTableModel = new DefaultTableModel(null, stockColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        stockAlertTable = new JTable(stockAlertTableModel);
        setupStockAlertsTable();
        stockAlertsPanel.add(new JScrollPane(stockAlertTable), BorderLayout.CENTER);

        // Add panels to split pane
        splitPane.setLeftComponent(ordersPanel);
        splitPane.setRightComponent(stockAlertsPanel);

        mainContent.add(splitPane, BorderLayout.CENTER);

        // Load data for both tables
        refreshDashboardData();

        return mainContent;
    }

    /**
     * Sets up the orders table with appropriate configurations.
     */
    private void setupOrdersTable() {
        dashboardTable.setRowHeight(40);
        dashboardTable.setShowGrid(false);
        dashboardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dashboardTable.setBackground(Color.WHITE);

        JTableHeader tableHeader = dashboardTable.getTableHeader();
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 12));

        // Set column widths
        dashboardTable.getColumnModel().getColumn(0).setPreferredWidth(80); // Order ID
        dashboardTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Date
        dashboardTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Store
        dashboardTable.getColumnModel().getColumn(3).setPreferredWidth(80); // Item ID
        dashboardTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Name
        dashboardTable.getColumnModel().getColumn(5).setPreferredWidth(50); // Stocks
        dashboardTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Price
        // Set left alignment for Price column
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        dashboardTable.getColumnModel().getColumn(6).setCellRenderer(leftRenderer);
        dashboardTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Category
        dashboardTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Status

        // Add status renderer
        dashboardTable.getColumnModel().getColumn(8).setCellRenderer(new StatusRenderer());
    }

    /**
     * Sets up the stock alerts table with appropriate configurations.
     */
    private void setupStockAlertsTable() {
        stockAlertTable.setRowHeight(40);
        stockAlertTable.setShowGrid(false);
        stockAlertTable.setBackground(Color.WHITE);
        
        JTableHeader stockHeader = stockAlertTable.getTableHeader();
        stockHeader.setBackground(Color.WHITE);
        stockHeader.setFont(new Font("Arial", Font.BOLD, 12));

        // Stock level renderer
        class StockLevelRenderer extends DefaultTableCellRenderer {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);

                if (value != null) {
                    String level = value.toString();
                    switch (level) {
                        case "Warning":
                            label.setBackground(new Color(255, 215, 0)); // Gold
                            label.setForeground(Color.BLACK);
                            break;
                        case "Critical":
                            label.setBackground(new Color(255, 99, 71)); // Tomato red
                            label.setForeground(Color.WHITE);
                            break;
                        case "Out of Stock":
                            label.setBackground(new Color(139, 0, 0)); // Dark red
                            label.setForeground(Color.WHITE);
                            break;
                        default:
                            label.setBackground(Color.WHITE);
                            label.setForeground(Color.BLACK);
                    }
                }
                label.setOpaque(true);
                return label;
            }
        }

        stockAlertTable.getColumnModel().getColumn(3).setCellRenderer(new StockLevelRenderer());
    }

    /**
     * Refreshes the data displayed on the dashboard.
     */
    private void refreshDashboardData() {
        refreshOrdersTable();
        refreshStockAlertsTable();
    }

    /**
     * Refreshes the stock alerts table with updated data.
     */
    private void refreshStockAlertsTable() {
        stockAlertTableModel.setRowCount(0);
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM items")) {

            while (resultSet.next()) {
                // Determine stock level based on quantity
                int quantity = resultSet.getInt("quantity");
                String stockLevel = determineStockLevel(quantity);
                
                // Only add items that need attention
                if (!stockLevel.equals("Normal")) {
                    Object[] row = {
                        resultSet.getString("item_id"),
                        resultSet.getString("name"),
                        quantity,
                        stockLevel
                    };
                    stockAlertTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error fetching stock alerts: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String determineStockLevel(int quantity) {
        // Assuming each item has a default maximum stock of 100
        // You might want to make this dynamic based on each item's maximum stock level
        double percentage = (quantity / 100.0) * 100;
        
        if (quantity == 0) {
            return "Out of Stock";
        } else if (percentage <= 10) {
            return "Critical";
        } else if (percentage <= 30) {
            return "Warning";
        }
        return "Normal";
    }

    /**
     * Refreshes the orders table with updated data.
     */
    private void refreshOrdersTable() {
        tableModel.setRowCount(0);
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM orderItems")) {
            
            while (resultSet.next()) {
                String status = resultSet.getString("status");
                if ("Completed".equalsIgnoreCase(status)) {
                    continue; // Skip completed orders
                }
                Object[] row = {
                    resultSet.getString("order_id"),
                    resultSet.getString("date"),
                    resultSet.getString("sales_channel"),
                    resultSet.getString("item_id"),
                    resultSet.getString("name"),
                    resultSet.getInt("quantity"),
                    resultSet.getDouble("price"),
                    resultSet.getString("category"),
                    status
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error refreshing orders: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Refreshes the entire dashboard view.
     */
    private void refreshDashboard() {
        refreshDashboardData();
    }

    /**
     * Main method to launch the Dashboard application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard("User"); // Pass a default user for testing
            dashboard.setVisible(true);
        });
    }
}
