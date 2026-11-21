import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StudentManagementGUI {

    public static void main(String[] args) {
    	
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Student Record Management System");
            frame.setSize(600, 450);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(5, 1, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

            JLabel title = new JLabel(
                "STUDENT RECORD MANAGEMENT",
                SwingConstants.CENTER
            );
            title.setFont(new Font("Arial", Font.BOLD, 22));

            JButton addButton = new JButton("Add Student");
            addButton.addActionListener(e -> {
                JTextField nameField = new JTextField();
                JTextField branchField = new JTextField();
                JTextField yearField = new JTextField();
                JTextField emailField = new JTextField();

                Object[] fields = {
                    "Student Name:", nameField,
                    "Branch:", branchField,
                    "Year:", yearField,
                    "Email:", emailField
                };

                int result = JOptionPane.showConfirmDialog(
                    frame, fields, "Add Student",
                    JOptionPane.OK_CANCEL_OPTION
                );

                if (result == JOptionPane.OK_OPTION) {
                    String name = nameField.getText().trim();
                    String branch = branchField.getText().trim();
                    String yearText = yearField.getText().trim();
                    String email = emailField.getText().trim();

                    if (name.isEmpty() || branch.isEmpty()
                            || yearText.isEmpty() || email.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            frame, "Please fill in all fields."
                        );
                        return;
                    }

                    try {
                        int year = Integer.parseInt(yearText);

                        String sql = "INSERT INTO students "
                                + "(name, branch, year, email) "
                                + "VALUES (?, ?, ?, ?)";

                        try (java.sql.Connection con = getConnection();
                             java.sql.PreparedStatement ps =
                                     con.prepareStatement(sql)) {

                            ps.setString(1, name);
                            ps.setString(2, branch);
                            ps.setInt(3, year);
                            ps.setString(4, email);

                            ps.executeUpdate();

                            JOptionPane.showMessageDialog(
                                frame, "Student added successfully!"
                            );
                        }

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(
                            frame, "Year must be a number."
                        );
                    } catch (java.sql.SQLException ex) {
                        JOptionPane.showMessageDialog(
                            frame, "Database error: " + ex.getMessage()
                        );
                    }
                }
            });
            JButton viewButton = new JButton("View All Students");
            viewButton.addActionListener(e -> {
                try (java.sql.Connection con = getConnection();
                     java.sql.Statement st = con.createStatement();
                     java.sql.ResultSet rs =
                             st.executeQuery("SELECT * FROM students")) {

                    javax.swing.JTextArea area =
                            new javax.swing.JTextArea(10, 60);

                    area.append("ID\tName\tBranch\tYear\tEmail\n");
                    area.append("------------------------------------------\n");

                    while (rs.next()) {
                        area.append(
                            rs.getInt("student_id") + "\t" +
                            rs.getString("name") + "\t" +
                            rs.getString("branch") + "\t" +
                            rs.getInt("year") + "\t" +
                            rs.getString("email") + "\n"
                        );
                    }

                    area.setEditable(false);

                    JOptionPane.showMessageDialog(
                        frame,
                        new javax.swing.JScrollPane(area),
                        "All Students",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (java.sql.SQLException ex) {
                    JOptionPane.showMessageDialog(
                        frame,
                        "Database error: " + ex.getMessage()
                    );
                }
            });
            JButton updateButton = new JButton("Update Student");
            updateButton.addActionListener(e -> {
                try {
                    String idText = JOptionPane.showInputDialog(
                        frame, "Enter Student ID to update:");

                    if (idText == null || idText.trim().isEmpty()) {
                        return;
                    }

                    int id = Integer.parseInt(idText);

                    try (java.sql.Connection con = getConnection()) {
                        java.sql.PreparedStatement check = con.prepareStatement(
                            "SELECT * FROM students WHERE student_id = ?");
                        check.setInt(1, id);

                        java.sql.ResultSet rs = check.executeQuery();

                        if (!rs.next()) {
                            JOptionPane.showMessageDialog(frame,
                                "Student ID not found!");
                            return;
                        }

                        JTextField nameField =
                            new JTextField(rs.getString("name"));
                        JTextField branchField =
                            new JTextField(rs.getString("branch"));
                        JTextField yearField =
                            new JTextField(String.valueOf(rs.getInt("year")));
                        JTextField emailField =
                            new JTextField(rs.getString("email"));

                        Object[] fields = {
                            "Student Name:", nameField,
                            "Branch:", branchField,
                            "Year:", yearField,
                            "Email:", emailField
                        };

                        int result = JOptionPane.showConfirmDialog(
                            frame, fields, "Update Student",
                            JOptionPane.OK_CANCEL_OPTION);

                        rs.close();
                        check.close();

                        if (result == JOptionPane.OK_OPTION) {
                            try (java.sql.PreparedStatement ps =
                                con.prepareStatement(
                                "UPDATE students SET name=?, branch=?, year=?, email=? WHERE student_id=?")) {

                                ps.setString(1, nameField.getText().trim());
                                ps.setString(2, branchField.getText().trim());
                                ps.setInt(3, Integer.parseInt(
                                    yearField.getText().trim()));
                                ps.setString(4, emailField.getText().trim());
                                ps.setInt(5, id);

                                ps.executeUpdate();

                                JOptionPane.showMessageDialog(frame,
                                    "Student updated successfully!");
                            }
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame,
                        "Enter a valid Student ID and year.");
                } catch (java.sql.SQLException ex) {
                    JOptionPane.showMessageDialog(frame,
                        "Database error: " + ex.getMessage());
                }
            });
            JButton deleteButton = new JButton("Delete Student");
            deleteButton.addActionListener(e -> {
                String idText = JOptionPane.showInputDialog(
                    frame, "Enter Student ID to delete:");

                if (idText == null || idText.trim().isEmpty()) {
                    return;
                }

                try {
                    int id = Integer.parseInt(idText.trim());

                    int confirm = JOptionPane.showConfirmDialog(
                        frame,
                        "Are you sure you want to delete Student ID " + id + "?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                    );

                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }

                    try (java.sql.Connection con = getConnection();
                         java.sql.PreparedStatement ps = con.prepareStatement(
                             "DELETE FROM students WHERE student_id = ?")) {

                        ps.setInt(1, id);

                        int rows = ps.executeUpdate();

                        if (rows > 0) {
                            JOptionPane.showMessageDialog(
                                frame, "Student deleted successfully!");
                        } else {
                            JOptionPane.showMessageDialog(
                                frame, "Student ID not found!");
                        }
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        frame, "Please enter a valid Student ID.");
                } catch (java.sql.SQLException ex) {
                    JOptionPane.showMessageDialog(
                        frame, "Database error: " + ex.getMessage());
                }
            });

            panel.add(title);
            panel.add(addButton);
            panel.add(viewButton);
            panel.add(updateButton);
            panel.add(deleteButton);

            frame.add(panel);
            frame.setVisible(true);
        });
    }
    private static java.sql.Connection getConnection()
            throws java.sql.SQLException {
        return java.sql.DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/student_db",
            "root",
            "Rishika@123"
        );
    }
}