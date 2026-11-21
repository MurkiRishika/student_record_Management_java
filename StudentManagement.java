import java.sql.*;
import java.util.Scanner;

public class StudentManagement {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/student_db";
        String user = "root";
        String password = "Rishika@123";

        try (
            Connection con = DriverManager.getConnection(url, user, password);
            Scanner sc = new Scanner(System.in)
        ) {
            while (true) {
                System.out.println("\n=== Student Record Management ===");
                System.out.println("1. Add Student");
                System.out.println("2. View All Students");
                System.out.println("3. Update Student");
                System.out.println("4. Delete Student");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                int choice = Integer.parseInt(sc.nextLine());

                if (choice == 1) {
                    System.out.print("Enter student name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter branch: ");
                    String branch = sc.nextLine();

                    System.out.print("Enter year: ");
                    int year = Integer.parseInt(sc.nextLine());

                    System.out.print("Enter email: ");
                    String email = sc.nextLine();

                    String sql = "INSERT INTO students (name, branch, year, email) VALUES (?, ?, ?, ?)";

                    try (PreparedStatement ps = con.prepareStatement(sql)) {
                        ps.setString(1, name);
                        ps.setString(2, branch);
                        ps.setInt(3, year);
                        ps.setString(4, email);
                        ps.executeUpdate();
                        System.out.println("Student added successfully!");
                    }

                } else if (choice == 2) {
                    String sql = "SELECT * FROM students";

                    try (Statement st = con.createStatement();
                         ResultSet rs = st.executeQuery(sql)) {

                        System.out.println("\nID | Name | Branch | Year | Email");

                        while (rs.next()) {
                            System.out.println(
                                rs.getInt("student_id") + " | " +
                                rs.getString("name") + " | " +
                                rs.getString("branch") + " | " +
                                rs.getInt("year") + " | " +
                                rs.getString("email")
                            );
                        }
                    }

                } else if (choice == 3) {
    System.out.print("Enter student ID to update: ");
    int id = Integer.parseInt(sc.nextLine());

    System.out.print("Enter new name: ");
    String name = sc.nextLine();

    System.out.print("Enter new branch: ");
    String branch = sc.nextLine();

    System.out.print("Enter new year: ");
    int year = Integer.parseInt(sc.nextLine());

    System.out.print("Enter new email: ");
    String email = sc.nextLine();

    String sql = "UPDATE students SET name=?, branch=?, year=?, email=? WHERE student_id=?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, name);
        ps.setString(2, branch);
        ps.setInt(3, year);
        ps.setString(4, email);
        ps.setInt(5, id);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("Student updated successfully!");
        } else {
            System.out.println("Student ID not found.");
        }
    }
} else if (choice == 4) {
    System.out.print("Enter student ID to delete: ");
    int id = Integer.parseInt(sc.nextLine());

    String sql = "DELETE FROM students WHERE student_id = ?";

    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, id);

        int rows = ps.executeUpdate();

        if (rows > 0) {
            System.out.println("Student deleted successfully!");
        } else {
            System.out.println("Student ID not found.");
        }
    }
}else if (choice == 5) {
    System.out.println("Exiting Student Management System");
    break;
} else {
    System.out.println("Invalid choice. Try again.");
}
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}