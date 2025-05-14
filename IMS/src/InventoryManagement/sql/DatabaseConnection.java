package InventoryManagement.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3307/inventory_management";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                Statement statement = connection.createStatement()) {
            // Create the orderItems table if it does not exist
            String createOrderItemsTableSQL = "CREATE TABLE IF NOT EXISTS orderItems (" +
                    "order_id INTEGER PRIMARY KEY AUTO_INCREMENT, " +
                    "date TEXT, " +
                    "requested_by TEXT, " +
                    "sales_channel TEXT, " +
                    "item TEXT, " +
                    "items INTEGER, " +
                    "status TEXT);";
            statement.execute(createOrderItemsTableSQL);

            // Create the items table if it does not exist
            String createItemsTableSQL = "CREATE TABLE IF NOT EXISTS items (" +
                    "item_id INT PRIMARY KEY , " +
                    "name TEXT, " +
                    "category TEXT, " +
                    "quantity INTEGER, " +
                    "sales_channel TEXT, " +
                    "price INTEGER);";
            statement.execute(createItemsTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize the database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
