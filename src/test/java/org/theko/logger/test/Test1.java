package org.theko.logger.test;

import java.io.FileOutputStream;
import java.util.Scanner;

import org.theko.logger.GlobalLogger;

public class Test1 {
    public static void main(String[] args) {
        // Declare scanner object to read user input
        Scanner scanner = null;

        try {
            // Set a custom log pattern (includes timestamp, log level, thread, class, method, and message)
            GlobalLogger.setPattern("[{time yyyy:MM:dd HH:mm:ss:SSS}] {level} | [{thread}] | {class}.{method} > {message}\n");

            // Add a FileOutputStream to write log entries to "test1.log"
            GlobalLogger.addOutputStream(new FileOutputStream("test1.log"));
            
            // Log a debug message to indicate the log file has been added
            GlobalLogger.debug("Log file added to output streams.");
            
            // Log an info message to indicate the program has been launched
            GlobalLogger.info("Program launched.");

            // Initialize the scanner to read user input from the console
            scanner = new Scanner(System.in);
            GlobalLogger.debug("Scanner object has been created.");

            // Prompt user to enter text to reverse
            System.out.print("Enter text to reverse: ");
            String text = scanner.nextLine();  // Read the input text
            GlobalLogger.info("Text " + text + " received.");
            GlobalLogger.debug("Reversing...");

            // Create a StringBuilder to reverse the input text
            GlobalLogger.debug("StringBuilder created.");
            StringBuilder reversedText = new StringBuilder();

            // Loop through the input string in reverse order and append to StringBuilder
            for (int i = text.length() - 1; i >= 0; i--) {
                reversedText.append(text.charAt(i));
            }

            // Log the reversed text
            GlobalLogger.info("Reversed text: " + reversedText.toString());
            // Output the reversed text to the console
            System.out.println("Reversed: " + reversedText.toString());

        } catch (Exception ex) {
            // Log any exceptions that occur
            GlobalLogger.error("An exception occured: " + ex.getMessage());
        } finally {
            // Close the scanner if it was initialized
            if (scanner != null) {
                scanner.close();
                GlobalLogger.debug("Scanner has been closed.");
                GlobalLogger.close();
            }
        }
    }
}
