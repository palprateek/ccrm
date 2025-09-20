package edu.ccrm.test;

import edu.ccrm.config.AppConfig;

/**
 * Simple test to verify AppConfig loads configuration properly on startup.
 */
public class ConfigTest {
    public static void main(String[] args) {
        System.out.println("=== CCRM Configuration Test ===");
        
        // Initialize AppConfig
        AppConfig config = AppConfig.getInstance();
        System.out.println("AppConfig instance created.");
        
        // Load configuration
        config.loadConfiguration();
        
        // Test configuration values
        System.out.println("\n=== Configuration Values ===");
        System.out.println("Data Directory: " + config.getDataDirectory());
        System.out.println("Backup Directory: " + config.getBackupDirectory());
        System.out.println("Max Credits: " + config.getIntProperty("max.credits.per.semester", 0));
        System.out.println("Min Credits: " + config.getIntProperty("min.credits.per.semester", 0));
        System.out.println("Enrollment Deadline Hours: " + config.getLongProperty("enrollment.deadline.hours", 0L));
        System.out.println("App Name: " + config.getProperty("app.name", "Unknown"));
        System.out.println("App Version: " + config.getProperty("app.version", "Unknown"));
        
        // Test configuration loaded flag
        System.out.println("\n=== Configuration Status ===");
        System.out.println("Configuration Loaded: " + config.isConfigLoaded());
        
        // Test multiple calls to loadConfiguration (should only load once)
        System.out.println("\n=== Testing Multiple Load Calls ===");
        config.loadConfiguration();
        config.loadConfiguration();
        
        System.out.println("\n=== Test Complete ===");
    }
}