import java.util.Scanner;

public class SimpleCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        double num1, num2, result;
        char operator;
        String continueCalc;

        do {
            System.out.println("Enter first number:");
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a number:");
                scanner.next(); // Clear invalid input
            }
            num1 = scanner.nextDouble();

            System.out.println("Enter an operator (+, -, *, /):");
            operator = scanner.next().charAt(0);

            System.out.println("Enter second number:");
            while (!scanner.hasNextDouble()) {
                System.out.println("Invalid input. Please enter a number:");
                scanner.next(); // Clear invalid input
            }
            num2 = scanner.nextDouble();

            switch (operator) {
                case '+':
                    result = num1 + num2;
                    System.out.println("Result: " + result);
                    break;
                case '-':
                    result = num1 - num2;
                    System.out.println("Result: " + result);
                    break;
                case '*':
                    result = num1 * num2;
                    System.out.println("Result: " + result);
                    break;
                case '/':
                    if (num2 == 0) {
                        System.out.println("Error: Cannot divide by zero.");
                    } else {
                        result = num1 / num2;
                        System.out.println("Result: " + result);
                    }
                    break;
                default:
                    System.out.println("Invalid operator.");
            }

            System.out.println("Do you want to perform another operation? (yes/no):");
            continueCalc = scanner.next();

        } while (continueCalc.equalsIgnoreCase("yes"));

        System.out.println("Calculator closed.");
        scanner.close();
    }
}

