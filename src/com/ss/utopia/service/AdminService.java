package com.ss.utopia.service;

import com.ss.utopia.Tools;
import com.ss.utopia.dao.UserRoleDAO;
import com.ss.utopia.manager.*;

import java.sql.Connection;
import java.sql.SQLException;

public class AdminService extends BaseService{

    // vars
    ConnectionUtil connUtil;

    /**
     * Print admin-specific options menu
     */
    protected void printOptions() {
        System.out.println("Admin options:\n" +
            " - 1) Manage Flights\n" +
            " - 2) Manage Seats\n" +
            " - 3) Manage Tickets/Passengers\n" +
            " - 4) Manage Airports\n" +
            " - 5) Manage Travellers\n" +
            " - 6) Manage Agents\n" +
            " - 7) Override Ticket Trip Cancellation\n" +
            " - 8) Log Out\n");
    }


    /**
     * Constructor
     * @param connUtil reusable SQL connection utility
     */
    public AdminService(ConnectionUtil connUtil) {
        this.connUtil = connUtil;
    }


    /**
     * Main running loop for Admin
     */
    @Override
    public void run() throws SQLException {
        // scan user input
        System.out.println("Logged in as Admin.\n");
        printOptions();
        boolean login = Boolean.TRUE;

        // user input and access loop
        while (login) {
            // get user option selection
            Integer option = Tools.getOption("Enter an Option:", 1, 8);

            // attempt to connect to server
            Connection conn;
            Boolean save = Boolean.FALSE;

            // set up connection
            try {
                conn = connUtil.getConnection();
            } catch (Exception e) {
                System.out.println("Unable to open database connection.");
                return;
            }

            // Interpret user selection
            BaseManager manager;
            switch (option) {
                case 1:
                    // manage flights
                    manager = new FlightManager(conn);
                    save = manager.execute();
                    System.out.println("Saving Flight modifications...");
                    break;
                case 2:
                    // manage seats

                    break;
                case 3:
                    // manage tickets/passengers
                    manager = new BookingManager(conn);
                    save = manager.execute();
                    System.out.println("Saving Booking Modifications...");
                    break;
                case 4:
                    // manage airports
                    manager = new AirportManager(conn);
                    save = manager.execute();
                    System.out.println("Saving Airport modifications...");
                    break;
                case 5:
                    // manage travellers
                    manager = new UserManager(conn, UserRoleDAO.TRAVELLER);
                    save = manager.execute();
                    System.out.println("Saving Traveller modifications...");
                    break;
                case 6:
                    // manage employees
                    manager = new UserManager(conn, UserRoleDAO.AGENT);
                    save = manager.execute();
                    System.out.println("Saving Agent modifications...");
                    break;
                case 7:
                    // override ticket trip cancellation
                    OverrideManager ov = new OverrideManager(conn);
                    save = ov.execute();
                    System.out.println("Saving overrides...");
                    break;
                case 8:
                    // log out
                    System.out.println("Logging out...");
                    login = Boolean.FALSE;
                    conn.close();
                    break;
                default:
                    System.out.println("Error parsing selection. Restarting...");
                    conn.close();
                    continue;
            }

            // commit if saved, otherwise roll back
            if (save) {
                conn.commit();
                conn.close();
            }
            else {
                System.out.println("Received error code during SQL update or insert. Aborting commit.");
                if (conn != null)
                    conn.rollback();
            }
            printOptions();
        }
    }
}
