package com.ss.utopia.jdbc;

import com.ss.utopia.Tools;
import com.ss.utopia.dao.UserDAO;
import com.ss.utopia.dao.UserRoleDAO;
import com.ss.utopia.entity.User;
import com.ss.utopia.service.AdminService;
import com.ss.utopia.service.BaseService;
import com.ss.utopia.service.ConnectionUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * @author amattson-sm
 * Utopia evaluation program
 */
public class InitConnection {

    public static final String driver = "com.mysql.cj.jdbc.Driver";

    /**
     * start the user input
     * @param args input args (ignored)
     */
    public static void main(String[] args) {
        // Register driver
        ConnectionUtil connUtil = new ConnectionUtil();
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: Problem with class '" + driver + "'\nExiting...");
            return;
        }

        // Initial Setup
        System.out.println("Successfully loaded drivers.");
        System.out.println("Welcome to the Utopia Airlines Management System.");
        Scanner in = new Scanner(System.in);

        // Enter main loop
        while (true) {

            Connection conn;
            try {
                conn = connUtil.getConnection();
                UserDAO userDAO = new UserDAO(conn);

                // select login type
                System.out.println("Which type of user would you like to login as:\n" +
                    " - 1) Admin\n" +
                    " - 2) Traveller\n" +
                    " - 3) Agent\n" +
                    " - 4) Exit");
                Integer option = Tools.getOption("Enter a user type:", 1, 4);
                if (option == 4) {
                    System.out.println("Exiting...");
                    return;
                }
                System.out.println();

                // get login
                System.out.print("Enter Username:\n - ");
                String username = in.nextLine();
                System.out.print("Enter Password:\n - ");
                String password = in.nextLine();

                // attempt to login
                User activeUser = userDAO.readUserByLogin(username, password);
                if (activeUser == null) {
                    System.out.println("Login does not match any records. Exiting to main screen.");
                    continue;
                }

                // check role validity
                Integer role = activeUser.getRole();
                if (option == 1 && role != UserRoleDAO.ADMIN) {
                    // fail admin login
                    System.out.println("User does not possess Admin login privileges. Exiting to main screen.");
                    continue;
                }
                if (option == 2 && role != UserRoleDAO.TRAVELLER) {
                    // fail traveller login
                    System.out.println("User is not a Customer or Traveller account. Exiting to main screen.");
                    continue;
                }
                if (option == 3 && role != UserRoleDAO.AGENT) {
                    // fail employee login
                    System.out.println("User is not an Agent or Employee account. Exiting to main screen.");
                    continue;
                }

                // user login is valid; continue to user management screen
                BaseService user;
                switch (option) {
                    case 1:
                        // enter admin
                        System.out.println("Logging into Admin service...");
                        user = new AdminService(connUtil);
                        try { user.run(); }
                        catch (SQLException e) {
                            System.out.println("Problem with engaging SQL database.");
                        }
                        System.out.println("Logged out of Admin service.");
                        break;
                    case 2:
                        // enter traveller
                        System.out.println("Traveller not yet implemented. Exiting..");
                        break;
                    case 3:
                        // enter agent
                        System.out.println("Agent not yet implemented. Exiting...");
                        break;
                    default:
                        System.out.println("Error parsing role. Exiting...");
                }
            } catch (Exception e) {
                System.out.println("Error connecting to SQL server:");
                e.printStackTrace();
            }
        }
    }
}
