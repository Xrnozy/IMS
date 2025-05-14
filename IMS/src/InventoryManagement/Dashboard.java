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
    public Dashboard() {
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

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 60));

        JTextField searchBar = new JTextField("Search...");
        searchBar.setPreferredSize(new Dimension(300, 30));
        JButton notificationButton = new JButton("🔔");
        notificationButton.setFocusPainted(false);
        notificationButton.setContentAreaFilled(false);
        notificationButton.setBorderPainted(false);

        JPanel headerRight = new JPanel();
        headerRight.setBackground(Color.WHITE);
        headerRight.add(searchBar);
        headerRight.add(notificationButton);
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

        // Stock Alert section

        // Add action listeners to buttons to dynamically load content
        dashboardButton.addActionListener(e -> {
            if (!(mainContent.getComponentCount() > 0 && mainContent.getComponent(0).getClass().equals(Dashboard.class))) {
                mainContent.removeAll();
                Dashboard dashboardInstance = new Dashboard();
                mainContent.add(dashboardInstance.getContentPanel(), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            }
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
            OrderManagementSystem OrderManagementSystem = new OrderManagementSystem();
            mainContent.add(OrderManagementSystem.getMainPanel(), BorderLayout.CENTER);
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
    }

    public JPanel getContentPanel() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(new Color(240, 240, 240));
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel titleLabel = new JLabel("Dashboard", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        mainContent.add(titleLabel, BorderLayout.NORTH);
        // Update the table layout to match OrderManagementSystem
        String[] columnNames = {"", "Order ID", "Date", "Requested by", "Sales Channel", "Item", "Items", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(null, columnNames) {
            private static final long serialVersionUID = 1L;

            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                }
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };

        JTable dashboardTable = new JTable(tableModel);
        dashboardTable.setRowHeight(40);
        dashboardTable.setShowGrid(false);
        dashboardTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dashboardTable.setBackground(Color.WHITE);

        JTableHeader tableHeader = dashboardTable.getTableHeader();
        tableHeader.setBackground(Color.WHITE);
        tableHeader.setFont(new Font("Arial", Font.BOLD, 12));

        dashboardTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        dashboardTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        dashboardTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        dashboardTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        dashboardTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        dashboardTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        dashboardTable.getColumnModel().getColumn(6).setPreferredWidth(50);
        dashboardTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        // Fetch data from the SQLite database
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM orderItems")) {

            // Ensure the first column contains Boolean values
            while (resultSet.next()) {
                Object[] row = {
                    Boolean.FALSE, // Checkbox column must be Boolean
                    resultSet.getString("order_id"),
                    resultSet.getString("date"),
                    resultSet.getString("requested_by"),
                    resultSet.getString("sales_channel"),
                    resultSet.getString("item"),
                    resultSet.getInt("items"),
                    resultSet.getString("status")
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error fetching data from the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Add a custom renderer for the 'Status' column
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

        dashboardTable.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());

        JScrollPane scrollPane = new JScrollPane(dashboardTable);
        mainContent.add(scrollPane, BorderLayout.CENTER);

        return mainContent;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
        });
    }
}
