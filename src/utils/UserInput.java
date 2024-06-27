package utils;

import java.util.Scanner;

public class UserInput {
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    public static int getIntInput(Scanner scanner, String prompt, int min, int max) {
        int input;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("Please enter a value between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // Clear invalid input
            }
        }
    }

    public static String getStringInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.next();
    }

    public static String getStringInput(Scanner scanner, String prompt, String regex) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.next();
            if (input.matches(regex)) {
                return input;
            } else {
                System.out.println("Invalid format. Please try again.");
            }
        }
    }

    public static double getDoubleInput(Scanner scanner, String prompt, double min, double max) {
        double input;
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                input = scanner.nextDouble();
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("Please enter a value between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("Invalid input. Please enter a decimal number.");
                scanner.next(); // Clear invalid input
            }
        }
    }
}
