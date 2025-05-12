package InventoryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OrderOverview {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Order Overview");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);

            // Sidebar navigation
            JPanel navBar = new JPanel();
            navBar.setLayout(new BoxLayout(navBar, BoxLayout.Y_AXIS));
            navBar.setBackground(new Color(45, 45, 45));
            navBar.setPreferredSize(new Dimension(250, frame.getHeight()));

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
            header.setPreferredSize(new Dimension(frame.getWidth(), 60));

            JTextField searchBar = new JTextField("Search order ID");
            searchBar.setPreferredSize(new Dimension(300, 30));
            JButton notificationButton = new JButton("🔔");
            notificationButton.setFocusPainted(false);
            notificationButton.setContentAreaFilled(false);
            notificationButton.setBorderPainted(false);

            JPanel headerRight = new JPanel();
            headerRight.setBackground(Color.WHITE);
            headerRight.add(searchBar);
            headerRight.add(notificationButton);

            header.add(headerRight, BorderLayout.EAST);

            // Main content
            JPanel mainContent = new JPanel(new BorderLayout());
            mainContent.setBackground(new Color(240, 240, 240));
            mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            // Orders table
            JPanel ordersPanel = new JPanel(new BorderLayout());
            ordersPanel.setBorder(BorderFactory.createTitledBorder("Orders"));
            String[] columns = {"Order ID", "Date", "Requested by", "Sales channel", "Item", "Items", "Status"};
            Object[][] data = {
                {"#7678", "05/12/2025", "Manager Dominic", "Store name", "Laptop", 3, "Completed"},
                {"#7679", "05/12/2025", "Manager Abella", "Store name", "Laptop", 3, "Pending"},
                {"#7680", "05/12/2025", "Manager Madz", "Store name", "Laptop", 3, "Completed"},
                {"#7681", "05/12/2025", "Manager Cristof", "Store name", "Laptop", 3, "Completed"},
                {"#7682", "05/12/2025", "Manager Kevs", "Store name", "Laptop", 3, "Completed"}
            };
            JTable ordersTable = new JTable(new DefaultTableModel(data, columns));
            ordersPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

            // Add components to main content
            mainContent.add(ordersPanel, BorderLayout.CENTER);

            // Add action listeners to switch pages
            dashboardButton.addActionListener(e -> {
                mainContent.removeAll();
                Dashboard Dashboard = new Dashboard();
                mainContent.add(Dashboard.getContentPanel(), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

            productManagementButton.addActionListener(e -> {
                mainContent.removeAll();
                mainContent.add(new JLabel("Product Management Page", SwingConstants.CENTER), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

            inventoryOverviewButton.addActionListener(e -> {
                mainContent.removeAll();
                mainContent.add(new JLabel("Inventory Overview Page", SwingConstants.CENTER), BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

            orderOverviewButton.addActionListener(e -> {
                mainContent.removeAll();
                mainContent.add(ordersPanel, BorderLayout.CENTER);
                mainContent.revalidate();
                mainContent.repaint();
            });

            generateReportButton.addActionListener(e -> {
                mainContent.removeAll();
                mainContent.add(new JLabel("Generate Report Page", SwingConstants.CENTER), BorderLayout.CENTER);
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

            // Add components to frame
            frame.getContentPane().add(navBar, BorderLayout.WEST);
            frame.getContentPane().add(header, BorderLayout.NORTH);
            frame.getContentPane().add(mainContent, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }

    // Add a method to return the content panel
    public JPanel getContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBorder(BorderFactory.createTitledBorder("Orders"));
        String[] columns = {"Order ID", "Date", "Requested by", "Sales channel", "Item", "Items", "Status"};
        Object[][] data = {
            {"#7678", "05/12/2025", "Manager Dominic", "Store name", "Laptop", 3, "Completed"},
            {"#7679", "05/12/2025", "Manager Abella", "Store name", "Laptop", 3, "Pending"},
            {"#7680", "05/12/2025", "Manager Madz", "Store name", "Laptop", 3, "Completed"},
            {"#7681", "05/12/2025", "Manager Cristof", "Store name", "Laptop", 3, "Completed"},
            {"#7682", "05/12/2025", "Manager Kevs", "Store name", "Laptop", 3, "Completed"}
        };
        JTable ordersTable = new JTable(new DefaultTableModel(data, columns));
        ordersPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);
        panel.add(ordersPanel, BorderLayout.CENTER);
        return panel;
    }
}
