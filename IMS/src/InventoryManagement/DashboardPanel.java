
package InventoryManagement;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public DashboardPanel() {
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(40, 40, 60));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        String[] menuItems = {
                "Dashboard", "Product Management", "Inventory Overview",
                "Order Overview", "Generate Report"
        };
        for (String item : menuItems) {
            JLabel label = new JLabel(item);
            label.setForeground(Color.WHITE);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 0));
            sidebar.add(label);
        }

        JPanel content = new JPanel();
        content.setBackground(new Color(245, 248, 250));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel topButtons = new JPanel(new GridLayout(1, 4, 20, 0));
        topButtons.setOpaque(false);
        topButtons.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        topButtons.add(createCard("Products", "Product Management"));
        topButtons.add(createCard("Inventory", "Inventory Overview"));
        topButtons.add(createCard("Orders", "Order Overview"));
        topButtons.add(createCard("Report", "Generate Report"));

        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        tablesPanel.setOpaque(false);
        tablesPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        tablesPanel.add(createStockAlertPanel());
        tablesPanel.add(createRecentItemsPanel());

        content.add(topButtons);
        content.add(tablesPanel);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private JPanel createCard(String title, String label) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(180, 80));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        card.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JLabel lblText = new JLabel(label);
        lblText.setFont(new Font("SansSerif", Font.BOLD, 14));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(lblText, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStockAlertPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel("Stock Alert");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = { "Product ID", "Date", "Quantity", "Alert amt.", "Status" };
        String[][] data = {
                { "order ID", "Date", "Quantity", "Low on Stocks", "Ordering" },
                { "order ID", "Date", "Quantity", "Low on Stocks", "Order Shipped" },
                { "order ID", "Date", "Quantity", "Low on Stocks", "Order Shipped" },
        };

        JTable table = new JTable(data, columns) {

            private static final long serialVersionUID = 1L;

            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (column == 3) {
                    comp.setForeground(Color.RED);
                } else {
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        };
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRecentItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel("Recent Added Item");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        String[] columns = { "order ID", "Quantity", "Category" };
        String[][] data = {
                { "order ID", "Quantity", "Category" },
                { "order ID", "Quantity", "Category" },
                { "order ID", "Quantity", "Category" },
        };

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }
}
