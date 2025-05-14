package InventoryManagement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class GenerateReport {
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
            mainContent.add(lblReports, BorderLayout.NORTH);

            String[] columnNames = {"Select", "Order ID", "Date", "Requested By", "Sales Channel", "Item", "Items"};
            DefaultTableModel tableModel = new DefaultTableModel(null, columnNames) {
                @Override
                public Class<?> getColumnClass(int column) {
                    if (column == 0) return Boolean.class;
                    return String.class;
                }

                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 0;
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

            frame.getContentPane().add(mainContent, BorderLayout.CENTER);

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

            frame.setVisible(true);
        });
    }

    // Add a method to dynamically load content into the Dashboard
    public JPanel getContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Generate Report Content", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
