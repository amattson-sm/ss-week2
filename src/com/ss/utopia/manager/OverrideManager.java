package com.ss.utopia.manager;

import com.ss.utopia.dao.BookingDAO;
import com.ss.utopia.entity.Booking;
import com.ss.utopia.entity.Passenger;

import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public class OverrideManager {

    BookingDAO dao;

    public OverrideManager(Connection conn) {
        dao = new BookingDAO(conn);
    }

    public Boolean execute() {
        Scanner in = new Scanner(System.in);
        try {
            printOverrides(dao.readRefundedBookings());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not access the database.");
            return Boolean.FALSE;
        }

        while (true) {
            // get input
            Integer choice;
            System.out.print("\n Enter a booking ID to override, or type 'exit':\n - ");
            String input = in.nextLine().strip().toLowerCase();
            if ("exit".equals(input))
                return Boolean.TRUE;
            try {
                choice = Integer.parseInt(input);
            } catch (Exception e) {
                System.out.println("Unable to read input. Please try again.");
                continue;
            }

            // check input
            try {
                Booking removal = dao.readInactiveBookingById(choice);
                if (removal == null) {
                    System.out.println("No booking found with the given ID. Please try again.");
                    continue;
                }
            } catch (Exception e) {
                System.out.println("Unable to access the database.");
                return Boolean.TRUE;
            }

            // apply input
            try {
                dao.overrideRefund(choice, true);
            } catch (Exception e) {
                System.out.println("Unable to write to the database.");
                return Boolean.FALSE;
            }

            System.out.println("Successfully overrode trip cancellation and cancelled refund.");
        }
    }

    private void printOverrides(List<Booking> in) {
        System.out.println("List of Bookings:\n" +
            "-----------------");
        for (Booking book : in) {
            System.out.println("Booking ID: " + book.getId() + ";   " +
                "Flight ID: " + book.getFlightId() + "\n  PASSENGERS:");
            int count = 1;
            for (Passenger person : book.getPassengers()) {
                System.out.println("  "+count+". Passenger ID: " + person.getId() + ", " +
                    "Name: " + person.getFamilyName() + ", " +
                    person.getGivenName() + ". Gender: " + person.getGender().toUpperCase() +
                    "\n      Birthdate: " + person.getDob() + "; Address: " + person.getAddress());
                count++;
            }
            System.out.println();
        }
    }
}
