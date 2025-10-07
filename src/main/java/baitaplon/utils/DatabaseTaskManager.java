package baitaplon.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.SwingUtilities;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * Database Task Manager for handling multi-threaded database operations
 * Provides async database operations with proper thread management
 */
public class DatabaseTaskManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseTaskManager.class);
    private static final int THREAD_POOL_SIZE = 5;
    private static ExecutorService executorService;
    private static volatile boolean initialized = false;
    
    /**
     * Initialize the thread pool
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE, new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "DatabaseTask-" + threadNumber.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        });
        
        initialized = true;
        logger.info("Database task manager initialized with {} threads", THREAD_POOL_SIZE);
    }
    
    /**
     * Execute a database task asynchronously
     * @param task The database task to execute
     * @param <T> Return type
     * @return CompletableFuture with the result
     */
    public static <T> CompletableFuture<T> executeAsync(Supplier<T> task) {
        if (!initialized) {
            initialize();
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.get();
            } catch (Exception e) {
                logger.error("Database task failed", e);
                throw new RuntimeException("Database task failed", e);
            }
        }, executorService);
    }
    
    /**
     * Execute a database task asynchronously and update UI on EDT
     * @param task The database task to execute
     * @param uiUpdate The UI update to run on EDT
     * @param <T> Return type
     * @return CompletableFuture with the result
     */
    public static <T> CompletableFuture<T> executeAsyncWithUIUpdate(Supplier<T> task, java.util.function.Consumer<T> uiUpdate) {
        if (!initialized) {
            initialize();
        }
        
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            try {
                return task.get();
            } catch (Exception e) {
                logger.error("Database task failed", e);
                throw new RuntimeException("Database task failed", e);
            }
        }, executorService);
        
        future.thenAcceptAsync(result -> {
            try {
                uiUpdate.accept(result);
            } catch (Exception e) {
                logger.error("UI update failed", e);
            }
        }, SwingUtilities::invokeLater);
        
        return future;
    }
    
    /**
     * Execute multiple database tasks in parallel
     * @param tasks Array of database tasks
     * @return CompletableFuture with all results
     */
    @SafeVarargs
    public static <T> CompletableFuture<T[]> executeAllAsync(Supplier<T>... tasks) {
        if (!initialized) {
            initialize();
        }
        
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] futures = new CompletableFuture[tasks.length];
        
        for (int i = 0; i < tasks.length; i++) {
            final int index = i;
            futures[i] = CompletableFuture.supplyAsync(() -> {
                try {
                    return tasks[index].get();
                } catch (Exception e) {
                    logger.error("Database task {} failed", index, e);
                    throw new RuntimeException("Database task " + index + " failed", e);
                }
            }, executorService);
        }
        
        return CompletableFuture.allOf(futures)
                .thenApply(unused -> {
                    @SuppressWarnings("unchecked")
                    T[] results = (T[]) new Object[futures.length];
                    for (int i = 0; i < futures.length; i++) {
                        results[i] = futures[i].join();
                    }
                    return results;
                });
    }
    
    /**
     * Execute a database task with timeout
     * @param task The database task to execute
     * @param timeoutMs Timeout in milliseconds
     * @param <T> Return type
     * @return CompletableFuture with the result
     */
    public static <T> CompletableFuture<T> executeAsyncWithTimeout(Supplier<T> task, long timeoutMs) {
        if (!initialized) {
            initialize();
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.get();
            } catch (Exception e) {
                logger.error("Database task failed", e);
                throw new RuntimeException("Database task failed", e);
            }
        }, executorService)
        .orTimeout(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
    
    /**
     * Get thread pool statistics
     * @return Thread pool info
     */
    public static String getThreadPoolStats() {
        if (!initialized || executorService == null) {
            return "Thread pool not initialized";
        }
        
        if (executorService instanceof java.util.concurrent.ThreadPoolExecutor) {
            java.util.concurrent.ThreadPoolExecutor tpe = (java.util.concurrent.ThreadPoolExecutor) executorService;
            return String.format("Active: %d, Pool: %d, Queue: %d, Completed: %d",
                               tpe.getActiveCount(),
                               tpe.getPoolSize(),
                               tpe.getQueue().size(),
                               tpe.getCompletedTaskCount());
        }
        
        return "Thread pool active";
    }
    
    /**
     * Shutdown the thread pool
     */
    public static synchronized void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            logger.info("Shutting down database task manager...");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            initialized = false;
            logger.info("Database task manager shutdown complete");
        }
    }
    
    /**
     * Add shutdown hook to properly close the thread pool
     */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application shutdown detected, closing database task manager...");
            shutdown();
        }));
    }
}
