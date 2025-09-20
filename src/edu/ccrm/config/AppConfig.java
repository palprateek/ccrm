package edu.ccrm.config;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Singleton class for application configuration.
 * Manages properties like data file paths.
 * Loads configuration from application.properties file if available,
 * otherwise uses default values.
 */
public final class AppConfig {
    private static final AppConfig INSTANCE = new AppConfig();
    private static final String CONFIG_FILE = "application.properties";
    private final Properties properties;
    private volatile boolean configLoaded = false;

    private AppConfig() {
        properties = new Properties();
    }

    public static AppConfig getInstance() {
        return INSTANCE;
    }

    /**
     * Loads configuration from application.properties file if it exists,
     * otherwise loads default configuration.
     * This method is thread-safe and ensures configuration is loaded only once.
     */
    public synchronized void loadConfiguration() {
        if (configLoaded) {
            return; // Configuration already loaded
        }

        Path configPath = Paths.get(CONFIG_FILE);
        
        if (Files.exists(configPath)) {
            try (FileReader reader = new FileReader(configPath.toFile())) {
                properties.load(reader);
                System.out.println("Configuration loaded from " + CONFIG_FILE);
            } catch (IOException e) {
                System.err.println("Error loading configuration from " + CONFIG_FILE + ": " + e.getMessage());
                System.out.println("Falling back to default configuration.");
                loadDefaultConfiguration();
            }
        } else {
            System.out.println("Configuration file " + CONFIG_FILE + " not found. Using default configuration.");
            loadDefaultConfiguration();
        }
        
        configLoaded = true;
        System.out.println("Configuration loaded successfully.");
    }

    /**
     * Loads default configuration values.
     */
    private void loadDefaultConfiguration() {
        // Default properties
        properties.setProperty("data.directory", "data/");
        properties.setProperty("backup.directory", "backup/");
        properties.setProperty("max.credits.per.semester", "18");
        properties.setProperty("min.credits.per.semester", "12");
        properties.setProperty("enrollment.deadline.hours", "168"); // 1 week
        properties.setProperty("app.name", "Campus Course & Records Manager");
        properties.setProperty("app.version", "1.0.0");
        properties.setProperty("app.environment", "development");
        properties.setProperty("log.level", "INFO");
        properties.setProperty("student.id.min", "1000");
        properties.setProperty("student.id.max", "9999");
        properties.setProperty("course.code.pattern", "^[A-Z]{2,4}[0-9]{3,4}$");
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * Gets an integer property value.
     * @param key the property key
     * @param defaultValue the default value if key not found or invalid
     * @return the integer value
     */
    public int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("Invalid integer value for property " + key + ": " + properties.getProperty(key));
            return defaultValue;
        }
    }

    /**
     * Gets a long property value.
     * @param key the property key
     * @param defaultValue the default value if key not found or invalid
     * @return the long value
     */
    public long getLongProperty(String key, long defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Long.parseLong(value) : defaultValue;
        } catch (NumberFormatException e) {
            System.err.println("Invalid long value for property " + key + ": " + properties.getProperty(key));
            return defaultValue;
        }
    }

    /**
     * Checks if the configuration has been loaded.
     * @return true if configuration is loaded, false otherwise
     */
    public boolean isConfigLoaded() {
        return configLoaded;
    }

    /**
     * Gets the data directory path from configuration.
     * @return the data directory path
     */
    public String getDataDirectory() {
        return getProperty("data.directory", "data/");
    }

    /**
     * Gets the backup directory path from configuration.
     * @return the backup directory path
     */
    public String getBackupDirectory() {
        return getProperty("backup.directory", "backup/");
    }
}