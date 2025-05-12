package InventoryManagement.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/inventory_management";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    static {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {
            // Create the dashboard_items table if it does not exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS dashboard_items (" +
                                    "order_id TEXT, " +
                                    "date TEXT, " +
                                    "requested_by TEXT, " +
                                    "sales_channel TEXT, " +
                                    "item TEXT, " +
                                    "items INTEGER, " +
                                    "status TEXT);";
            statement.execute(createTableSQL);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize the database: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
