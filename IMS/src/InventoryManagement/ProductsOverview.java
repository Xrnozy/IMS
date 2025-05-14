package InventoryManagement;

import javax.swing.*;
import java.awt.*;

public class ProductsOverview {

    public JPanel getContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Products Overview", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel productsGrid = new JPanel(new GridLayout(0, 2, 20, 20));
        productsGrid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Sample product data
        String[][] products = {
            {"Inverter", "cat1", "Product description", "imgs/inverter.png"},
            {"Battery", "cat2", "Product description", "imgs/battery.png"},
            {"Generator", "cat2", "Product description", "imgs/generator.png"},
            {"Charger", "cat3", "Product description", "imgs/charger.png"},
            {"Power", "cat4", "Product description", "imgs/power.png"}
        };

        for (String[] product : products) {
            JPanel productCard = new JPanel(new BorderLayout());
            productCard.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

            JLabel imageLabel = new JLabel(new ImageIcon(product[3])); // Placeholder for product image
            JLabel nameLabel = new JLabel(product[0], SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            JLabel categoryLabel = new JLabel(product[1], SwingConstants.CENTER);
            JLabel descriptionLabel = new JLabel(product[2], SwingConstants.CENTER);

            productCard.add(imageLabel, BorderLayout.NORTH);
            productCard.add(nameLabel, BorderLayout.CENTER);
            productCard.add(categoryLabel, BorderLayout.SOUTH);
            productCard.add(descriptionLabel, BorderLayout.SOUTH);

            productsGrid.add(productCard);
        }

        JScrollPane scrollPane = new JScrollPane(productsGrid);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}
