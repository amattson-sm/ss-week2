package com.ss.utopia.manager;

import com.ss.utopia.dao.AirplaneDAO;
import com.ss.utopia.dao.FlightDAO;
import com.ss.utopia.dao.RouteDAO;
import com.ss.utopia.entity.Airplane;
import com.ss.utopia.entity.Flight;
import com.ss.utopia.Tools;
import com.ss.utopia.entity.Route;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.*;

public class FlightManager extends BaseManager {

    FlightDAO dao;
    RouteDAO rdao;

    /**
     * Constructor
     * @param conn reusable connection to the database
     */
    public FlightManager(Connection conn) {
        super(conn);
        dao = new FlightDAO(conn);
        rdao = new RouteDAO(conn);
    }


    /**
     * Print options specific to Flight Manager
     */
    protected void printOptions() {
        System.out.print("\nSelect an action:\n" +
            "   1) Add a Flight\n" +
            "   2) Update a Flight\n" +
            "   3) Delete a Flight\n" +
            "   4) Read Flights\n" +
            "   5) Save and Exit\n\n");
    }


    /**
     * Print flights in a pretty way
     * @param in list of flights to print
     */
    private void printFlights(List<Flight> in) {
        System.out.println("  ID  | ROUTE | PLANE | SEATS |  PRICE  | DATE & TIME" +
                         "\n------|-------|-------|-------|---------|--------------");
        for (Flight flight : in) {
            StringBuilder temp = new StringBuilder(" ");
            temp.append(Tools.fitString(flight.getFlightId().toString(), 4)).append(" | ");
            temp.append(Tools.fitString(flight.getRouteId().toString(), 5)).append(" | ");
            temp.append(Tools.fitString(flight.getAirplaneId().toString(), 5)).append(" | ");
            temp.append(Tools.fitString(Integer.toString(flight.getMaxSeats() - flight.getReservedSeats()), 5)).append(" | ");
            temp.append(Tools.fitString(flight.getSeatPrice().toString(), 7)).append(" | ");
            temp.append(flight.getDateTime());
            System.out.println(temp.toString());
        }
        System.out.println();
    }


    /**
     * Function to find a flight in the db given its ID
     * @param id String defining the flight to find
     * @return the flight if it exists
     */
    private Flight getFlight(Integer id) {
        try {
            List<Flight> matches = dao.readFlightsById(id);
            if (matches.size() == 0) {
                System.out.println("Flight by that ID does not exist.");
                return null;
            }
            return matches.get(0);
        } catch (Exception e)  {
            System.out.println("Failed to read the database.");
            return null;
        }
    }


    /**
     * Main function for manager - run user input and direct
     */
    public Boolean execute() {
        while (true) {
            // get selection
            printOptions();
            Integer option = Tools.getOption("Select an option:", 1, 5);

            // execute selection
            switch (option) {
                case 1:
                    // add a flight
                    if (!addObject())
                        return Boolean.FALSE;
                    break;
                case 2:
                    // update a flight
                    if (!updateObject())
                        return Boolean.FALSE;
                    break;
                case 3:
                    // delete a flight
                    if (!removeObject())
                        return Boolean.FALSE;
                    break;
                case 4:
                    // list flights
                    try {
                        printFlights(dao.readFlights());
                    } catch (Exception e) {
                        System.out.println("Error reading flights from database.");
                    }
                    break;
                case 5:
                    // exit
                    System.out.println("Leaving Flight Management screen...");
                    return Boolean.TRUE;
                default:
                    // base case
                    System.out.println("Error processing input value.");
            }
        }
    }


    /**
     * Tell the DAO to add a new specified Flight
     */
    @Override
    protected Boolean addObject() {
        // vars
        Scanner in = new Scanner(System.in);
        int airplane, reservedSeats;
        String origin, destination;
        float seatPrice;
        Timestamp departureTime;
        AirportManager ports = new AirportManager(conn);

        // get input
        try {
            ports.printAllAirports();
            System.out.print("Enter an origin Airport ID (String(3)):\n - ");
            origin = in.nextLine().toUpperCase();
            if (origin.length() != 3) {
                System.out.println("Airport IDs must be of length 3.");
                return Boolean.TRUE;
            }
            System.out.print("Enter a destination Airport ID (String(3)):\n - ");
            destination = in.nextLine().toUpperCase();
            if (destination.length() != 3) {
                System.out.println("Airport IDs must be of length 3.");
                return Boolean.TRUE;
            }
            System.out.print("Enter an Airplane ID (Integer):\n - ");
            airplane = Integer.parseInt(in.nextLine());
            System.out.print("Enter number of reserved seats (Integer):\n - ");
            reservedSeats = Integer.parseInt(in.nextLine());
            System.out.print("Enter price per seat (Float):\n - ");
            seatPrice = Float.parseFloat(in.nextLine());
            System.out.print("Enter date and time of departure (LocalDate):\n - ");
            departureTime = Timestamp.valueOf(in.nextLine());
        } catch (Exception e) {
            System.out.println("Type match failure; value entered does not match the required type.");
            return Boolean.TRUE;
        }

        // check values
        AirplaneDAO adao = new AirplaneDAO(conn);
        Airplane plane = null;
        try {
            plane = adao.readAirplanesById(airplane);
        } catch (Exception e) {
            System.out.println("Error reading from database.");
        }
        if (plane == null) {
            // no plane with ID
            System.out.println("No plane with the ID '" + airplane + "' found.");
            return Boolean.TRUE;
        }
        if (reservedSeats > plane.getCapacity()) {
            // plane has too low capacity.
            System.out.println("Cannot reserve more seats than the plane capacity.");
            return Boolean.TRUE;
        }

        // create new objects
        Flight insert = new Flight();
        Route route;
        Integer id = null;
        try {
            route = rdao.readRouteByAirportCodes(origin, destination);
            if (route != null)
                id = route.getRouteId();
        } catch (Exception e) {
            System.out.println("Error reading from database.");
            return Boolean.TRUE;
        }

        // check route
        if (route == null) {
            // route needs generated
            System.out.println("Generating new route...");
            route = new Route();
            route.setOriginAirport(origin);
            route.setDestinationAirport(destination);
            try {
                rdao.addRoute(route);
                route = rdao.readRouteByAirportCodes(origin, destination);
                id = route.getRouteId();
            } catch (Exception e) {
                System.out.println("Error reading from database.");
                return Boolean.TRUE;
            }
        }

        insert.setRouteId(id);
        insert.setAirplaneId(airplane);
        insert.setDateTime(departureTime); // 2018-09-01 09:01:15
        insert.setSeatPrice(seatPrice);
        insert.setReservedSeats(reservedSeats);

        // insert flight
        try {
            dao.addFlight(insert);
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Foreign keys did not match; did you enter a valid airport and route ID?");
            return Boolean.FALSE;
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        System.out.println("Added new flight successfully.");
        return Boolean.TRUE;
    }


    /**
     * Tell the DAO to remove a given Flight
     */
    @Override
    protected Boolean removeObject() {
        // vars
        Scanner in = new Scanner(System.in);
        int select;

        // get user input
        try {
            printFlights(dao.readFlights());
            System.out.print("Enter a flight to remove:\n - ");
            try {
                select = Integer.parseInt(in.nextLine());
            } catch (ArithmeticException e) {
                System.out.println("Could not parse user input.");
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println("Error reading from database.");
            return Boolean.TRUE;
        }

        // get Flight object
        Flight rem = getFlight(select);
        if (rem == null)
            return Boolean.TRUE;

        // perform remove
        try {
            System.out.println("Removing flight...");
            dao.deleteFlight(rem);
        } catch (Exception e) {
            System.out.println("Error removing values from db.");
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully removed flight.");
        return Boolean.TRUE;
    }


    /**
     * Tell the DAO to update a given Flight
     */
    @Override
    protected Boolean updateObject() {
        // vars
        Scanner in = new Scanner(System.in);
        Flight match;
        int select, reservedSeats;

        // print options
        try {
            printFlights(dao.readFlights());
        } catch (ClassNotFoundException | SQLException  e) {
            System.out.println("Error reading flights from db.");
            return Boolean.TRUE;
        }
        System.out.print("Enter a flight to update:\n - ");

        // get option
        try {
            select = Integer.parseInt(in.nextLine());
        } catch (ArithmeticException e) {
            System.out.println("Could not parse user input.");
            return Boolean.TRUE;
        }
        match = getFlight(select);
        if (match == null)
            return Boolean.TRUE;

        // create new flight
        Flight newFlight = new Flight();
        newFlight.setFlightId(select);
        System.out.println("Modifying flight with ID '" + select + "':");
        printFlights(new ArrayList<>(Collections.singletonList(match)));
        try {
            System.out.print("Enter number of reserved seats (Integer):\n - ");
            reservedSeats = Integer.parseInt(in.nextLine());
            newFlight.setReservedSeats(reservedSeats);
            System.out.print("Enter price of each seat (Float):\n - ");
            newFlight.setSeatPrice(Float.valueOf(in.nextLine()));
            System.out.print("Enter a date and time for the flight (LocalDate):\n - ");
            newFlight.setDateTime(Timestamp.valueOf(in.nextLine()));
        } catch (Exception e) {
            System.out.println("Type match failure. The previous user input is not of the type specified.");
            return Boolean.TRUE;
        }

        // check values
        AirplaneDAO adao = new AirplaneDAO(conn);
        Airplane plane = null;
        Integer planeId = match.getAirplaneId();
        try {
            plane = adao.readAirplanesById(planeId);
        } catch (Exception e) {
            System.out.println("Error reading from database.");
        }
        if (plane == null) {
            // no plane with ID
            System.out.println("No plane with the ID '" + planeId + "' found.");
            return Boolean.TRUE;
        }
        if (reservedSeats > plane.getCapacity()) {
            // plane has too low capacity.
            System.out.println("Cannot reserve more seats than the plane capacity.");
            return Boolean.TRUE;
        }

        // perform modification
        try {
            dao.updateFlight(newFlight);
        } catch (Exception e) {
            System.out.println("Error inserting updated values into the database.");
            e.printStackTrace();
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully modified flight.");
        return Boolean.TRUE;
    }
}
