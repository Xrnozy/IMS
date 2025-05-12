package InventoryManagement;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.ListSelectionModel;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;

public class OrderManagementSystem extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField searchField;
    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton btnExportToExcel;
    private JButton btnImportOrders;
    private JButton btnNewOrders;
    private JButton btnClearFilter;
    private JComboBox<String> comboBoxSales;
    private JComboBox<String> comboBoxStatus;
    private List<Object[]> originalData = new ArrayList<>();

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    OrderManagementSystem frame = new OrderManagementSystem();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public OrderManagementSystem() {
        setTitle("Order Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblOrders = new JLabel("Order Management");
        lblOrders.setFont(new Font("Arial", Font.BOLD, 18));
        lblOrders.setBounds(15, 15, 200, 25);
        contentPane.add(lblOrders);
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBounds(15, 100, 325, 30);
        searchPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        searchField = new JTextField();
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
        searchField.setToolTipText("Search order ID");
        
        JButton searchButton = new JButton("🔍");
        searchButton.setBorder(null);
        searchButton.setBackground(Color.WHITE);
        searchButton.setFocusPainted(false);
        
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        contentPane.add(searchPanel);
        
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int newSearchWidth = Math.min(400, Math.max(250, width - 600));
                searchPanel.setBounds(15, 100, newSearchWidth, 30);
                searchPanel.revalidate();
            }
        });

        JLabel datePickerIcon = new JLabel("📅");
        datePickerIcon.setHorizontalAlignment(SwingConstants.CENTER);
        datePickerIcon.setBounds(360, 100, 30, 30);
        datePickerIcon.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        contentPane.add(datePickerIcon);
        
        comboBoxSales = new JComboBox<>();
        comboBoxSales.setModel(new DefaultComboBoxModel<>(new String[] {"All Sales", "KangKong Chips"}));
        comboBoxSales.setBounds(400, 100, 150, 30);
        contentPane.add(comboBoxSales);
        
        comboBoxStatus = new JComboBox<>();
        comboBoxStatus.setModel(new DefaultComboBoxModel<>(new String[] {"All Status", "Completed", "Pending"}));
        comboBoxStatus.setBounds(560, 100, 100, 30);
        contentPane.add(comboBoxStatus);
        
        JButton btnFilter = new JButton("Filter");
        btnFilter.setBounds(670, 100, 80, 30);
        btnFilter.setBackground(new Color(110, 0, 220));
        btnFilter.setForeground(Color.WHITE);
        btnFilter.setBorder(null);
        contentPane.add(btnFilter);
        
        btnClearFilter = new JButton("Clear");
        btnClearFilter.setBounds(760, 100, 80, 30);
        btnClearFilter.setBackground(Color.WHITE);
        btnClearFilter.setBorder(BorderFactory.createLineBorder(new Color(110, 0, 220)));
        btnClearFilter.setForeground(new Color(110, 0, 220));
        contentPane.add(btnClearFilter);
        
        btnExportToExcel = new JButton("Export to excel");
        btnExportToExcel.setBounds(462, 38, 130, 30);
        btnExportToExcel.setBackground(Color.WHITE);
        btnExportToExcel.setBorder(BorderFactory.createLineBorder(new Color(110, 0, 220)));
        btnExportToExcel.setForeground(new Color(110, 0, 220));
        contentPane.add(btnExportToExcel);
        
        btnImportOrders = new JButton("Import Orders");
        btnImportOrders.setBounds(600, 38, 140, 30);
        btnImportOrders.setBackground(Color.WHITE);
        btnImportOrders.setBorder(BorderFactory.createLineBorder(new Color(110, 0, 220)));
        btnImportOrders.setForeground(new Color(110, 0, 220));
        contentPane.add(btnImportOrders);
        
        btnNewOrders = new JButton("+ New Orders");
        btnNewOrders.setBounds(748, 38, 122, 30);
        btnNewOrders.setBackground(new Color(110, 0, 220));
        btnNewOrders.setForeground(Color.WHITE);
        btnNewOrders.setBorder(null);
        contentPane.add(btnNewOrders);
        
        btnNewOrders.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddOrderDialog();
            }
        });
        
        String[] columnNames = {"", "order ID", "Date", "Requested by", "Sales channel", "Item", "Items", "Status"};
        tableModel = new DefaultTableModel(null, columnNames) {
            private static final long serialVersionUID = 1L;
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) {
                    return Boolean.class;
                }
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        
        addSampleData();
        
        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(40);
        ordersTable.setShowGrid(false);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.setBackground(Color.WHITE);
        
        sorter = new TableRowSorter<>(tableModel);
        ordersTable.setRowSorter(sorter);
        
        JTableHeader header = ordersTable.getTableHeader();
        header.setBackground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 12));
        
        ordersTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        ordersTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        ordersTable.getColumnModel().getColumn(3).setPreferredWidth(150);
        ordersTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        ordersTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        ordersTable.getColumnModel().getColumn(6).setPreferredWidth(50);
        ordersTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        
        ordersTable.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBounds(15, 145, 855, 400);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        contentPane.add(scrollPane);
        
        btnExportToExcel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(OrderManagementSystem.this, 
                        "Export to Excel functionality would be implemented here.", 
                        "Export", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnImportOrders.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(OrderManagementSystem.this, 
                        "Import Orders functionality would be implemented here.", 
                        "Import", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        btnFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilters();
            }
        });
        
        btnClearFilter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFilters();
            }
        });
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchOrders();
            }
        });
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchOrders();
                }
            }
        });
    }
    	
    private void addSampleData() {
        String today = new SimpleDateFormat("MM/dd/yyyy").format(new Date());

        Object[] row1 = {Boolean.FALSE, "#0001", today, "Josh Mojica", "KangKong Chips", "Cheese", "3", "Completed"};
        Object[] row2 = {Boolean.FALSE, "#0002", today, "Josh Mojica", "KangKong Chips", "Sour and Cream", "3", "Pending"};
        Object[] row3 = {Boolean.FALSE, "#0003", today, "Josh Mojica", "KangKong Chips", "Barbeque", "3", "Completed"};
        Object[] row4 = {Boolean.FALSE, "#0004", today, "Josh Mojica", "KangKong Chips", "Chocolate", "3", "Pending"};
        Object[] row5 = {Boolean.FALSE, "#0005", today, "Josh Mojica", "KangKong Chips", "Spicy Cheese", "3", "Cancelled"};

        originalData.add(row1);
        originalData.add(row2);
        originalData.add(row3);
        originalData.add(row4);
        originalData.add(row5);

        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);
        tableModel.addRow(row5);
    }
    
    private void applyFilters() {
        String salesFilter = comboBoxSales.getSelectedItem().toString();
        String statusFilter = comboBoxStatus.getSelectedItem().toString();
        
        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        
        if (!"All Sales".equals(salesFilter)) {
            RowFilter<Object, Object> salesRowFilter = RowFilter.regexFilter(salesFilter, 4);
            filters.add(salesRowFilter);
        }
        
        if (!"All Status".equals(statusFilter)) {
            RowFilter<Object, Object> statusRowFilter = RowFilter.regexFilter("^" + statusFilter + "$", 7);
            filters.add(statusRowFilter);
        }
        
        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else if (filters.size() == 1) {
            sorter.setRowFilter(filters.get(0));
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
        
        int visibleRows = ordersTable.getRowCount();
        if (visibleRows == 0) {
            JOptionPane.showMessageDialog(this, 
                    "No orders match the selected filters.", 
                    "Filter Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String message = "Showing " + visibleRows + " order" + (visibleRows > 1 ? "s" : "");
            if (!"All Sales".equals(salesFilter)) message += " from " + salesFilter;
            if (!"All Status".equals(statusFilter)) message += " with status: " + statusFilter;
            
            JOptionPane.showMessageDialog(this, message, "Filter Applied", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void clearFilters() {
        comboBoxSales.setSelectedItem("All Sales");
        comboBoxStatus.setSelectedItem("All Status");
        sorter.setRowFilter(null);
        JOptionPane.showMessageDialog(this, "Filters cleared.", "Filters", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void searchOrders() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                    "Please enter an order ID to search", 
                    "Search", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        sorter.setRowFilter(null);
        
        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String orderId = tableModel.getValueAt(i, 1).toString().toLowerCase();
            if (orderId.contains(searchText)) {
                int modelRow = i;
                if (ordersTable.getRowSorter() != null) {
                    modelRow = ordersTable.convertRowIndexToView(i);
                }
                
                if (modelRow >= 0) {
                    ordersTable.setRowSelectionInterval(modelRow, modelRow);
                    ordersTable.scrollRectToVisible(ordersTable.getCellRect(modelRow, 0, true));
                    found = true;
                    break;
                }
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(this, 
                    "No orders found with ID: " + searchText, 
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    
    private void showAddOrderDialog() {
        JDialog addOrderDialog = new JDialog(this, "Add New Order", true);
        addOrderDialog.setSize(450, 400);
        addOrderDialog.setLocationRelativeTo(this);
        addOrderDialog.setLayout(new GridLayout(0, 1, 10, 10));
        addOrderDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField orderIdField = new JTextField(10);
        JTextField requestedByField = new JTextField(20);
        JTextField salesChannelField = new JTextField(20);
        JTextField itemField = new JTextField(20);
        JTextField itemCountField = new JTextField(5);

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Pending", "Completed", "Cancelled"});

        JPanel orderIdPanel = createLabeledField("Order ID:", orderIdField);
        JPanel requestedByPanel = createLabeledField("Requested by:", requestedByField);
        JPanel salesChannelPanel = createLabeledField("Sales channel:", salesChannelField);
        JPanel itemPanel = createLabeledField("Item:", itemField);
        JPanel itemCountPanel = createLabeledField("Items count:", itemCountField);
        JPanel statusPanel = createLabeledField("Status:", statusCombo);

        addOrderDialog.add(orderIdPanel);
        addOrderDialog.add(requestedByPanel);
        addOrderDialog.add(salesChannelPanel);
        addOrderDialog.add(itemPanel);
        addOrderDialog.add(itemCountPanel);
        addOrderDialog.add(statusPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        JButton saveButton = new JButton("Save");

        cancelButton.addActionListener(e -> addOrderDialog.dispose());

        saveButton.addActionListener(e -> {
            if (orderIdField.getText().trim().isEmpty() ||
                requestedByField.getText().trim().isEmpty() ||
                salesChannelField.getText().trim().isEmpty() ||
                itemField.getText().trim().isEmpty() ||
                itemCountField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(addOrderDialog,
                        "All fields are required!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int itemCount;
            try {
                itemCount = Integer.parseInt(itemCountField.getText().trim());
                if (itemCount <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addOrderDialog,
                        "Items count must be a positive number!",
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newOrderId = orderIdField.getText().trim();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String existingId = tableModel.getValueAt(i, 1).toString();
                if (existingId.equalsIgnoreCase(newOrderId)) {
                    JOptionPane.showMessageDialog(addOrderDialog,
                            "Order ID already exists!",
                            "Duplicate Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            String today = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
            Object[] newRow = {
                Boolean.FALSE,
                newOrderId,
                today,
                requestedByField.getText().trim(),
                salesChannelField.getText().trim(),
                itemField.getText().trim(),
                String.valueOf(itemCount),
                statusCombo.getSelectedItem()
            };

            originalData.add(newRow);
            tableModel.addRow(newRow);

            if (sorter != null) {
                sorter.setRowFilter(sorter.getRowFilter());
            }

            addOrderDialog.dispose();

            int newRowIndex = tableModel.getRowCount() - 1;
            ordersTable.setRowSelectionInterval(newRowIndex, newRowIndex);
            ordersTable.scrollRectToVisible(ordersTable.getCellRect(newRowIndex, 0, true));

            JOptionPane.showMessageDialog(OrderManagementSystem.this,
                    "New order added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        addOrderDialog.add(buttonPanel);

        addOrderDialog.setVisible(true);
    }
    
    private JPanel createLabeledField(String labelText, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.add(label);
        panel.add(field);
        return panel;
    }
    
    private JPanel createLabeledField(String labelText, JComboBox<String> combo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(120, 25));
        panel.add(label);
        panel.add(combo);
        return panel;
    }
    
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            if ("Completed".equals(value)) {
                label.setBackground(new Color(78, 188, 137));
                label.setForeground(Color.WHITE);
            } else if ("Pending".equals(value)) {
                label.setBackground(new Color(198, 224, 118));
                label.setForeground(Color.BLACK);
            } else if ("Cancelled".equals(value)) {
                label.setBackground(new Color(255, 99, 71)); // Tomato color for cancelled
                label.setForeground(Color.WHITE);
            } else {
                label.setBackground(Color.WHITE);
                label.setForeground(Color.BLACK);
            }

            label.setOpaque(true);
            return label;
        }
    }

    /**
     * Returns the main panel of the OrderManagementSystem.
     * @return JPanel representing the main content pane.
     */
    public JPanel getMainPanel() {
        return contentPane;
    }
}