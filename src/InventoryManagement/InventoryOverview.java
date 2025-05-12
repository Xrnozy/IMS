package InventoryManagement;

import javax.swing.*;
import java.awt.*;

public class InventoryOverview {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Proceed to the Inventory Overview page
            JFrame frame = new JFrame("Inventory Overview");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JLabel label = new JLabel("Inventory Overview Page", SwingConstants.CENTER);
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

            navBar.add(Box.createVerticalStrut(20));
            navBar.add(productsOverviewButton);

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

    // Add a method to dynamically load content into the Dashboard
    public JPanel getContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Inventory Overview Content", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}
