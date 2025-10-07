package baitaplon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for reading configuration from properties file
 * Supports both external config file (outside JAR) and internal config (inside JAR)
 */
public class ConfigReader {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);
    private static final Properties properties = new Properties();
    private static boolean loaded = false;
    private static String configSource = "default";
    
    static {
        loadProperties();
    }
    
    /**
     * Load properties from database.properties file
     * Priority: External file > Internal JAR file > Default values
     */
    private static void loadProperties() {
        // Try to load from external file first (outside JAR)
        if (loadFromExternalFile()) {
            return;
        }
        
        // Try to load from internal JAR file
        if (loadFromJarFile()) {
            return;
        }
        
        // Fall back to default values
        logger.warn("No database.properties file found, using default values");
        setDefaultProperties();
    }
    
    /**
     * Load properties from external file (outside JAR)
     * @return true if loaded successfully, false otherwise
     */
    private static boolean loadFromExternalFile() {
        try {
            // Try multiple possible locations for external config
            String[] possiblePaths = {
                "database.properties",                    // Current directory
                "config/database.properties",             // Config subdirectory
                "../database.properties",                 // Parent directory
                System.getProperty("user.home") + "/.baitaplon/database.properties", // User home
                System.getProperty("java.class.path").split(File.pathSeparator)[0] + "/../database.properties" // JAR parent directory
            };
            
            for (String pathStr : possiblePaths) {
                Path path = Paths.get(pathStr);
                if (Files.exists(path) && Files.isRegularFile(path)) {
                    try (FileInputStream input = new FileInputStream(path.toFile())) {
                        properties.load(input);
                        loaded = true;
                        configSource = "external: " + path.toAbsolutePath();
                        logger.info("Database configuration loaded from external file: {}", path.toAbsolutePath());
                        return true;
                    } catch (IOException e) {
                        logger.warn("Failed to load external config from {}: {}", path, e.getMessage());
                    }
                }
            }
            
            return false;
        } catch (Exception e) {
            logger.warn("Error checking for external config files: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Load properties from internal JAR file
     * @return true if loaded successfully, false otherwise
     */
    private static boolean loadFromJarFile() {
        try (InputStream input = ConfigReader.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            
            if (input == null) {
                return false;
            }
            
            properties.load(input);
            loaded = true;
            configSource = "internal JAR";
            logger.info("Database configuration loaded from internal JAR file");
            return true;
            
        } catch (IOException e) {
            logger.error("Error loading internal database.properties", e);
            return false;
        }
    }
    
    /**
     * Set default properties if file cannot be loaded
     */
    private static void setDefaultProperties() {
        properties.setProperty("db.host", "localhost");
        properties.setProperty("db.port", "3306");
        properties.setProperty("db.name", "baitaplonjava");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("db.use.ssl", "false");
        properties.setProperty("db.server.timezone", "UTC");
        properties.setProperty("db.use.unicode", "true");
        properties.setProperty("db.character.encoding", "UTF-8");
        loaded = true;
        logger.info("Using default database configuration");
    }
    
    /**
     * Get property value by key
     * @param key Property key
     * @return Property value
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Get property value by key with default value
     * @param key Property key
     * @param defaultValue Default value if key not found
     * @return Property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get integer property value
     * @param key Property key
     * @param defaultValue Default value if key not found or invalid
     * @return Integer property value
     */
    public static int getIntProperty(String key, int defaultValue) {
        try {
            String value = properties.getProperty(key);
            return value != null ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for key '{}': {}", key, properties.getProperty(key));
            return defaultValue;
        }
    }
    
    /**
     * Get boolean property value
     * @param key Property key
     * @param defaultValue Default value if key not found
     * @return Boolean property value
     */
    public static boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * Check if properties were loaded successfully
     * @return true if properties loaded, false otherwise
     */
    public static boolean isLoaded() {
        return loaded;
    }
    
    /**
     * Reload properties from file
     */
    public static void reload() {
        properties.clear();
        loaded = false;
        configSource = "default";
        loadProperties();
    }
    
    /**
     * Get the source of current configuration
     * @return Configuration source description
     */
    public static String getConfigSource() {
        return configSource;
    }
    
    /**
     * Check if configuration was loaded from external file
     * @return true if loaded from external file, false otherwise
     */
    public static boolean isExternalConfig() {
        return configSource.startsWith("external:");
    }
    
    /**
     * Get all configuration properties as a map
     * @return Map of all configuration properties
     */
    public static Properties getAllProperties() {
        Properties copy = new Properties();
        copy.putAll(properties);
        return copy;
    }
}
