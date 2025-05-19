package InventoryManagement;

import javax.swing.*;
import java.awt.*;

public class Help {

    public JPanel getContentPanel() {
        JPanel helpPanel = new JPanel(new BorderLayout(10, 10));
        helpPanel.setBackground(new Color(245, 245, 245));
        helpPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Main Title
        JLabel titleLabel = new JLabel("Help - Color Coding Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        helpPanel.add(titleLabel, BorderLayout.NORTH);

        // Main Content Panel
        JPanel mainContentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        mainContentPanel.setBackground(new Color(245, 245, 245));

        // Orders Section
        JPanel ordersPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        ordersPanel.setBackground(new Color(245, 245, 245));
        ordersPanel.setBorder(BorderFactory.createTitledBorder("Color Coding for Orders"));

        JLabel onTheWayLabel = new JLabel("On the Way", SwingConstants.CENTER);
        onTheWayLabel.setOpaque(true);
        onTheWayLabel.setBackground(Color.WHITE); // Light blue
        onTheWayLabel.setForeground(Color.BLACK);
        onTheWayLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel pendingLabel = new JLabel("Pending", SwingConstants.CENTER);
        pendingLabel.setOpaque(true);
        pendingLabel.setBackground(new Color(198, 224, 118)); // Yellow
        pendingLabel.setForeground(Color.BLACK);
        pendingLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel completedLabel = new JLabel("Completed", SwingConstants.CENTER);
        completedLabel.setOpaque(true);
        completedLabel.setBackground(new Color(78, 188, 137)); // Green
        completedLabel.setForeground(Color.WHITE);
        completedLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        ordersPanel.add(onTheWayLabel);
        ordersPanel.add(pendingLabel);
        ordersPanel.add(completedLabel);

        // Stock Levels Section
        JPanel stockLevelsPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        stockLevelsPanel.setBackground(new Color(245, 245, 245));
        stockLevelsPanel.setBorder(BorderFactory.createTitledBorder("Color Coding for Item Stock Levels"));

        JLabel warningLabel = new JLabel("Warning (less than or equal to 30)", SwingConstants.CENTER);
        warningLabel.setOpaque(true);
        warningLabel.setBackground(new Color(255, 215, 0)); // Gold
        warningLabel.setForeground(Color.BLACK);
        warningLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel lowStocksLabel = new JLabel("Low Stocks (less than or equal to 15)", SwingConstants.CENTER);
        lowStocksLabel.setOpaque(true);
        lowStocksLabel.setBackground(new Color(255, 99, 71)); // Tomato red
        lowStocksLabel.setForeground(Color.WHITE);
        lowStocksLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel outOfStockLabel = new JLabel("Out of Stock (0)", SwingConstants.CENTER);
        outOfStockLabel.setOpaque(true);
        outOfStockLabel.setBackground(new Color(139, 0, 0)); // Dark red
        outOfStockLabel.setForeground(Color.WHITE);
        outOfStockLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel normalLabel = new JLabel("Normal (above 30 stocks)", SwingConstants.CENTER);
        normalLabel.setOpaque(true);
        normalLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        normalLabel.setBackground(Color.WHITE); // Light green
        normalLabel.setForeground(Color.BLACK);
        normalLabel.setFont(new Font("Arial", Font.PLAIN, 16));

        JLabel noteLabel = new JLabel("If the stock is below 30, you can double-click the 'Stocks (per box)' column for the item that is running low to create a new order.", SwingConstants.CENTER);
        noteLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        noteLabel.setForeground(new Color(100, 100, 100));

        stockLevelsPanel.add(warningLabel);
        stockLevelsPanel.add(lowStocksLabel);
        stockLevelsPanel.add(outOfStockLabel);
        stockLevelsPanel.add(normalLabel);
        stockLevelsPanel.add(noteLabel);

        // Add sections to main content panel
        mainContentPanel.add(ordersPanel);
        mainContentPanel.add(stockLevelsPanel);

        helpPanel.add(mainContentPanel, BorderLayout.CENTER);

        

        return helpPanel;
    }
}
