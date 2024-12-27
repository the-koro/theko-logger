package org.theko.logger.test;

import java.io.FileOutputStream;
import java.util.Scanner;

import org.theko.logger.GlobalLogger;
import org.theko.logger.LogLevel;

public class Test1 {
    public static void main(String[] args) {
        Scanner scanner = null;

        try {
            GlobalLogger.setPattern("[{time yyyy:MM:dd HH:mm:ss:SSS}] {level} | [{thread}] | {class}.{method} > {message}\n");
            GlobalLogger.addOutputStream(new FileOutputStream("test1.log"));
            GlobalLogger.log(LogLevel.DEBUG, "Log file added to output streams.");
            GlobalLogger.log(LogLevel.INFO, "Program launched.");

            scanner = new Scanner(System.in);
            GlobalLogger.log(LogLevel.DEBUG, "Scanner object has been created.");

            System.out.print("Enter text to reverse: ");
            String text = scanner.nextLine();
            GlobalLogger.log(LogLevel.INFO, "Text " + text + " received.");
            GlobalLogger.log(LogLevel.DEBUG, "Reversing...");

            GlobalLogger.log(LogLevel.DEBUG, "StringBuilder created.");
            StringBuilder reversedText = new StringBuilder();

            for (int i = text.length() - 1; i >= 0; i--) {
                reversedText.append(text.charAt(i));
            }

            GlobalLogger.log(LogLevel.INFO, "Reversed text: " + reversedText.toString());
            System.out.println("Reversed: " + reversedText.toString());

        } catch (Exception ex) {
            GlobalLogger.log(LogLevel.ERROR, "An exception occured: " + ex.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
                GlobalLogger.log(LogLevel.DEBUG, "Scanner has been closed.");
            }
        }
    }
}
