package com.ss.utopia.manager;

import com.ss.utopia.Tools;
import com.ss.utopia.dao.UserDAO;
import com.ss.utopia.dao.UserRoleDAO;
import com.ss.utopia.entity.User;

import java.sql.Connection;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Scanner;

public class UserManager extends BaseManager {

    private final Integer userType;
    private final String userName, userNameMod;
    UserDAO dao;

    /**
     * Modified constructor to save the user type being modified
     * @param conn connection to forward
     * @param userType Integer representing either Agent or Traveller
     */
    public UserManager(Connection conn, Integer userType) {
        super(conn);
        this.userType = userType;
        switch (userType) {
            case UserRoleDAO.AGENT:
                userName = "Agent";
                userNameMod = "n Agent";
                break;
            case UserRoleDAO.TRAVELLER:
                userName = "Traveller";
                userNameMod = " Traveller";
                break;
            default:
                System.out.println("Cannot instantiate a user manager with an incorrect user type.");
                throw new IndexOutOfBoundsException();
        }
        dao = new UserDAO(conn);
    }


    /**
     * Print options specific to User Manager
     */
    @Override
    protected void printOptions() {
        System.out.print("\nAvailable Actions:\n" +
            "   1) Add a"+userNameMod+"\n" +
            "   2) Update a"+userNameMod+"\n" +
            "   3) Delete a"+userNameMod+"\n" +
            "   4) List "+userName+"s\n" +
            "   5) Save and Exit\n\n");
    }

    /**
     * Print Users in a pretty way
     */
    public void printUsers(List<User> in) {
        System.out.println("List of " + userName + "s:");
        System.out.println(" UID | Full Name\n" +
                           "-----|-----------");
        for (User user : in) {
            String temp = " " + Tools.fitString(user.getId().toString(), 3) + " | " +
                user.getLastName() + ", " + user.getFirstName();
            System.out.println(temp);
        }
        System.out.println();
    }


    /**
     * Main function for manager - run user input and direct
     */
    @Override
    public Boolean execute() {
        while (true) {
            // get selection
            printOptions();
            Integer option = Tools.getOption("Select an option:", 1, 5);

            // execute selection
            switch (option) {
                case 1:
                    // add a user
                    if (!addObject())
                        return Boolean.FALSE;
                    break;
                case 2:
                    // update a user
                    if (!updateObject())
                        return Boolean.FALSE;
                    break;
                case 3:
                    // delete a user
                    if (!removeObject())
                        return Boolean.FALSE;
                    break;
                case 4:
                    // list users
                    try {
                        printUsers(dao.readUserByRole(userType));
                    } catch (Exception e) {
                        System.out.println("Error reading "+userName+"s from database.");
                    }
                    break;
                case 5:
                    // exit
                    System.out.println("Leaving "+userName+" Management screen...");
                    return Boolean.TRUE;
                default:
                    // base case
                    System.out.println("Error processing input value.");
            }
        }
    }


    /**
     * Tell the DAO to add a new user
     */
    @Override
    protected Boolean addObject() {
        // create user object
        User insert = newUser("Creating new");
        if (insert == null) {
            System.out.println("Unable to assign values to object.");
            return Boolean.TRUE;
        }

        // insert user
        try {
            dao.addUser(insert);
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Could not add a user that doesn't have a valid role.");
            return Boolean.FALSE;
        } catch (Exception e) {
            e.printStackTrace();
            return Boolean.FALSE;
        }
        System.out.println("Added new " + userName + " successfully.");
        return Boolean.TRUE;
    }


    /**
     * Tell the DAO to remove a given user
     */
    @Override
    protected Boolean removeObject() {
        // vars
        Scanner in = new Scanner(System.in);
        int select;

        // get user input
        try {
            printUsers(dao.readUserByRole(userType));
            System.out.print("Enter a " + userName + " to remove:\n - ");
            try {
                select = Integer.parseInt(in.nextLine());
            } catch (ArithmeticException e) {
                System.out.println("Could not parse user input.");
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println("Error reading users from database.");
            return Boolean.TRUE;
        }

        // get specific user
        User rem;
        try {
            rem = dao.readUserByRoleAndId(select, userType);
            if (rem == null) {
                System.out.println("Input does not match any "+userName+"s.");
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println("Unable to read from database.");
            return Boolean.TRUE;
        }

        // perform remove
        try {
            System.out.println("Removing "+userName+"...");
            dao.deleteUser(rem);
        } catch (Exception e) {
            System.out.println("Unable to write to database.");
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully deleted "+userName+".");
        return Boolean.TRUE;
    }


    /**
     * Tell the DAO to update a given user
     */
    @Override
    protected Boolean updateObject() {
        // vars
        Scanner in = new Scanner(System.in);
        int select;

        // get user input
        try {
            printUsers(dao.readUserByRole(userType));
            System.out.print("Enter a " + userName + " to update:\n - ");
            try {
                select = Integer.parseInt(in.nextLine());
                try {
                    User test = dao.readUserByRoleAndId(select, userType);
                    if (test == null) {
                        System.out.println("User does not exist as a" + userNameMod +".");
                        return Boolean.TRUE;
                    }
                } catch (Exception e) {
                    System.out.println("Could not read new user.");
                    return Boolean.TRUE;
                }
            } catch (ArithmeticException e) {
                System.out.println("Could not parse user input.");
                return Boolean.TRUE;
            }
        } catch (Exception e) {
            System.out.println("Error reading users from database.");
            return Boolean.TRUE;
        }

        // create new user
        User user = newUser("Updating");
        System.out.print("Enter the "+userName+"'s intended role ID (Integer) (currently: "+userType+"):\n - ");
        int role;
        if (user == null) {
            System.out.println("Error creating new user: unable to assign types.");
            return Boolean.TRUE;
        } try {
            role = Integer.parseInt(in.nextLine());
            user.setRole(role);
            user.setId(select);
        } catch (Exception e) {
            System.out.println("The role entered is not a valid role.");
        }

        // perform update
        try {
            dao.updateUser(user);
        } catch (Exception e) {
            System.out.println("Unable to update user in database.");
            return Boolean.FALSE;
        }

        // end
        System.out.println("Successfully updated " + userName + ".");
        return Boolean.TRUE;
    }

    private User newUser(String mode) {
        // get input
        Scanner in = new Scanner(System.in);
        System.out.println(mode + " " + userName + "...");
        String firstName, lastName, username, password, email, phone;
        try {
            System.out.print("Enter the "+userName+"'s Given Name (String):\n - ");
            firstName = in.nextLine();
            System.out.print("Enter the "+userName+"'s Family Name (String):\n - ");
            lastName = in.nextLine();
            System.out.print("Create the "+userName+"'s username (String):\n - ");
            username = in.nextLine();
            System.out.print("Create the "+userName+"'s default password (String):\n - ");
            password = in.nextLine();
            System.out.print("Enter the "+userName+"'s Email address (String):\n - ");
            email = in.nextLine();
            System.out.print("Enter the "+userName+"'s Phone # (String):\n - ");
            phone = in.nextLine();
        } catch (Exception e) {
            System.out.println("Error parsing input; values failed to match.");
            return null;
        }

        // create user object
        User insert = new User();
        insert.setRole(userType);
        insert.setFirstName(firstName);
        insert.setLastName(lastName);
        insert.setUsername(username);
        insert.setPassword(password);
        insert.setEmail(email);
        insert.setPhone(phone);

        return insert;
    }
}
