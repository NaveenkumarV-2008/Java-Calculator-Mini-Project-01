/*
        Author : Naveen kumar V
        Date : 09 November 2025
        Project No : 01

    A simple calculator using enhanced switch-case (Java 14 feature), while loop, and methods in Java.
*/


import java.util.Scanner;

public class Calculator 
{
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        double num1 ;
        double num2 ;
        String operator ;
        double result = 0;
        boolean validOperation = true ;

        num1 = getValidDouble(scanner, "Enter the value of num1 : ");

        System.out.print("Enter the operation you want to do (+, -, *, /, ^, %) : ");
        operator = scanner.nextLine();
        operator = operator.trim();

        num2 = getValidDouble(scanner, "Enter the value of num2 : ");

        switch (operator)
        {
            case "+" -> result = num1 + num2;
            case "-" -> result = num1 - num2;
            case "*" -> result = num1 * num2;
            case "/" -> 
            {
                if (num2 == 0)
                {
                    System.out.print("Can not divide by 0. ");
                    validOperation = false ;
                }
                else
                {
                    result = num1 / num2;
                }
            }
            case "%" ->
            {
            if ( num2 == 0)
                {
                    System.out.print("Can not divide by 0. ");
                    validOperation = false ;
                }
                else
                {
                    result = num1 % num2;
                } 
            }
            case "^" -> 
            {
                if (num1 < 0 && num2 == 0.5) 
                {
                    System.out.printf("%.2fi%n", Math.sqrt(-num1));
                    validOperation = false; 
                }
                else if (num1 < 0 && num2 % 1 != 0)
                {
                    System.out.print("Invalid input. -ve base with non-integer exponent gives Complex number");
                    validOperation = false ;
                }
                else 
                {
                    result = Math.pow(num1, num2);
                }
            }               
            default -> 
            {
                System.out.print("Invalid operator");
                validOperation = false;
            }
        }
        if (validOperation)
        {
            System.out.printf("%.2f \n",result);
        }
        scanner.close();
    }
    static double getValidDouble(Scanner scanner, String msg)
    {
        while (true) 
        {
            System.out.print(msg);
            String data = scanner.nextLine();

            if (data.isEmpty())
            {
                System.out.print("Input can't be empty, Please enter a number. ");
                continue;
            }
            try 
            {
                return Double.parseDouble(data);
            }
            catch (NumberFormatException e)
            {
                System.out.println("Invalid number, Please Try again.");
            }
        }
    }
}
