package main.java.samplegui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class AdminDashboard extends JFrame {
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private JButton userDetailsButton;
    private JButton approvedDriversButton;
    private JButton pendingDriversButton;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        // Initialize table model and JTable
        tableModel = new DefaultTableModel();
        dataTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dataTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Initialize buttons
        JPanel buttonPanel = new JPanel();
        userDetailsButton = new JButton("User Details");
        approvedDriversButton = new JButton("Approved Drivers");
        pendingDriversButton = new JButton("Pending Drivers");
        buttonPanel.add(userDetailsButton);
        buttonPanel.add(approvedDriversButton);
        buttonPanel.add(pendingDriversButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        // Add button listeners
        userDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUserData();
            }
        });

        approvedDriversButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDriverData("APPROVED");
            }
        });

        pendingDriversButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDriverData("PENDING");
            }
        });
    }

    private void loadUserData() {
        String sql = "SELECT id, name, email, phone_number, role FROM Users";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Clear existing data
            tableModel.setColumnIdentifiers(new Object[]{"ID", "Name", "Email", "Phone Number", "Role"});
            tableModel.setRowCount(0);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("role")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load user data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadDriverData(String status) {
        String sql = "SELECT id, name, email, license_number, citizenship_id, photo_url, vehicle_model, vehicle_number, phone_number, status " +
                     "FROM Drivers WHERE status = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            // Clear existing data
            tableModel.setColumnIdentifiers(new Object[]{"ID", "Name", "Email", "License Number", "Citizenship ID", "Photo URL", "Vehicle Model", "Vehicle Number", "Phone Number", "Status", "Accept", "Reject"});
            tableModel.setRowCount(0);

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("license_number"),
                    rs.getString("citizenship_id"),
                    rs.getString("photo_url"),
                    rs.getString("vehicle_model"),
                    rs.getString("vehicle_number"),
                    rs.getString("phone_number"),
                    rs.getString("status"),
                    "Accept",
                    "Reject"
                });
            }

            // Add custom renderer for actions column
            TableColumnModel columnModel = dataTable.getColumnModel();
            TableColumn acceptColumn = columnModel.getColumn(10);
            TableColumn rejectColumn = columnModel.getColumn(11);

            acceptColumn.setCellRenderer(new ButtonRenderer("Accept"));
            acceptColumn.setCellEditor(new ButtonEditor(new JCheckBox(), this, "Accept"));

            rejectColumn.setCellRenderer(new ButtonRenderer("Reject"));
            rejectColumn.setCellEditor(new ButtonEditor(new JCheckBox(), this, "Reject"));

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load driver data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Inner class for rendering buttons in the Actions column
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        private String action;

        public ButtonRenderer(String action) {
            this.action = action;
            setOpaque(true);
            setActionCommand(action);
        }

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value == null) {
                return this;
            }
            setText(value.toString());
            return this;
        }
    }

    // Inner class for editing buttons in the Actions column
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String action;
        private AdminDashboard frame;

        public ButtonEditor(JCheckBox checkBox, AdminDashboard frame, String action) {
            super(checkBox);
            this.frame = frame;
            this.action = action;
            button = new JButton(action);
            button.setOpaque(true);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = dataTable.getSelectedRow();
                    if (row != -1) {
                        int driverId = (int) tableModel.getValueAt(row, 0);
                        if (action.equals("Accept")) {
                            updateDriverStatus(driverId, "APPROVED");
                        } else if (action.equals("Reject")) {
                            deleteDriver(driverId);
                        }
                    }
                }
            });
        }

        @Override
        public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return action;
        }

        private void updateDriverStatus(int driverId, String status) {
            String sql = "UPDATE Drivers SET status = ? WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, status);
                stmt.setInt(2, driverId);
                stmt.executeUpdate();

                // Reload data to reflect changes
                loadDriverData("PENDING");

                JOptionPane.showMessageDialog(frame, "Driver Status Updated to " + status + "!");

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to update driver status: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private void deleteDriver(int driverId) {
            String sql = "DELETE FROM Drivers WHERE id = ?";

            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, driverId);
                stmt.executeUpdate();

                // Reload data to reflect changes
                loadDriverData("PENDING");

                JOptionPane.showMessageDialog(frame, "Driver has been rejected and removed!");

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Failed to delete driver: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
