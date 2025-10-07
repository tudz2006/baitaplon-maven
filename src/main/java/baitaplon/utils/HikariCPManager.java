package baitaplon.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * HikariCP Connection Pool Manager
 * Provides high-performance database connection pooling
 */
public class HikariCPManager {
    
    private static final Logger logger = LoggerFactory.getLogger(HikariCPManager.class);
    private static HikariDataSource dataSource;
    private static volatile boolean initialized = false;
    
    /**
     * Initialize HikariCP connection pool
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            HikariConfig config = new HikariConfig();
            
            // Basic connection settings
            String host = ConfigReader.getProperty("db.host", "localhost");
            int port = ConfigReader.getIntProperty("db.port", 3306);
            String name = ConfigReader.getProperty("db.name", "baitaplonjava");
            String user = ConfigReader.getProperty("db.user", "root");
            String password = ConfigReader.getProperty("db.password", "");
            
            // Build JDBC URL
            StringBuilder urlBuilder = new StringBuilder("jdbc:mysql://");
            urlBuilder.append(host).append(":").append(port).append("/").append(name);
            urlBuilder.append("?useSSL=").append(ConfigReader.getBooleanProperty("db.use.ssl", false));
            urlBuilder.append("&serverTimezone=").append(ConfigReader.getProperty("db.server.timezone", "UTC"));
            urlBuilder.append("&useUnicode=").append(ConfigReader.getBooleanProperty("db.use.unicode", true));
            urlBuilder.append("&characterEncoding=").append(ConfigReader.getProperty("db.character.encoding", "UTF-8"));
            
            config.setJdbcUrl(urlBuilder.toString());
            config.setUsername(user);
            config.setPassword(password);
            
            // Connection pool settings
            config.setMaximumPoolSize(ConfigReader.getIntProperty("hikari.maximum.pool.size", 10));
            config.setMinimumIdle(ConfigReader.getIntProperty("hikari.minimum.idle", 2));
            config.setConnectionTimeout(ConfigReader.getIntProperty("hikari.connection.timeout", 30000));
            config.setIdleTimeout(ConfigReader.getIntProperty("hikari.idle.timeout", 600000));
            config.setMaxLifetime(ConfigReader.getIntProperty("hikari.max.lifetime", 1800000));
            config.setLeakDetectionThreshold(ConfigReader.getIntProperty("hikari.leak.detection.threshold", 60000));
            
            // Connection pool name
            config.setPoolName("BaitaplonPool");
            
            // Additional MySQL optimizations
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
            
            dataSource = new HikariDataSource(config);
            initialized = true;
            
            logger.info("HikariCP connection pool initialized successfully");
            logger.info("Pool size: {} (max: {}, min idle: {})", 
                       config.getMaximumPoolSize(), 
                       config.getMaximumPoolSize(), 
                       config.getMinimumIdle());
            
        } catch (Exception e) {
            logger.error("Failed to initialize HikariCP connection pool", e);
            throw new RuntimeException("Database connection pool initialization failed", e);
        }
    }
    
    /**
     * Get a connection from the pool
     * @return Database connection
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initialize();
        }
        return dataSource.getConnection();
    }
    
    /**
     * Get the DataSource
     * @return HikariDataSource
     */
    public static DataSource getDataSource() {
        if (!initialized) {
            initialize();
        }
        return dataSource;
    }
    
    /**
     * Check if connection pool is healthy
     * @return true if healthy, false otherwise
     */
    public static boolean isHealthy() {
        if (!initialized || dataSource == null || dataSource.isClosed()) {
            return false;
        }
        
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            logger.warn("Connection pool health check failed", e);
            return false;
        }
    }
    
    /**
     * Get connection pool statistics
     * @return Pool statistics string
     */
    public static String getPoolStats() {
        if (!initialized || dataSource == null) {
            return "Pool not initialized";
        }
        
        return String.format("Active: %d, Idle: %d, Total: %d, Waiting: %d",
                           dataSource.getHikariPoolMXBean().getActiveConnections(),
                           dataSource.getHikariPoolMXBean().getIdleConnections(),
                           dataSource.getHikariPoolMXBean().getTotalConnections(),
                           dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection());
    }
    
    /**
     * Shutdown the connection pool
     */
    public static synchronized void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Shutting down HikariCP connection pool...");
            dataSource.close();
            initialized = false;
            logger.info("HikariCP connection pool shutdown complete");
        }
    }
    
    /**
     * Add shutdown hook to properly close the pool
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application shutdown detected, closing connection pool...");
            shutdown();
        }));
    }
}
