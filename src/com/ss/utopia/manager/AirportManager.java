package com.ss.utopia.manager;

import com.ss.utopia.dao.AirportDAO;
import com.ss.utopia.dao.RouteDAO;
import com.ss.utopia.entity.Airport;
import com.ss.utopia.Tools;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class AirportManager extends BaseManager {

    AirportDAO dao;
    RouteDAO routes;


    /**
     * Print options specific to Airport Manager
     */
    protected void printOptions() {
        System.out.println("Available Actions:\n" +
            " - 1) Add an Airport\n" +
            " - 2) Update an Airport\n" +
            " - 3) Delete an Airport\n" +
            " - 4) List Airports\n" +
            " - 5) Save and Exit\n");
    }


    /**
     * Print airports in a pretty way
     * @param in list of airports to print
     */
    private void printAirports(List<Airport> in) {
        System.out.println(" ID  | City\n" +
                           "-----|----------");
        for (Airport port : in) {
            System.out.println(" " + port.getAirportCode() + " | " + port.getCityName());
        }
        System.out.println();
    }


    /**
     * Print all airports
     */
    public void printAllAirports() {
        try {
            printAirports(dao.readAirports());
        } catch (Exception e) {
            System.out.println("Could not access database.");
        }
    }


    /**
     * Constructor
     * @param conn reusable connection to the database
     */
    public AirportManager(Connection conn) {
        super(conn);
        dao = new AirportDAO(conn);
        routes = new RouteDAO(conn);
    }


    /**
     * Function to find an airport in the db given its ID
     * @param iata String defining the airport to find
     * @return the airport if it exists, or null if failed
     */
    private Airport getAirport(String iata) {
        try {
            List<Airport> matches = dao.readAirportsById(iata);
            if (matches.size() == 0) {
                System.out.println("Airport by that ID does not exist.");
                return null;
            }
            return matches.get(0);
        } catch (Exception e) {
            System.out.println("Failed to read the database.");
            return null;
        }
    }


    /**
     * Main function for manager - run user input and direct
     */
    @Override
    public Boolean execute() {
        while (true) {
            // print and get options every iteration
            printOptions();
            Integer option = Tools.getOption("Select an option:", 1, 5);

            // use input
            switch (option) {
                case 1:
                    // add airport
                    if (!addObject())
                        return Boolean.FALSE;
                    break;
                case 2:
                    // update an airport
                    if (!updateObject())
                        return Boolean.FALSE;
                    break;
                case 3:
                    // delete an airport
                    if (!removeObject())
                        return Boolean.FALSE;
                    break;
                case 4:
                    // list airports
                    try {
                        printAirports(dao.readAirports());
                    } catch (Exception e) {
                        System.out.println("Error reading airports from database.");
                        return Boolean.FALSE;
                    }
                    break;
                case 5:
                    // exit
                    System.out.println("Leaving Airport Management screen...");
                    return Boolean.TRUE;
                default:
                    // base case
                    System.out.println("Error processing input value.");
            }
        }
    }


    /**
     * Tell the DAO to insert a new Airport
     */
    @Override
    protected Boolean addObject() {
        Scanner in = new Scanner(System.in);
        String id, city;

        // get user input
        System.out.print("Enter an airport id (3 characters):\n - ");
        id = in.nextLine().toUpperCase();
        System.out.print("Enter a city name (String):\n - ");
        city = in.nextLine().toUpperCase();
        if (id.length() != 3) {
            System.out.println("Airport ID is not valid. Exiting...");
            return Boolean.TRUE;
        }

        // construct new airport
        Airport insert = new Airport();
        insert.setAirportCode(id);
        insert.setCityName(city);

        // insert new airport
        try {
            dao.addAirport(insert);
        } catch (SQLException e) {
            System.out.println("SQL Error trying to add the airport.");
            return Boolean.FALSE;
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }

        // end
        System.out.println("Added new airport successfully.");
        return Boolean.TRUE;
    }


    /**
     * Tell the DAO to remove an item
     */
    @Override
    protected Boolean removeObject() {
        Scanner in = new Scanner(System.in);
        Airport match;

        // get airport to remove
        System.out.print("Enter an Airport ID to remove:\n - ");
        String iata = in.nextLine().toUpperCase();
        match = getAirport(iata);
        if (match == null)
            return Boolean.TRUE;

        // perform remove
        try {
            System.out.println("Removing Airport...");
            dao.deleteAirport(match);
        } catch (Exception e) {
            System.out.println("Error removing Airport values from db.");
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully removed airport.");
        return Boolean.TRUE;
    }


    /**
     * Tell the DAO to update an object
     */
    @Override
    protected Boolean updateObject() {
        // vars
        Scanner in = new Scanner(System.in);
        Airport match;

        // get airport to modify
        System.out.print("Enter an Airport ID to modify:\n - ");
        String iata = in.nextLine().toUpperCase();
        match = getAirport(iata);
        if (match == null)
            return Boolean.TRUE;

        // perform modification
        System.out.print("Modifying airport under ID '" + iata + "'.\n" +
            "Enter a new name for the airport city:\n - ");
        match.setCityName(in.nextLine());
        try {
            dao.updateAirport(match);
        } catch (Exception e) {
            System.out.println("Error inserting updated values into db.");
            return Boolean.FALSE;
        }

        //end
        System.out.println("Successfully modified airport.");
        return Boolean.TRUE;
    }
}
