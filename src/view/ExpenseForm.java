package view;

import controller.ExpenseController;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

public class ExpenseForm extends JFrame {
    private final JTextField nameField = new JTextField(20);
    private final JTextField amountField = new JTextField(10);
    private final JComboBox<String> categoryBox = new JComboBox<>(new String[]{"Food", "Transport", "Bills", "Entertainment", "Other"});
    private final JTextField dateField = new JTextField(10);

    private final JButton addButton = new JButton("Add Expense");
    private final JButton clearButton = new JButton("Clear");
    private final JButton exitButton = new JButton("Exit");
    private final JButton summaryButton = new JButton("View Summary");
    private final JButton eventDemoButton = new JButton("Event Demo");

    private final DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Name", "Amount", "Category", "Date"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    public ExpenseForm() {
        super("SmartExpense - Expense Entry");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        dateField.setText(LocalDate.now().toString());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 6, 6));
        formPanel.add(new JLabel("Expense Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Amount:"));
        formPanel.add(amountField);
        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryBox);
        formPanel.add(new JLabel("Date (YYYY-MM-DD):"));
        formPanel.add(dateField);
        formPanel.add(summaryButton);
        formPanel.add(eventDemoButton);
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.add(addButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(exitButton);
        formPanel.add(buttonsPanel);

        add(formPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        setSize(700, 450);
        setLocationRelativeTo(null);
    }

    public void setController(ExpenseController controller) {
        addButton.addActionListener(controller);
        clearButton.addActionListener(controller);
        exitButton.addActionListener(controller);
        summaryButton.addActionListener(controller);
        eventDemoButton.addActionListener(controller);

        // Key and mouse listeners for Week 2 topics
        nameField.addKeyListener(controller);
        amountField.addKeyListener(controller);
        dateField.addKeyListener(controller);
        table.addMouseListener(controller);
    }

    public String getExpenseNameInput() { return nameField.getText().trim(); }
    public String getAmountInput() { return amountField.getText().trim(); }
    public String getCategoryInput() { return (String) categoryBox.getSelectedItem(); }
    public String getDateInput() { return dateField.getText().trim(); }

    public JButton getAddButton() { return addButton; }
    public JButton getClearButton() { return clearButton; }
    public JButton getExitButton() { return exitButton; }
    public JButton getSummaryButton() { return summaryButton; }
    public JButton getEventDemoButton() { return eventDemoButton; }
    public DefaultTableModel getTableModel() { return tableModel; }

    public void clearInputs() {
        nameField.setText("");
        amountField.setText("");
        categoryBox.setSelectedIndex(0);
        dateField.setText(LocalDate.now().toString());
        nameField.requestFocusInWindow();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ExpenseForm view = new ExpenseForm();
            controller.ExpenseController controller = new controller.ExpenseController(view);
            view.setController(controller);
            view.setVisible(true);
        });
    }
}


