package InventoryManagement;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame1 extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;
    private int attempts = 0;
    private final int MAX_ATTEMPTS = 3;
    private static final long serialVersionUID = 1L;

    public LoginFrame1() {
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

        //Image
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.LIGHT_GRAY);
        rightPanel.setLayout(new BorderLayout());

        JLabel placeholder = new JLabel("IMAGE PLACEHOLDER", SwingConstants.CENTER);
        placeholder.setFont(new Font("Arial", Font.BOLD, 16));
        rightPanel.add(placeholder, BorderLayout.CENTER);

        add(rightPanel, BorderLayout.CENTER);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String user = userField.getText();
                String pass = new String(passwordField.getPassword());

                // Ensure the DashboardPanel is properly displayed after successful login
                if (user.equals("admin") && pass.equals("password123")) {
                    JOptionPane.showMessageDialog(null, "Login successful!");
                    getContentPane().removeAll();
                    DashboardPanel dashboardPanel = new DashboardPanel();
                    getContentPane().add(dashboardPanel, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                } else {
                    attempts++;
                    if (attempts >= MAX_ATTEMPTS) {
                        JOptionPane.showMessageDialog(null, "Login Attempts exceeded. Closing Program.");
                        System.exit(0);
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid credentials. Attempts left: " + (MAX_ATTEMPTS - attempts));
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame1().setVisible(true);
        });
    }
}