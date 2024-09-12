package main.java.samplegui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainDashboard extends JFrame {
    
    public MainDashboard() {
        setTitle("Main Dashboard");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JButton userButton = new JButton("Register as User");
        JButton driverButton = new JButton("Register as Driver");
        JButton adminButton = new JButton("Admin Dashboard");
        
        panel.add(userButton);
        panel.add(driverButton);
        panel.add(adminButton);
        
        add(panel);
        
        userButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserRegistration().setVisible(true);
            }
        });
        
        driverButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DriverRegistration().setVisible(true);
            }
        });
        
        adminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminDashboard().setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainDashboard().setVisible(true);
            }
        });
    }
}
