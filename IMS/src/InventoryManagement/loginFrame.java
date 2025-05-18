package InventoryManagement;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class loginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;
    private int attempts = 0;
    private final int MAX_ATTEMPTS = 3;
    private static final long serialVersionUID = 1L;

    /**
     * Constructor to initialize the login frame with UI components.
     */
    public loginFrame() {
        setTitle("Inventory Management Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel loginForm = new JPanel();
        loginForm.setBackground(Color.WHITE);
        loginForm.setPreferredSize(new Dimension(350, 600));
        loginForm.setLayout(null);

        JLabel lblLogin = new JLabel("Login");
        lblLogin.setFont(new Font("Tahoma", Font.BOLD, 20));
        lblLogin.setBounds(40, 40, 100, 30);
        loginForm.add(lblLogin);

        JLabel lblUser = new JLabel("User*");
        lblUser.setBounds(40, 100, 100, 20);
        loginForm.add(lblUser);

        userField = new JTextField();
        userField.setBounds(40, 130, 250, 30);
        loginForm.add(userField);

        JLabel lblPassword = new JLabel("Password*");
        lblPassword.setBounds(40, 180, 100, 20);
        loginForm.add(lblPassword);

        passwordField = new JPasswordField();
        passwordField.setBounds(40, 210, 250, 30);
        loginForm.add(passwordField);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(40, 270, 250, 35);
        loginForm.add(btnLogin);

        add(loginForm, BorderLayout.WEST);
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.setLayout(new BorderLayout());

        // Simplify error handling for ImageIcon loading
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/InventoryManagement/login_picture.png"));
            if (icon.getImageLoadStatus() != MediaTracker.COMPLETE) {
                throw new RuntimeException("Image resource not found");
            }
            JLabel label = new JLabel(icon);
            rightPanel.add(label, BorderLayout.CENTER);
        } catch (RuntimeException e) {
            e.printStackTrace();
            JLabel placeholder = new JLabel("No Image Available", SwingConstants.CENTER);
            placeholder.setFont(new Font("Arial", Font.BOLD, 16));
            placeholder.setForeground(Color.DARK_GRAY);
            rightPanel.add(placeholder, BorderLayout.CENTER);
        }

        add(rightPanel, BorderLayout.CENTER);


        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = userField.getText();
                String pass = new String(passwordField.getPassword());

                // Ensure the Dashboard is properly displayed after successful login
                if (user.equalsIgnoreCase("Dominic") && pass.equals("1")) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    dispose(); // Close the login frame

                    // Set the logged-in user in the UserSession class
                    UserSession.setLoggedInUser(user);

                    // Pass the username to the Dashboard
                    Dashboard dashboard = new Dashboard(user);
                    dashboard.setVisible(true); // Ensure the Dashboard frame is visible

                } else {
                    attempts++;
                    if (attempts >= MAX_ATTEMPTS) {
                        JOptionPane.showMessageDialog(null, "Login Attempts exceeded. Closing Program.");
                        System.exit(0);
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Invalid credentials. Attempts left: " + (MAX_ATTEMPTS - attempts));
                    }
                }
            }
        });
    }

    /**
     * Validates the user credentials and handles login attempts.
     */
    private void validateLogin() {
        String user = userField.getText();
        String pass = new String(passwordField.getPassword());

        if (user.equals("Dominic") && pass.equals("1")) {
            JOptionPane.showMessageDialog(null, "Login successful!");
            dispose(); // Close the login frame

            Dashboard dashboard = new Dashboard(user);
            dashboard.setVisible(true); // Ensure the Dashboard frame is visible

        } else {
            attempts++;
            if (attempts >= MAX_ATTEMPTS) {
                JOptionPane.showMessageDialog(null, "Login Attempts exceeded. Closing Program.");
                System.exit(0);
            } else {
                showError("Invalid credentials. Attempts left: " + (MAX_ATTEMPTS - attempts));
            }
        }
    }

    /**
     * Displays an error message for invalid login attempts.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    /**
     * Resets the login form fields.
     */
    private void resetForm() {
        userField.setText("");
        passwordField.setText("");
        attempts = 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new loginFrame().setVisible(true);
        });
    }
}