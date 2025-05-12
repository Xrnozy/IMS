package InventoryManagement;

// Add missing imports for Swing components and layout managers
import javax.swing.*;
import java.awt.*;

// Simplify the loginFrame to a basic login implementation
public class loginFrame extends JFrame {
    private boolean loginSuccessful = false;

    public loginFrame() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the frame

        // Create a panel for the login form
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10)); // Use BorderLayout for simplicity

        // Create a sub-panel for inputs
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        // Create a login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Simple authentication logic
            if ("admin".equals(username) && "password".equals(password)) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loginSuccessful = true;
                dispose(); // Close the login frame
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add components to the main panel
        panel.add(inputPanel, BorderLayout.CENTER);
        panel.add(loginButton, BorderLayout.SOUTH);

        // Add the panel to the frame
        add(panel);
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }
}