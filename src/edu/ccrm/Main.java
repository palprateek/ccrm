package edu.ccrm;

import edu.ccrm.cli.CCRM_CLI;
import edu.ccrm.config.AppConfig;

/**
 * Main entry point for the Campus Course & Records Manager (CCRM) application.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting Campus Course & Records Manager...");
        // Initialize the Singleton AppConfig
        AppConfig config = AppConfig.getInstance();
        config.loadConfiguration();

        // Instantiate and run the command-line interface
        CCRM_CLI cli = new CCRM_CLI();
        cli.run();

        System.out.println("Thank you for using CCRM. Goodbye!");
    }
}