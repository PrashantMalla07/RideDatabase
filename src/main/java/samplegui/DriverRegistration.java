package main.java.samplegui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DriverRegistration extends JFrame {
    private JTextField nameField;
    private JTextField emailField; // Added this field to identify the user
    private JTextField phoneField; // Added phone field
    private JTextField passwordField; // Added password field
    private JTextField licenseField;
    private JTextField photoField;
    private JTextField citizenshipField;
    private JTextField vehicleModelField;
    private JTextField vehicleNumberField;

    public DriverRegistration() {
        setTitle("Driver Registration");
        setSize(400, 350); // Increased size to accommodate new fields
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        nameField = new JTextField(20);
        emailField = new JTextField(20); // Initialize emailField
        phoneField = new JTextField(20); // Initialize phoneField
        passwordField = new JTextField(20); // Initialize passwordField
        licenseField = new JTextField(20);
        photoField = new JTextField(20);
        citizenshipField = new JTextField(20);
        vehicleModelField = new JTextField(20);
        vehicleNumberField = new JTextField(20);

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:")); // Added email field
        panel.add(emailField);
        panel.add(new JLabel("Phone Number:")); // Added phone number field
        panel.add(phoneField);
        panel.add(new JLabel("Password:")); // Added password field
        panel.add(passwordField);
        panel.add(new JLabel("License Number:"));
        panel.add(licenseField);
        panel.add(new JLabel("Citizenship ID:"));
        panel.add(citizenshipField);
        panel.add(new JLabel("Photo URL:"));
        panel.add(photoField);
        panel.add(new JLabel("Vehicle Model:"));
        panel.add(vehicleModelField);
        panel.add(new JLabel("Vehicle Number:"));
        panel.add(vehicleNumberField);

        JButton submitButton = new JButton("Submit");
        panel.add(submitButton);

        add(panel);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerDriver();
            }
        });
    }

    private void registerDriver() {
        String name = nameField.getText();
        String email = emailField.getText(); // Retrieve email
        String phone = phoneField.getText(); // Retrieve phone number
        String password = passwordField.getText(); // Retrieve password
        String license = licenseField.getText();
        String photo = photoField.getText();
        String citizenship = citizenshipField.getText();
        String vehicleModel = vehicleModelField.getText();
        String vehicleNumber = vehicleNumberField.getText();

        String sql = "INSERT INTO Drivers (user_id, name, email, phone_number, password, license_number, citizenship_id, photo_url, vehicle_model, vehicle_number, status) " +
                     "VALUES ((SELECT id FROM Users WHERE email = ?), ?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')";

        try (Connection conn = DatabaseConfig.getConnection(); // Assume DatabaseConfig class exists for DB connection
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email); // Set email to find user_id
            stmt.setString(2, name); // Set name
            stmt.setString(3, email); // Set email
            stmt.setString(4, phone); // Set phone number
            stmt.setString(5, password); // Set password
            stmt.setString(6, license); // Set license number
            stmt.setString(7, citizenship); // Set citizenship ID
            stmt.setString(8, photo); // Set photo URL
            stmt.setString(9, vehicleModel); // Set vehicle model
            stmt.setString(10, vehicleNumber); // Set vehicle number
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Driver Registration Submitted for Admin Approval!");

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
