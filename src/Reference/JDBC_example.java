package Reference;

import java.sql.*;
import java.util.Scanner;

public class JDBC_example {
    public static final String driver = "com.mysql.jdbc.Driver";
    public static final String url = "jdbc:mysql://localhost/utopia";
    public static final String username = "root";
    public static final String password = "root";

    /**
     * test suite for SQL connections
     * @param args default input args
     * @throws ClassNotFoundException on driver registration
     */
    public static void main(String[] args) throws ClassNotFoundException {
        // register driver
        Class.forName(driver);

        // create connecion
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            // prepared statement object and user input
            PreparedStatement pstmt = conn.prepareStatement("Select * from route where origin_id = ?");
            System.out.print("Enter origin ID to search:\n - ");
            Scanner scan = new Scanner(System.in);
            String originId = scan.nextLine();
            pstmt.setString(1, originId);

            // execute sql query
            ResultSet result = pstmt.executeQuery();

            // report sql results
            while (result.next()) {
                System.out.println("Route ID: " + result.getInt("id"));
                System.out.println("Route Origin ID: " + result.getString("origin_id"));
                System.out.println("Route Dest ID: " + result.getInt("destination_id"));
                System.out.println("----------------");
            }

        } catch (SQLException e) {
            System.out.println("SQL Exception triggered: connection error");
            e.printStackTrace();
        }
    }
}
