package com.ss.utopia.manager;

import com.ss.utopia.Tools;
import com.ss.utopia.dao.BookingDAO;
import com.ss.utopia.dao.FlightDAO;
import com.ss.utopia.dao.PassengerDAO;
import com.ss.utopia.entity.Booking;
import com.ss.utopia.entity.Flight;
import com.ss.utopia.entity.Passenger;

import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class BookingManager extends BaseManager {

    BookingDAO dao;
    PassengerDAO pdao;


    /**
     * Constructor for building DAOs
     * @param conn connection to pass to DAOs
     */
    public BookingManager(Connection conn) {
        super(conn);
        dao = new BookingDAO(conn);
        pdao = new PassengerDAO(conn);
    }


    /**
     * print manager-specific options
     */
    @Override
    protected void printOptions() {
        System.out.println("Available actions:\n" +
            " - 1) Add a Booking\n" +
            " - 2) Update a booking\n" +
            " - 3) Delete a booking\n" +
            " - 4) List bookings\n" +
            " - 5) Save and Exit\n");
    }


    /**
     * print all bookings prettily
     * @param in list of bookings to print
     */
    private void printBookings(List<Booking> in) {
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


    /**
     * Main loop for manager
     */
    @Override
    public Boolean execute() {
        while (true) {
            // get options
            printOptions();
            Integer option = Tools.getOption("Select an option:", 1, 5);

            // use input
            switch (option) {
                case 1:
                    // add booking
                    if (!addObject())
                        return Boolean.FALSE;
                    break;
                case 2:
                    // update booking
                    if (!updateObject())
                        return Boolean.FALSE;
                    break;
                case 3:
                    // delete booking
                    if (!removeObject())
                        return Boolean.FALSE;
                    break;
                case 4:
                    // list bookings
                    try {
                        printBookings(dao.readBookings());
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error reading bookings from the database.");
                    }
                    break;
                case 5:
                    // exit
                    System.out.println("Leaving Booking Management screen...");
                    return Boolean.TRUE;
                default:
                    // base case
                    System.out.println("Error processing input value.");
            }
        }
    }


    /**
     * Tell the DAO to add a booking.
     */
    @Override
    protected Boolean addObject() {
        Scanner in = new Scanner(System.in);
        int flightId, userId, active;
        String stripe;

        // get user input
        try {
            System.out.print("Enter a Flight ID for the Booking (Integer):\n - ");
            flightId = Integer.parseInt(in.nextLine());
            System.out.print("Enter a User ID for the Booking (Integer):\n - ");
            userId = Integer.parseInt(in.nextLine());
            System.out.print("Enter a Card Stripe for the Booking Payment (String):\n - ");
            stripe = in.nextLine();
            System.out.print("Enter whether the booking is active (0 or 1):\n - ");
            active = Integer.parseInt(in.nextLine());
            if (active < 0 || active > 1) {
                System.out.println("Input must be a 0 or a 1.");
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println("Error: Invalid input (type match).");
            return Boolean.TRUE;
        }

        // check flight info
        FlightDAO fdao = new FlightDAO(conn);
        List<Flight> flights;
        Flight flight;
        try {
            flights = fdao.readFlightsById(flightId);
            if (flights.size() == 0) {
                System.out.println("Flight not found with that ID.");
                return Boolean.TRUE;
            }
            flight = flights.get(0);
        } catch (Exception e) {
            System.out.println("Unable to read from database.");
            return Boolean.TRUE;
        }
        int max = flight.getMaxSeats() - flight.getReservedSeats();
        if (max <= 0) {
            System.out.println("Airplane selected is full. Please select a different airplane.");
            return Boolean.TRUE;
        }

        // create object
        Booking booking = new Booking();
        booking.setFlightId(flightId);
        booking.setBookingUser(userId);
        booking.setCardStripe(stripe);
        booking.setActive(active == 1);
        booking.setConfirmation(new Random().nextInt());
        Integer pk;

        // try initial insert
        try {
            pk = dao.addBooking(booking);
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("One or more foreign key constraints failed to satisfy.");
            return Boolean.FALSE;
        } catch (Exception e) {
            System.out.println("Error adding the initial booking.");
            return Boolean.FALSE;
        }

        // try passenger insert
        booking.setPassengers(getPassengers(pk, max));
        int count = 1;
        for (Passenger passenger : booking.getPassengers()) {
            try {
                pdao.addPassenger(passenger);
                count++;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error adding passenger " + count + ".");
                return Boolean.FALSE;
            }
        }

        // update flight information
        flight.setReservedSeats(flight.getReservedSeats() + booking.getPassengers().size());
        try {
            fdao.updateFlight(flight);
        } catch (Exception e) {
            System.out.println("Failed to update flight information.");
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully added booking and passengers.");
        return Boolean.TRUE;
    }


    /**
     * Remove a booking
     */
    @Override
    protected Boolean removeObject() {
        Scanner in = new Scanner(System.in);

        // get booking selection
        System.out.print("Enter a Booking ID to remove:\n - ");
        int id;
        try {
            id = Integer.parseInt(in.nextLine());
        } catch (Exception e) {
            System.out.println("Unable to parse input. Please enter an integer.");
            return Boolean.TRUE;
        }

        // check booking exists
        try {
            dao.readBookingById(id);
        } catch (Exception e) {
            System.out.println("Selected ID does not exist.");
            return Boolean.TRUE;
        }

        // remove booking
        try {
            dao.deleteBooking(id);
        } catch (Exception e) {
            System.out.println("Failed to remove booking from database.");
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully removed booking.");
        return Boolean.TRUE;
    }


    /**
     * Update a booking
     */
    @Override
    protected Boolean updateObject() {
        // get edit target
        Scanner in = new Scanner(System.in);
        System.out.print("Enter a booking ID to update:\n - ");
        int target;
        try {
            target = Integer.parseInt(in.nextLine());
        } catch (Exception e) {
            System.out.println("Unable to parse input.");
            return Boolean.TRUE;
        }

        // check booking exists
        Booking booking;
        try {
            booking = dao.readBookingById(target);
            if (booking == null)  {
                System.out.println("Selected ID does not exist.");
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println("Selected ID does not exist.");
            return Boolean.TRUE;
        }

        // list options for updating
        System.out.print("Options for updating:\n" +
            " - 1) Disable and Refund ticket\n" +
            " - 2) Add passengers\n" +
            " - 3) Remove passengers\n" +
            " - 4) Cancel\n\n");
        Integer option = Tools.getOption("Select an option:", 1, 4);
        FlightDAO fdao = new FlightDAO(conn);
        Flight flight;
        int max;

        // interpret option
        switch (option) {
            case 1:
                // disable and refund ticket
                try {
                    dao.overrideRefund(target, false);
                } catch (Exception e) {
                    System.out.println("Unable to write to the database.");
                    return Boolean.FALSE;
                }
                System.out.println("Disabled the booking and refunded the ticket.");
                break;
            case 2:
                // check flight info
                try {
                    flight = fdao.readFlightsById(booking.getFlightId()).get(0);
                } catch (Exception e) {
                    System.out.println("Unable to read from database.");
                    return Boolean.TRUE;
                }
                max = flight.getMaxSeats() - flight.getReservedSeats();
                if (max <= 0) {
                    System.out.println("The selected booking's flight is full. Cannot add more passengers.");
                    return Boolean.TRUE;
                }

                // add passengers
                int count = 1;
                List<Passenger> add = getPassengers(target, max);
                for (Passenger passenger : add) {
                    try {
                        pdao.addPassenger(passenger);
                        count++;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("Error adding passenger " + count + ".");
                        return Boolean.FALSE;
                    }
                }

                // update flight info
                flight.setReservedSeats(flight.getReservedSeats() + add.size());
                try {
                    fdao.updateFlight(flight);
                } catch (Exception e) {
                    System.out.println("Error updating flight in the database.");
                    return Boolean.FALSE;
                }

                // end
                System.out.println("Successfully added new passenger(s).");
                break;
            case 3:
                // remove passengers
                System.out.print("Enter a passenger ID to remove:\n - ");
                int id;
                try {
                    id = Integer.parseInt(in.nextLine());
                } catch (Exception e) {
                    System.out.println("Unable to parse input.");
                    return Boolean.TRUE;
                }
                try {
                    pdao.deletePassenger(id);
                } catch (Exception e) {
                    System.out.println("Unable to remove passenger from database.");
                    return Boolean.FALSE;
                }

                // check flight info
                try {
                    flight = fdao.readFlightsById(booking.getFlightId()).get(0);
                } catch (Exception e) {
                    System.out.println("Unable to read from database.");
                    return Boolean.FALSE;
                }
                flight.setReservedSeats(flight.getReservedSeats() - 1);
                try {
                    fdao.updateFlight(flight);
                } catch (Exception e) {
                    System.out.println("Unable to update flight information.");
                    return Boolean.FALSE;
                }

                // end
                System.out.println("Successfully removed a passenger.");
                break;
            case 4:
                // exit
                break;
        }

        // end
        System.out.println("Leaving booking update screen.");
        return Boolean.TRUE;
    }


    /**
     * Function to get input to create some passengers
     * @param id Booking id to assign to passengers
     * @return the passenger list to append to the booking
     */
    private List<Passenger> getPassengers(Integer id, Integer max) {
        // vars
        Scanner in = new Scanner(System.in);
        List<Passenger> passengers = new ArrayList<>();
        int count = 1;

        // create passengers
        while (count <= max) {

            // check if continuing creation
            if (count > 1) {
                System.out.print("Create another Passenger? ('y' to continue)\n - ");
                if (!in.nextLine().strip().toLowerCase().equals("y")) {
                    System.out.println("Finished creating passengers.");
                    return passengers;
                }
            }

            // start passenger creation
            System.out.println("Creating passenger #" + count + " of max "+max+".");
            String givenName, familyName, gender, address;
            LocalDate dob;

            // get passenger info
            try {
                System.out.print("Enter the Passenger's Given Name (String):\n - ");
                givenName = in.nextLine();
                System.out.print("Enter the Passenger's Family Name (String):\n - ");
                familyName = in.nextLine();
                System.out.print("Enter the Passenger's Date of Birth (Date):\n - ");
                dob = LocalDate.parse(in.nextLine());
                System.out.print("Enter the Passenger's Gender (M, F, or Other):\n - ");
                gender = in.nextLine().strip().toLowerCase();
                if (!(gender.equals("m") || gender.equals("f") || gender.equals("other"))) {
                    System.out.println("Gender entered does not match available options. Please try again.");
                    continue;
                }
                System.out.print("Enter the Passenger's Address (String):\n - ");
                address = in.nextLine();
            } catch (Exception e) {
                System.out.println("Error creating passenger - incorrect value entered.");
                continue;
            }

            // create passenger
            Passenger passenger = new Passenger();
            passenger.setGivenName(givenName);
            passenger.setFamilyName(familyName);
            passenger.setDob(dob);
            passenger.setGender(gender);
            passenger.setAddress(address);
            passenger.setBookingId(id);

            // add passenger
            passengers.add(passenger);
            count++;
        }

        System.out.println("Created the maximum number of passengers. Returning...");
        return passengers;
    }
}
