package com.ss.utopia.manager;

import com.ss.utopia.Tools;
import com.ss.utopia.dao.AirplaneDAO;
import com.ss.utopia.dao.FlightDAO;
import com.ss.utopia.entity.Airplane;
import com.ss.utopia.entity.Flight;

import java.sql.Connection;
import java.util.*;

public class AirplaneManager extends BaseManager {

    AirplaneDAO dao;

    public AirplaneManager(Connection conn) {
        super(conn);
        dao = new AirplaneDAO(conn);
    }

    @Override
    protected void printOptions() {
        System.out.println("Available Actions:\n" +
            " - 1) Add a new Airplane\n" +
            " - 2) Update an Airplane's capacity\n" +
            " - 3) Delete an Airplane\n" +
            " - 4) List Airplanes\n" +
            " - 5) Save and Exit\n");
    }

    public void printAirplanes(List<Airplane> in) {
        System.out.println(" ID | TYPE | SEATS \n" +
                           "----|------|-------");
        for (Airplane plane : in) {
            System.out.println(" " + Tools.fitString(plane.getId().toString(), 2) + " | "
                + Tools.fitString(plane.getType().toString(), 4) + " | "
                + plane.getCapacity());
        }
        System.out.println();
    }

    @Override
    public Boolean execute() {
        System.out.println("Entered Airplane manager.");
        while (true) {
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
                        printAirplanes(dao.readAirplanes());
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

    @Override
    protected Boolean addObject() {
        Scanner in = new Scanner(System.in);
        int capacity;

        // get input
        try {
            System.out.print("Enter the max capacity of the airplane:\n - ");
            capacity = Integer.parseInt(in.nextLine());
        } catch (Exception e) {
            System.out.println("Error: you need to enter an integer for capacity.");
            return Boolean.TRUE;
        }

        // find existing capacity
        Boolean create = Boolean.FALSE;
        Integer id;
        try {
            id = dao.getIdByCapacity(capacity);
            if (id == null)
                create = Boolean.TRUE;
        } catch (Exception e) {
            System.out.println("Error reading database.");
            return Boolean.TRUE;
        }

        // new airplane type
        if (create) {
            try {
                dao.addAirplaneWithType(capacity);
                System.out.println("Successfully added airplane and type.");
                return Boolean.TRUE;
            } catch (Exception e) {
                System.out.println("Error inserting airplane and type into database.");
                return Boolean.FALSE;
            }
        }

        // existing airplane type
        try {
            dao.addAirplane(id);
        } catch (Exception e) {
            System.out.println("Error inserting airplane into database.");
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully added airplane.");
        return Boolean.TRUE;
    }

    @Override
    protected Boolean removeObject() {
        Scanner in = new Scanner(System.in);
        int id;

        // get input
        try {
            System.out.print("Enter airplane ID to remove:\n - ");
            id = Integer.parseInt(in.nextLine());
        } catch (Exception e) {
            System.out.println("Error: you need to enter an integer for plane ID.");
            return Boolean.TRUE;
        }

        // remove
        try {
            dao.removeAirplane(id);
        } catch (Exception e) {
            System.out.println("Error removing airplane from database.");
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully removed airplane.");
        return Boolean.TRUE;
    }

    @Override
    protected Boolean updateObject() {
        Scanner in = new Scanner(System.in);
        int choice;

        // get input
        try {
            System.out.print("Enter an airplane ID to edit:\n - ");
            choice = Integer.parseInt(in.nextLine());
        } catch (Exception e) {
            System.out.println("Unable to parse input.");
            return Boolean.TRUE;
        }

        // get choice
        Airplane plane;
        try {
            plane = dao.readAirplanesById(choice);
            if (plane == null) {
                System.out.println("No airplane exists with that ID.");
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println("Unable to read airplanes from database.");
            return Boolean.TRUE;
        }

        // get new capacity
        printAirplanes(new ArrayList<>(Collections.singletonList(plane)));
        System.out.print("\nEnter new capacity for plane:\n - ");
        int capacity;
        try {
            capacity = Integer.parseInt(in.nextLine());
        } catch (Exception e) {
            System.out.println("Unable to parse input.");
            return Boolean.TRUE;
        }

        // check if capacity is valid (existing flights) (clean)
        try {
            FlightDAO fdao = new FlightDAO(conn);
            List<Flight> flights = fdao.getFlightsByAirplane(choice);
            for (Flight flight : flights) {
                if (flight.getReservedSeats() > capacity) {
                    System.out.println("There exist flights on this plane that have more reserved seats than this capacity." +
                        "\n Please remove or edit said flights before lowering the airplane capacity.");
                    return Boolean.TRUE;
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading database.");
            return Boolean.TRUE;
        }

        // find existing capacity
        Boolean create = Boolean.FALSE;
        Integer id;
        try {
            id = dao.getIdByCapacity(capacity);
            if (id == null)
                create = Boolean.TRUE;
        } catch (Exception e) {
            System.out.println("Error reading database.");
            return Boolean.TRUE;
        }

        // add new capacity if needed
        if (create) {
            try {
                id = dao.addType(capacity);
            } catch (Exception e) {
                System.out.println("Error inserting airplane and type into database.");
                return Boolean.FALSE;
            }
        }

        // update
        try {
            dao.updateAirplane(id, plane.getId());
        } catch (Exception e) {
            System.out.println("Error inserting airplane into database.");
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully updated capacity.");
        return Boolean.TRUE;
    }
}
