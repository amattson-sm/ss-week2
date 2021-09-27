package com.ss.utopia;

import java.util.Scanner;

public interface Tools {


    /**
     * function to get user input as an integer within a range
     * @param prompt String prompt before accepting input
     * @param min minimum integer choice for range
     * @param max maximum integer choice for range
     * @return user input selection
     */
    static Integer getOption(String prompt, Integer min, Integer max) {
        Scanner in = new Scanner(System.in);
        System.out.print(prompt + "\n - ");
        Integer option = null;
        while (option == null) {
            String selection = in.nextLine();
            try {
                option = Integer.valueOf(selection);
                if (option < min || option > max) {
                    System.out.print("Input must be between "+min+" and "+max+". Try again: \n - ");
                    option = null;
                }
            } catch (Exception e) {
                System.out.print("Invalid input. Try again:\n - ");
            }
        }
        return option;
    }

    /**
     * function to fit a string within a given length
     * @param input String value to fit
     * @param size max size of string
     * @return a fitted string (either truncated or with added whitespace)
     */
    static String fitString(String input, Integer size) {
        if (input.length() > size)
            return input.substring(0, size);
        return input + " ".repeat(size - input.length());
    }
}
