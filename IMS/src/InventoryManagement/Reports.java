package InventoryManagement;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
public class Reports extends JFrame {
private JPanel contentPane;
private JTextField searchField;
private JTable ordersTable;
private DefaultTableModel tableModel;
private JComboBox<String> comboBoxSales;
private TableRowSorter<DefaultTableModel> sorter;
private List<String> reportHistory = new ArrayList<>();
private List<Object[]> originalData = new ArrayList<>();
private JButton btnGenerateReport, btnViewPastReports;
public Reports() {
setTitle("Reports");
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
setBounds(100, 100, 900, 600);
contentPane = new JPanel();
contentPane.setBackground(Color.WHITE);
contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
setContentPane(contentPane);
contentPane.setLayout(null);
JLabel lblReports = new JLabel("Reports");
lblReports.setFont(new Font("Arial", Font.BOLD, 18));
lblReports.setBounds(15, 15, 200, 25);
contentPane.add(lblReports);
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
comboBoxSales = new JComboBox<>();
comboBoxSales.setModel(new DefaultComboBoxModel<>(new String[] {"All Sales",
"KangKong Chips"}));
comboBoxSales.setBounds(400, 100, 150, 30);
contentPane.add(comboBoxSales);
JButton btnFilter = new JButton("Filter");
btnFilter.setBounds(670, 100, 80, 30);
btnFilter.setBackground(new Color(110, 0, 220));
btnFilter.setForeground(Color.WHITE);
btnFilter.setBorder(null);
contentPane.add(btnFilter);
JButton btnClearFilter = new JButton("Clear");
btnClearFilter.setBounds(760, 100, 80, 30);
btnClearFilter.setBackground(Color.WHITE);
btnClearFilter.setBorder(BorderFactory.createLineBorder(new Color(110, 0, 220)));
btnClearFilter.setForeground(new Color(110, 0, 220));
contentPane.add(btnClearFilter);
btnGenerateReport = new JButton("Generate Report");
btnGenerateReport.setBounds(400, 50, 150, 30);
btnGenerateReport.setBackground(new Color(110, 0, 220));
btnGenerateReport.setForeground(Color.WHITE);
contentPane.add(btnGenerateReport);
btnViewPastReports = new JButton("View Past Reports");
btnViewPastReports.setBounds(560, 50, 150, 30);
btnViewPastReports.setBackground(new Color(110, 0, 220));
btnViewPastReports.setForeground(Color.WHITE);
contentPane.add(btnViewPastReports);
String[] columnNames = {"Select", "Order ID", "Date", "Requested By", "Sales Channel",
"Item", "Items"};
tableModel = new DefaultTableModel(null, columnNames) {
@Override
public Class<?> getColumnClass(int column) {
if (column == 0) return Boolean.class;
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
JScrollPane scrollPane = new JScrollPane(ordersTable);
scrollPane.setBounds(15, 145, 855, 400);
scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
contentPane.add(scrollPane);
btnGenerateReport.addActionListener(e -> generateReport());
btnViewPastReports.addActionListener(e -> viewPastReports());
btnFilter.addActionListener(e -> applyFilter());
btnClearFilter.addActionListener(e -> clearFilters());
searchButton.addActionListener(e -> searchReports());
}
private void addSampleData() {
String today = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
Object[] row1 = {false, "#0001", today, "Josh Mojica", "KangKong Chips", "Cheese", "3"};
Object[] row2 = {false, "#0002", today, "Josh Mojica", "KangKong Chips", "Sour and Cream", "3"};
Object[] row3 = {false, "#0003", today, "Josh Mojica", "KangKong Chips", "Barbeque", "3"};
Object[] row4 = {false, "#0004", today, "Josh Mojica", "KangKong Chips", "Chocolate", "3"};
Object[] row5 = {false, "#0005", today, "Josh Mojica", "KangKong Chips", "Spicy Cheese",
"3"};
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
private void generateReport() {
StringBuilder report = new StringBuilder();
int rowCount = tableModel.getRowCount();
for (int i = 0; i < rowCount; i++) {
Boolean isSelected = (Boolean) tableModel.getValueAt(i, 0);
if (isSelected != null && isSelected) {
for (int j = 1; j < tableModel.getColumnCount(); j++) {
report.append(tableModel.getValueAt(i, j)).append(" | ");
}
report.append("\n");
}
}
if (report.length() == 0) {
JOptionPane.showMessageDialog(this, "No rows selected for the report.");
return;
}
String generated = "Report generated on " +
new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date()) +
"\n" + report.toString();
reportHistory.add(generated);
JOptionPane.showMessageDialog(this, "Report generated:\n\n" + generated);
}
private void viewPastReports() {
String historyText = String.join("\n\n", reportHistory);
JOptionPane.showMessageDialog(this, historyText.isEmpty() ? "No past reports found." :
historyText, "Past Reports", JOptionPane.INFORMATION_MESSAGE);
}
private void applyFilter() {
String filterText = searchField.getText();
if (!filterText.isEmpty()) {
sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterText));
} else {
sorter.setRowFilter(null);
}
}
private void clearFilters() {
searchField.setText("");
sorter.setRowFilter(null);
}
private void searchReports() {
String searchText = searchField.getText().trim().toLowerCase();
if (!searchText.isEmpty()) {
sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
}
}
public JPanel getContentPanel() {
    JPanel mainContent = new JPanel(new BorderLayout());
    mainContent.setBackground(Color.WHITE);
    mainContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JLabel lblReports = new JLabel("Reports", SwingConstants.LEFT);
    lblReports.setFont(new Font("Arial", Font.BOLD, 18));
    mainContent.add(lblReports, BorderLayout.NORTH);

    String[] columnNames = {"Select", "Order ID", "Date", "Requested By", "Sales Channel", "Item", "Items"};
    DefaultTableModel tableModel = new DefaultTableModel(null, columnNames) {
        @Override
        public Class<?> getColumnClass(int column) {
            if (column == 0) return Boolean.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 0;
        }
    };

    JTable ordersTable = new JTable(tableModel);
    ordersTable.setRowHeight(40);
    ordersTable.setShowGrid(false);
    ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ordersTable.setBackground(Color.WHITE);

    JTableHeader header = ordersTable.getTableHeader();
    header.setBackground(Color.WHITE);
    header.setFont(new Font("Arial", Font.BOLD, 12));

    JScrollPane scrollPane = new JScrollPane(ordersTable);
    scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
    mainContent.add(scrollPane, BorderLayout.CENTER);

    return mainContent;
}
public static void main(String[] args) {
EventQueue.invokeLater(() -> {
try {
Reports frame = new Reports();
frame.setVisible(true);
} catch (Exception e) {
e.printStackTrace();
}
});
}
}