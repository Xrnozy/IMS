package InventoryManagement;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProductsOverviewApp extends JFrame {
    private JPanel contentPane;
    private JTextField searchField;
    private JComboBox<String> statusComboBox;
    private JComboBox<String> filterComboBox;
    private JPanel productsPanel;
    private JButton newProductsButton;
    private List<Product> productList = new ArrayList<>();
    private List<JPanel> productCardList = new ArrayList<>();
    // Added for sidebar
    private JPanel leftPanel;
    private JButton dashboardButton, inStockButton, productsButton, ordersButton;
    // Added for top panel
    private JButton btnGenerateReport, btnViewPastReports;
    private List<String> reportHistory = new ArrayList<>();

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ProductsOverviewApp frame = new ProductsOverviewApp();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ProductsOverviewApp() {
        setTitle("Products Overview");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1200, 700);
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(20, 20));
        // Left Panel (Sidebar)
        leftPanel = new JPanel();
        leftPanel.setBackground(Color.LIGHT_GRAY);
        leftPanel.setPreferredSize(new Dimension(90, getHeight()));
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        contentPane.add(leftPanel, BorderLayout.WEST);
        // Add buttons to the left panel
        dashboardButton = createSideBarButton("Dashboard");
        inStockButton = createSideBarButton("In Stock");
        productsButton = createSideBarButton("Products");
        ordersButton = createSideBarButton("Orders");
        // make product button selected.
        productsButton.setBackground(new Color(41, 128, 185));
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(dashboardButton);
        leftPanel.add(inStockButton);
        leftPanel.add(productsButton);
        leftPanel.add(ordersButton);
        leftPanel.add(Box.createVerticalGlue());
        // Top Panel (Search, Status, Filter, New Products)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(20, 0));
        JPanel searchAndFilterPanel = new JPanel();
        searchAndFilterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.add(searchAndFilterPanel, BorderLayout.WEST);
        searchField = new JTextField();
        searchField.setToolTipText("Search product name");
        searchField.setPreferredSize(new Dimension(250, 30));
        searchAndFilterPanel.add(searchField);
        statusComboBox = new JComboBox<>(new String[] { "Status", "In Stock", "Out of Stock",
                "Discontinued" });
        statusComboBox.setPreferredSize(new Dimension(120, 30));
        searchAndFilterPanel.add(statusComboBox);
        filterComboBox = new JComboBox<>(new String[] { "Filter", "Category", "Price",
                "Manufacturer" });
        filterComboBox.setPreferredSize(new Dimension(120, 30));
        searchAndFilterPanel.add(filterComboBox);
        newProductsButton = new JButton("+ New Products");
        newProductsButton.setBackground(new Color(110, 0, 220));
        newProductsButton.setForeground(Color.WHITE);
        newProductsButton.setFont(new Font("Arial", Font.BOLD, 14));
        newProductsButton.setPreferredSize(new Dimension(150, 30));
        // Add generate report and view past report button.
        JPanel reportButtonPanel = new JPanel();
        reportButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        btnGenerateReport = new JButton("Generate Report");
        btnGenerateReport.setBackground(new Color(110, 0, 220));
        btnGenerateReport.setForeground(Color.WHITE);
        reportButtonPanel.add(btnGenerateReport);
        btnViewPastReports = new JButton("View Past Reports");
        btnViewPastReports.setBackground(new Color(110, 0, 220));
        btnViewPastReports.setForeground(Color.WHITE);
        reportButtonPanel.add(btnViewPastReports);
        topPanel.add(searchAndFilterPanel, BorderLayout.WEST);
        topPanel.add(newProductsButton, BorderLayout.EAST);
        topPanel.add(reportButtonPanel, BorderLayout.SOUTH);
        contentPane.add(topPanel, BorderLayout.NORTH);
        // Products Panel (Displays Product Cards)
        productsPanel = new JPanel();
        productsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        contentPane.add(new JScrollPane(productsPanel), BorderLayout.CENTER);
        // Sample Products (Moved to a separate method)
        createSampleProducts();
        // Event Listeners
        newProductsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Implement the action to open a new product dialog/form
                JOptionPane.showMessageDialog(ProductsOverviewApp.this, "Open New Product Form");
            }
        });
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterProducts();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterProducts();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterProducts();
            }
        });
        statusComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterProducts();
            }
        });
        filterComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterProducts();
            }
        });
        btnGenerateReport.addActionListener(e -> generateReport());
        btnViewPastReports.addActionListener(e -> viewPastReports());
    }

    private void createSampleProducts() {
        // Sample product data
        productList.add(new Product("Inverter", "cat1", "Product Description", "inverter.png"));
        productList.add(new Product("Battery", "cat2", "Product Description", "battery.png"));
        productList.add(new Product("Generator", "cat2", "Product Description", "generator.png"));
        productList.add(new Product("Charger", "cat3", "Product Description", "charger.png"));
        productList.add(new Product("Power", "cat4", "Product Description", "power.png"));
        // Create and add product cards
        for (Product product : productList) {
            JPanel productCard = createProductCard(product);
            productsPanel.add(productCard);
            productCardList.add(productCard);
        }
    }

    private void filterProducts() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = (String) statusComboBox.getSelectedItem();
        String selectedFilter = (String) filterComboBox.getSelectedItem();
        for (int i = 0; i < productList.size(); i++) {
            Product product = productList.get(i);
            JPanel card = productCardList.get(i);
            boolean matchesSearch = product.getName().toLowerCase().contains(searchText) ||
                    product.getDescription().toLowerCase().contains(searchText);
            boolean matchesStatus = selectedStatus.equals("Status") ||
                    (selectedStatus.equals("In Stock") && i < 3) ||
                    (selectedStatus.equals("Out of Stock") && i >= 3);
            boolean matchesFilter = selectedFilter.equals("Filter") ||
                    product.getCategory().equals(selectedFilter.substring(3).toLowerCase());
            card.setVisible(matchesSearch && matchesStatus && matchesFilter);
        }
        productsPanel.revalidate();
        productsPanel.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        card.setPreferredSize(new Dimension(200, 250));
        // Load the image (assuming images are in the same folder)
        ImageIcon imageIcon = null;
        try {
            File imageFile = new File(product.getImagePath());
            if (imageFile.exists()) {
                imageIcon = new ImageIcon(imageFile.getAbsolutePath());
            } else {
                System.err.println("Couldn't find file: " + product.getImagePath());
                imageIcon = new ImageIcon(new File("placeholder.png").getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            imageIcon = new ImageIcon(new File("placeholder.png").getAbsolutePath());
        }
        Image scaledImage = imageIcon.getImage().getScaledInstance(150, 100,
                Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        card.add(imageLabel, BorderLayout.NORTH);
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(3, 1));
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel categoryLabel = new JLabel(product.getCategory());
        categoryLabel.setForeground(Color.GRAY);
        JLabel descriptionLabel = new JLabel("<html><body style='width: 150px'>" +
                product.getDescription() + "</body></html>");
        infoPanel.add(nameLabel);
        infoPanel.add(categoryLabel);
        infoPanel.add(descriptionLabel);
        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }

    // Inner class for Product
    private class Product {
        private String name;
        private String category;
        private String description;
        private String imagePath;

        public Product(String name, String category, String description, String imagePath) {
            this.name = name;
            this.category = category;
            this.description = description;
            this.imagePath = imagePath;
        }

        public String getName() {
            return name;
        }

        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return description;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    private JButton createSideBarButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(Color.LIGHT_GRAY);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 12)); // Changed font size to 12
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 40));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle button clicks here
                if (text.equals("Dashboard")) {
                    JOptionPane.showMessageDialog(ProductsOverviewApp.this, "Dashboard clicked");
                } else if (text.equals("In Stock")) {
                    JOptionPane.showMessageDialog(ProductsOverviewApp.this, "In Stock clicked");
                } else if (text.equals("Products")) {
                    JOptionPane.showMessageDialog(ProductsOverviewApp.this, "Products clicked");
                } else if (text.equals("Orders")) {
                    JOptionPane.showMessageDialog(ProductsOverviewApp.this, "Orders clicked");
                }
            }
        });
        return button;
    }

    private void generateReport() {
        StringBuilder report = new StringBuilder();
        for (int i = 0; i < productList.size(); i++) {
            JPanel card = productCardList.get(i);
            if (card.isVisible()) {
                report.append("Name: ").append(productList.get(i).getName()).append(" | ");
                report.append("Category: ").append(productList.get(i).getCategory()).append(" | ");
                report.append("Description:").append(productList.get(i).getDescription()).append("\n");
            }
        }
        if (report.length() == 0) {
            JOptionPane.showMessageDialog(this, "No products selected for the report.");
            return;
        }
        String generated = "Report generated on " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date())
                + "\n" + report.toString();
        reportHistory.add(generated);
        JOptionPane.showMessageDialog(this, "Report generated:\n\n" + generated);
    }

    private void viewPastReports() {
        String historyText = String.join("\n\n", reportHistory);
        JOptionPane.showMessageDialog(this, historyText.isEmpty() ? "No past reports found." : historyText,
                "Past Reports", JOptionPane.INFORMATION_MESSAGE);
    }
}