/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package baitaplon.baitaplon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for the Baitaplon Swing Application
 * @author HI
 */
public class BaitaplonMaven {
    
    private static final Logger logger = LoggerFactory.getLogger(BaitaplonMaven.class);

    public static void main(String[] args) {
        logger.info("Starting Baitaplon Swing Application...");
        
        try {
            // Set system properties for better UI
            System.setProperty("swing.aatext", "true");
            System.setProperty("swing.aatext.fontsize", "12");
            
            // Launch the main application window
            logger.info("Launching main application window...");
            java.awt.EventQueue.invokeLater(() -> {
                try {
                    baitaplon.main_layout mainWindow = new baitaplon.main_layout();
                    mainWindow.setVisible(true);
                    logger.info("Swing application window displayed successfully");
                } catch (Exception e) {
                    logger.error("Error launching Swing window", e);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error starting application", e);
            System.exit(1);
        }
    }
}