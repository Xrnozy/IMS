import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.sql.*;
import java.io.PrintWriter;

public class POS_Admin extends JFrame {
    // Hardcoded admin credentials
    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "admin123";

    // Data structures
    private java.util.List<Product> products = new ArrayList<>();
    private java.util.List<SaleItem> cart = new ArrayList<>();
    private java.util.List<SaleTransaction> transactions = new ArrayList<>();

    // Discount information
    private String discountHolderName = "";
    private String discountId = "";
    private boolean discountActive = false; // Add this field to track if a discount is active

    // UI components
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    // Login components
    private JTextField usernameField = new JTextField(8);
    private JPasswordField passwordField = new JPasswordField(8);
    private JLabel loginStatusLabel = new JLabel(" ");

    // Product info components
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTextField prodNameField = new JTextField(15);
    private JTextField prodPriceField = new JTextField(10);
    private JTextField prodDescField = new JTextField(12);
    private JTextField prodBrandField = new JTextField(10);
    private JTextField prodQtyField = new JTextField(5);

    // Sales transaction components
    private JComboBox<Product> productComboBox = new JComboBox<>(); // Dropdown for product selection
    private JTextField saleQuantityField = new JTextField(5);
    private JButton addToCartBtn;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel = new JLabel("Total: ₱0.00");

    // Billing and payment
    private JTextField cashReceivedField = new JTextField(10);
    private JLabel changeLabel = new JLabel("₱0.00");
    private JLabel totalAmountLabel = new JLabel("₱0.00");
    private JLabel discountedTotalLabel = new JLabel("₱0.00"); 
    private JButton payBtn;

    // Receipt area
    private JTextArea receiptArea = new JTextArea(20, 40);

    // Return item system
    private JTextField returnTransactionIdField = new JTextField(10);
    private JTextField returnProdIdField = new JTextField(10);
    private JTextField returnQuantityField = new JTextField(5);
    private JButton returnItemBtn;
    private JLabel returnStatusLabel = new JLabel(" ");

    // Status bar
    private JLabel statusBar = new JLabel("Welcome to POS Admin System");

    private Connection conn;

    public POS_Admin() {
        setTitle("Point of Sales - Admin Only");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // Initialize with some sample products
        products.add(new Product(1, "Corn Seed", "High-yield hybrid corn seed", "Sneed's", 100, 120.0));
        products.add(new Product(2, "Wheat Seed", "Premium spring wheat seed", "Chuck's", 80, 95.0));
        products.add(new Product(3, "Soybean Seed", "Non-GMO soybean seed", "Sneed's", 60, 110.0));

        // Populate productComboBox
        refreshProductComboBox();

        // Login Panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        // --- BIG ASS TEXT ---
        JLabel titleLabel = new JLabel("SNEED'S SEED AND FEED (formerly Chuck's)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26)); // Big bold font
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        loginPanel.add(titleLabel, c);

        // --- Login fields ---
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        c.gridy = 1;
        c.gridx = 0;
        loginPanel.add(new JLabel("Username:"), c);
        c.gridx = 1;
        loginPanel.add(usernameField, c);
        c.gridx = 0;
        c.gridy = 2;
        loginPanel.add(new JLabel("Password:"), c);
        c.gridx = 1;
        loginPanel.add(passwordField, c);
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        JButton loginBtn = new JButton("Login");
        loginPanel.add(loginBtn, c);
        c.gridy = 4;
        loginPanel.add(loginStatusLabel, c);

        loginBtn.addActionListener(e -> doLogin());

        mainPanel.add(loginPanel, "login");

        // Admin main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Product Info Panel
        JPanel productPanel = new JPanel(new BorderLayout(10, 10));
        productTableModel = new DefaultTableModel(
            new Object[] { "ID", "Name", "Description", "Brand", "Quantity", "Price (₱)" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(productTableModel);
        refreshProductTable();
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel prodForm = new JPanel();
        prodForm.add(new JLabel("Name:"));
        prodForm.add(prodNameField);
        prodForm.add(new JLabel("Price (₱):"));
        prodForm.add(prodPriceField);
        prodForm.add(new JLabel("Description:"));
        prodForm.add(prodDescField);
        prodForm.add(new JLabel("Brand:"));
        prodForm.add(prodBrandField);
        prodForm.add(new JLabel("Quantity:"));
        prodForm.add(prodQtyField);
        JButton addProdBtn = new JButton("Add Product");
        prodForm.add(addProdBtn);
        addProdBtn.addActionListener(e -> addProduct());
        productPanel.add(prodForm, BorderLayout.SOUTH);
        tabbedPane.addTab("Product Info", productPanel);

        // Sales Transaction Panel
        JPanel salesPanel = new JPanel(new BorderLayout(10, 10));
        JPanel addCartPanel = new JPanel();
        addCartPanel.add(new JLabel("Product:"));
        addCartPanel.add(productComboBox); // Add the combo box instead of the text field
        addCartPanel.add(new JLabel("Quantity:"));
        addCartPanel.add(saleQuantityField);
        addToCartBtn = new JButton("Add To Cart");
        addCartPanel.add(addToCartBtn);
        salesPanel.add(addCartPanel, BorderLayout.NORTH);

        cartTableModel = new DefaultTableModel(
                new Object[] { "Product", "Price (₱)", "Quantity", "Subtotal (₱)" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        salesPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel saleBottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saleBottomPanel.add(totalLabel);
        salesPanel.add(saleBottomPanel, BorderLayout.SOUTH);

        addToCartBtn.addActionListener(e -> {
            addToCart();
            updateBillingPanel();
        });

        tabbedPane.addTab("Sales Transaction", salesPanel);

        // Billing and Payment Panel
        JPanel billingPanel = new JPanel(new GridBagLayout());
        GridBagConstraints bc = new GridBagConstraints();
        bc.insets = new Insets(8, 8, 8, 8);
        bc.gridx = 0;
        bc.gridy = 0;
        billingPanel.add(new JLabel("Total Amount: "), bc);
        bc.gridx = 1;
        billingPanel.add(totalAmountLabel, bc);

        bc.gridx = 0;
        bc.gridy = 1;
        billingPanel.add(new JLabel("Discounted Total: "), bc); // Correctly add discounted total label
        bc.gridx = 1;
        billingPanel.add(discountedTotalLabel, bc);

        bc.gridx = 0;
        bc.gridy = 2;
        billingPanel.add(new JLabel("Cash Received: "), bc);
        bc.gridx = 1;
        billingPanel.add(cashReceivedField, bc);

        bc.gridx = 0;
        bc.gridy = 3;
        billingPanel.add(new JLabel("Change: "), bc); // Correctly add change label
        bc.gridx = 1;
        billingPanel.add(changeLabel, bc);

        bc.gridx = 0;
        bc.gridy = 4;
        bc.gridwidth = 2;
        bc.anchor = GridBagConstraints.CENTER;
        payBtn = new JButton("Pay");
        billingPanel.add(payBtn, bc);

        payBtn.addActionListener(e -> doPayment());

        tabbedPane.addTab("Billing & Payment", billingPanel);

        // Receipt Panel
        JPanel receiptPanel = new JPanel(new BorderLayout());
        receiptArea.setEditable(false);
        receiptPanel.add(new JScrollPane(receiptArea), BorderLayout.CENTER);
        JButton clearReceiptBtn = new JButton("Clear Receipt");
        receiptPanel.add(clearReceiptBtn, BorderLayout.SOUTH);
        clearReceiptBtn.addActionListener(e -> receiptArea.setText(""));
        tabbedPane.addTab("Receipt", receiptPanel);

        // Return Item Panel
        JPanel returnPanel = new JPanel(new GridBagLayout());
        GridBagConstraints rc = new GridBagConstraints();
        rc.insets = new Insets(6, 6, 6, 6);
        rc.fill = GridBagConstraints.HORIZONTAL;

        rc.gridx = 0;
        rc.gridy = 0;
        returnPanel.add(new JLabel("Transaction ID:"), rc);
        rc.gridx = 1;
        returnPanel.add(returnTransactionIdField, rc);
        rc.gridx = 0;
        rc.gridy = 1;
        returnPanel.add(new JLabel("Product ID:"), rc);
        rc.gridx = 1;
        returnPanel.add(returnProdIdField, rc);
        rc.gridx = 0;
        rc.gridy = 2;
        returnPanel.add(new JLabel("Return Quantity:"), rc);
        rc.gridx = 1;
        returnPanel.add(returnQuantityField, rc);
        rc.gridx = 0;
        rc.gridy = 3;
        rc.gridwidth = 2;
        rc.anchor = GridBagConstraints.CENTER;
        returnItemBtn = new JButton("Return Item");
        returnPanel.add(returnItemBtn, rc);
        rc.gridy = 4;
        returnPanel.add(returnStatusLabel, rc);

        returnItemBtn.addActionListener(e -> processReturn());

        tabbedPane.addTab("Return Item", returnPanel);

        // Discount Panel
        JPanel discountPanel = new JPanel(new GridBagLayout());
        GridBagConstraints dc = new GridBagConstraints();
        dc.insets = new Insets(6, 6, 6, 6);
        dc.fill = GridBagConstraints.HORIZONTAL;

        dc.gridx = 0;
        dc.gridy = 0;
        discountPanel.add(new JLabel("Name:"), dc);
        JTextField discountNameField = new JTextField(15);
        dc.gridx = 1;
        discountPanel.add(discountNameField, dc);

        dc.gridx = 0;
        dc.gridy = 1;
        discountPanel.add(new JLabel("Discount ID (12 digits):"), dc);
        JTextField discountIdField = new JTextField(15);
        dc.gridx = 1;
        discountPanel.add(discountIdField, dc);

        dc.gridx = 0;
        dc.gridy = 2;
        dc.gridwidth = 2;
        dc.anchor = GridBagConstraints.CENTER;
        JButton applyDiscountBtn = new JButton("Apply Discount");
        discountPanel.add(applyDiscountBtn, dc);

        JLabel discountStatusLabel = new JLabel(" ");
        dc.gridy = 3;
        discountPanel.add(discountStatusLabel, dc);

        applyDiscountBtn.addActionListener(e -> {
            String name = discountNameField.getText().trim();
            String discountIdInput = discountIdField.getText().trim();

            if (name.isEmpty() || discountIdInput.isEmpty()) {
                discountStatusLabel.setText("Please enter both name and discount ID.");
                return;
            }

            if (discountIdInput.length() != 12 || !discountIdInput.matches("\\d+")) {
                discountStatusLabel.setText("Discount ID must be a 12-digit number.");
                return;
            }

            // Store discount information
            discountHolderName = name;
            discountId = discountIdInput;
            discountActive = true; // Mark discount as active

            // Apply 12% discount
            double total = 0;
            for (SaleItem item : cart) {
                total += item.product.getPrice() * item.quantity;
            }
            double discountedTotal = total * 0.88; // 12% discount
            totalAmountLabel.setText(String.format("₱%.2f", total));
            discountedTotalLabel.setText(String.format("₱%.2f", discountedTotal)); // Update discounted total
            discountStatusLabel.setText("12% discount applied successfully!");

            // Clear fields after applying discount
            discountNameField.setText("");
            discountIdField.setText("");
        });

        tabbedPane.addTab("Discount", discountPanel);

        mainPanel.add(tabbedPane, "pos");

        add(mainPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
        cardLayout.show(mainPanel, "login");

        connectDatabase();
        loadProductsFromDatabase();
    }

    private void connectDatabase() {
        try {
            // Load MySQL JDBC driver (optional for JDBC 4+)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Update the connection string for MySQL
            // Replace "root", "password", and "pos_admin" with your actual MySQL username, password, and database name
            conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/pos_admin?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
                "root", // your MySQL username
                "" // your MySQL password
            );

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "name VARCHAR(255) UNIQUE," + // Add UNIQUE constraint here
                    "description VARCHAR(255)," +
                    "brand VARCHAR(255)," +
                    "quantity INT," +
                    "price DOUBLE)");

            // After table creation, insert mockup data (duplicates will be ignored)
            String[] mockNames = {"Corn Seed", "Wheat Seed", "Soybean Seed", "Rice Seed", "Barley Seed"};
            String[][] mockData = {
                {"Corn Seed", "High-yield hybrid corn seed", "Sneed's", "100", "120.0"},
                {"Wheat Seed", "Premium spring wheat seed", "Chuck's", "80", "95.0"},
                {"Soybean Seed", "Non-GMO soybean seed", "Sneed's", "60", "110.0"},
                {"Rice Seed", "Organic rice seed", "Chuck's", "50", "130.0"},
                {"Barley Seed", "Barley for brewing", "Barley Bros", "40", "150.0"}
            };
            for (String[] prod : mockData) {
                PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM products WHERE name = ?");
                ps.setString(1, prod[0]);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) == 0) {
                    PreparedStatement insert = conn.prepareStatement(
                        "INSERT INTO products (name, description, brand, quantity, price) VALUES (?, ?, ?, ?, ?)");
                    insert.setString(1, prod[0]);
                    insert.setString(2, prod[1]);
                    insert.setString(3, prod[2]);
                    insert.setInt(4, Integer.parseInt(prod[3]));
                    insert.setDouble(5, Double.parseDouble(prod[4]));
                    insert.executeUpdate();
                    insert.close();
                }
                rs.close();
                ps.close();
            }
            stmt.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection error: " + e.getMessage());
        }
    }

    private void loadProductsFromDatabase() {
        products.clear();
        try (Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
        while (rs.next()) {
            products.add(new Product(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getString("brand"),
                rs.getInt("quantity"),
                rs.getDouble("price")
            ));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
    }
    refreshProductComboBox();
    refreshProductTable();
}

private void addProductToDatabase(Product product) {
    try (PreparedStatement ps = conn.prepareStatement(
            "INSERT INTO products (name, description, brand, quantity, price) VALUES (?, ?, ?, ?, ?)")) {
        ps.setString(1, product.getName());
        ps.setString(2, product.getDescription());
        ps.setString(3, product.getBrand());
        ps.setInt(4, product.getQuantity());
        ps.setDouble(5, product.getPrice());
        ps.executeUpdate();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error adding product: " + e.getMessage());
    }
}

private void updateProductQuantityInDatabase(int productId, int newQuantity) {
    try (PreparedStatement ps = conn.prepareStatement(
            "UPDATE products SET quantity = ? WHERE id = ?")) {
        ps.setInt(1, newQuantity);
        ps.setInt(2, productId);
        ps.executeUpdate();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error updating product quantity: " + e.getMessage());
    }
}

    private void updateBillingPanel() {
        double total = 0;
        for (SaleItem item : cart) {
            double subtotal = item.product.getPrice() * item.quantity;
            total += subtotal;
        }
        totalAmountLabel.setText(String.format("₱%.2f", total));
        // Only update discounted total if discount is active, else set it to total
        if (discountActive) {
            double discountedTotal = total * 0.88; // 12% discount
            discountedTotalLabel.setText(String.format("₱%.2f", discountedTotal));
        } else {
            discountedTotalLabel.setText(String.format("₱%.2f", total));
        }
    }

    private void doPayment() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty. Add products before payment.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String cashText = cashReceivedField.getText().trim();
        if (cashText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter cash received.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double cash = Double.parseDouble(cashText);
            double total = Double.parseDouble(totalAmountLabel.getText().replace("₱", ""));
            double discountedTotal = Double.parseDouble(discountedTotalLabel.getText().replace("₱", ""));
            double discountAmount = total - discountedTotal;

            if (cash < discountedTotal) {
                JOptionPane.showMessageDialog(this, "Cash received is less than discounted total amount.", "Payment Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            double change = cash - discountedTotal;
            changeLabel.setText(String.format("₱%.2f", change)); // Update change label

            // Save transaction
            int transId = transactions.size() + 1;
            SaleTransaction trans = new SaleTransaction(
                transId,
                new ArrayList<>(cart),
                discountedTotal,
                cash,
                change,
                new java.util.Date() // <-- Explicitly use java.util.Date
            );
            transactions.add(trans);
            for (SaleItem item : cart) {
                int newQty = item.product.getQuantity() - item.quantity;
                updateProductQuantityInDatabase(item.product.getId(), newQty);
            }
            loadProductsFromDatabase(); // <-- Add this line to refresh products after sale

            // Generate receipt with stored discount details
            generateReceipt(trans, discountId, discountHolderName, "12%", 0, false);

            // Clear cart and reset fields
            cart.clear();
            refreshCartTable();
            cashReceivedField.setText("");
            saleQuantityField.setText("");
            updateBillingPanel();
            discountActive = false; // Reset discount
            discountHolderName = "";
            discountId = "";
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cash received must be a number.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveReceiptToFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(fileChooser.getSelectedFile())) {
                writer.write(receiptArea.getText());
                JOptionPane.showMessageDialog(this, "Receipt saved successfully!", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving receipt: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            loginStatusLabel.setText("Login successful.");
            statusBar.setText("Logged in as admin. Ready to manage the POS system.");
            cardLayout.show(mainPanel, "pos");
        } else {
            loginStatusLabel.setText("Invalid username or password.");
        }
    }

    private void refreshProductTable() {
        productTableModel.setRowCount(0);
        for (Product p : products) {
            productTableModel.addRow(new Object[] {
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getBrand(),
                p.getQuantity(),
                String.format("₱%.2f", p.getPrice())
            });
        }
    }

    private void refreshProductComboBox() {
        productComboBox.removeAllItems(); // Clear existing items
        for (Product product : products) {
            productComboBox.addItem(product); // Add each product to the combo box
        }
    }

    private void addProduct() {
        String name = prodNameField.getText().trim();
        String desc = prodDescField.getText().trim();
        String brand = prodBrandField.getText().trim();
        String qtyText = prodQtyField.getText().trim();
        String priceText = prodPriceField.getText().trim();
        if (name.isEmpty() || desc.isEmpty() || brand.isEmpty() || qtyText.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all product fields.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int qty = Integer.parseInt(qtyText);
            double price = Double.parseDouble(priceText);
            Product newProduct = new Product(0, name, desc, brand, qty, price);
            addProductToDatabase(newProduct);
            loadProductsFromDatabase();
            prodNameField.setText("");
            prodDescField.setText("");
            prodBrandField.setText("");
            prodQtyField.setText("");
            prodPriceField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity and Price must be valid numbers.", "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addToCart() {
        Product selectedProduct = (Product) productComboBox.getSelectedItem(); // Get the selected product
        String quantityText = saleQuantityField.getText().trim();

        if (selectedProduct == null || quantityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a product and enter quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if product already in cart and calculate total quantity in cart for this product
            int quantityInCart = 0;
            for (SaleItem item : cart) {
                if (item.product.getId() == selectedProduct.getId()) {
                    quantityInCart += item.quantity;
                }
            }
            // Check if requested quantity + in-cart quantity exceeds available stock
            if (quantity + quantityInCart > selectedProduct.getQuantity()) {
                JOptionPane.showMessageDialog(this,
                    "Not enough stock! Available: " + selectedProduct.getQuantity() +
                    ", In Cart: " + quantityInCart,
                    "Stock Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Add or update cart
            boolean foundInCart = false;
            for (SaleItem item : cart) {
                if (item.product.getId() == selectedProduct.getId()) {
                    item.quantity += quantity;
                    foundInCart = true;
                    break;
                }
            }
            if (!foundInCart) {
                cart.add(new SaleItem(selectedProduct, quantity, 0)); // Discount is always 0
            }
            refreshCartTable();
            saleQuantityField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0;
        for (SaleItem item : cart) {
            double subtotal = item.product.getPrice() * item.quantity;
            cartTableModel.addRow(new Object[] { item.product.getName(), String.format("$%.2f", item.product.getPrice()),
                    item.quantity, String.format("$%.2f", subtotal) });
            total += subtotal;
        }
        totalLabel.setText("Total: $" + String.format("%.2f", total));
    }

    private void generateReceipt(SaleTransaction trans, String discountId, String discountHolderName, String discountPercentage, double refundAmount, boolean isReturn) {
        StringBuilder sb = new StringBuilder();
        sb.append("----- RECEIPT -----\n");
        sb.append("Transaction #: ").append(trans.getTransactionId()).append("\n");
        sb.append("Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(trans.getDate())).append("\n\n");
        sb.append(String.format("%-20s%-10s%-10s%-10s\n", "Product", "Price", "Qty", "Subtotal"));

        for (SaleItem item : trans.getItems()) {
            double subtotal = item.product.getPrice() * item.quantity;
            sb.append(String.format("%-20s₱%-9.2f%-10d₱%-10.2f\n", item.product.getName(), item.product.getPrice(),
                    item.quantity, subtotal));
        }

        sb.append("\nTotal: ₱").append(String.format("%.2f", trans.getTotalAmount())).append("\n");

        // Add discount details to the receipt
        if (discountId != null && !discountId.isEmpty()) {
            sb.append("Discount Holder: ").append(discountHolderName).append("\n");
            sb.append("Discount ID: ").append(discountId).append("\n");
            sb.append("Discount Applied: ").append(discountPercentage).append("\n");
            sb.append("New Total (Discounted): ₱").append(String.format("%.2f", trans.getTotalAmount())).append("\n");
        }

        sb.append("Cash Received: ₱").append(String.format("%.2f", trans.getCashReceived())).append("\n");
        sb.append("Change: ₱").append(String.format("%.2f", trans.getChange())).append("\n");

        // Show refund details if this is a return
        if (isReturn) {
            sb.append("\n*** RETURN PROCESSED ***\n");
            sb.append("Refund Amount: ₱").append(String.format("%.2f", refundAmount)).append("\n");
            sb.append("-------------------\n");
        } else {
            sb.append("-------------------\n");
        }

        receiptArea.setText(sb.toString());
    }

    private void processReturn() {
        returnStatusLabel.setText(" ");
        String transIdText = returnTransactionIdField.getText().trim();
        String prodIdText = returnProdIdField.getText().trim();
        String qtyText = returnQuantityField.getText().trim();

        if (transIdText.isEmpty() || prodIdText.isEmpty() || qtyText.isEmpty()) {
            returnStatusLabel.setText("Please fill all return fields.");
            return;
        }
        try {
            int transId = Integer.parseInt(transIdText);
            int prodId = Integer.parseInt(prodIdText);
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                returnStatusLabel.setText("Return quantity must be positive.");
                return;
            }
            SaleTransaction trans = null;
            for (SaleTransaction t : transactions) {
                if (t.getTransactionId() == transId) {
                    trans = t;
                    break;
                }
            }
            if (trans == null) {
                returnStatusLabel.setText("Transaction not found.");
                return;
            }
            SaleItem itemToReturn = null;
            for (SaleItem item : trans.getItems()) {
                if (item.product.getId() == prodId) {
                    itemToReturn = item;
                    break;
                }
            }
            if (itemToReturn == null) {
                returnStatusLabel.setText("Product not found in transaction.");
                return;
            }
            if (qty > itemToReturn.quantity) {
                returnStatusLabel.setText("Return quantity exceeds purchased quantity.");
                return;
            }
            // Process return - reduce quantity or remove item
            itemToReturn.quantity -= qty;
            if (itemToReturn.quantity == 0) {
                trans.getItems().remove(itemToReturn);
            }

            // Update product quantity in database and reload products
            int newQty = itemToReturn.product.getQuantity() + qty;
            updateProductQuantityInDatabase(itemToReturn.product.getId(), newQty);
            loadProductsFromDatabase();

            // Refund amount to receipt display
            double refundAmount = itemToReturn.product.getPrice() * qty;
            String refundText = String.format("Refund amount: $%.2f", refundAmount);

            returnStatusLabel.setText("Item returned successfully. " + refundText);

            // Recalculate total after return
            double newTotal = 0;
            for (SaleItem item : trans.getItems()) {
                newTotal += item.product.getPrice() * item.quantity;
            }
            trans.totalAmount = newTotal;

            // Optionally, keep cashReceived the same, but recalculate change
            trans.change = trans.cashReceived - newTotal;

            // Regenerate receipt for the transaction with updated items
            generateReceipt(trans, null, null, "0%", refundAmount, true);

        } catch (NumberFormatException e) {
            returnStatusLabel.setText("Transaction ID, Product ID and Quantity must be numbers.");
        }
    }

    // Inner classes for product and sale items
    private static class Product {
        private int id;
        private String name;
        private String description;
        private String brand;
        private int quantity;
        private double price;

        public Product(int id, String name, String description, String brand, int quantity, double price) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.brand = brand;
            this.quantity = quantity;
            this.price = price;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getBrand() { return brand; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }

        @Override
        public String toString() {
            return name + " (" + brand + ", ₱" + price + ")";
        }
    }

    private static class SaleItem {
        private Product product;
        private int quantity;
        private double discount;

        public SaleItem(Product product, int quantity, double discount) {
            this.product = product;
            this.quantity = quantity;
            this.discount = discount;
        }
    }

    private static class SaleTransaction {
        private int transactionId;
        private java.util.List<SaleItem> items;
        private double totalAmount;
        private double cashReceived;
        private double change;
        private java.util.Date date; // Use fully qualified name

        public SaleTransaction(int transactionId, java.util.List<SaleItem> items, double totalAmount,
                double cashReceived, double change, java.util.Date date) { // Use fully qualified name
            this.transactionId = transactionId;
            this.items = items;
            this.totalAmount = totalAmount;
            this.cashReceived = cashReceived;
            this.change = change;
            this.date = date;
        }

        public int getTransactionId() {
            return transactionId;
        }

        public java.util.List<SaleItem> getItems() {
            return items;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public double getCashReceived() {
            return cashReceived;
        }

        public double getChange() {
            return change;
        }

        public java.util.Date getDate() { // Use fully qualified name
            return date;
        }
    } // <-- Add this to close SaleTransaction class

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            POS_Admin pos = new POS_Admin();
            pos.setVisible(true);
        });
    }
} // <-- This closes the POS_Admin class