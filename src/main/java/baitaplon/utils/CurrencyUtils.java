package baitaplon.utils;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for currency formatting and parsing
 */
public class CurrencyUtils {
    
    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");
    private static final NumberFormat CURRENCY_FORMATTER = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);
    
    /**
     * Format integer amount to Vietnamese currency string
     * @param amount Amount to format
     * @return Formatted currency string (e.g., "20,000 VNĐ")
     */
    public static String formatCurrency(int amount) {
        return String.format("%,d VNĐ", amount);
    }
    
    /**
     * Format long amount to Vietnamese currency string
     * @param amount Amount to format
     * @return Formatted currency string (e.g., "20,000 VNĐ")
     */
    public static String formatCurrency(long amount) {
        return String.format("%,d VNĐ", amount);
    }
    
    /**
     * Parse currency string to integer amount
     * @param currencyString Currency string to parse (e.g., "20,000 VNĐ")
     * @return Parsed integer amount
     * @throws NumberFormatException if parsing fails
     */
    public static int parseCurrency(String currencyString) throws NumberFormatException {
        if (currencyString == null || currencyString.trim().isEmpty()) {
            throw new NumberFormatException("Currency string is null or empty");
        }
        
        // Remove all non-numeric characters except minus sign
        String cleanString = currencyString.trim()
            .replace(" VNĐ", "")
            .replace(",", "")
            .replace(".", "")
            .replace(" ", "");
        
        if (cleanString.isEmpty()) {
            throw new NumberFormatException("No numeric value found in: " + currencyString);
        }
        
        return Integer.parseInt(cleanString);
    }
    
    /**
     * Safe parse currency string with default value
     * @param currencyString Currency string to parse
     * @param defaultValue Default value if parsing fails
     * @return Parsed integer amount or default value
     */
    public static int parseCurrencySafe(String currencyString, int defaultValue) {
        try {
            return parseCurrency(currencyString);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Check if string is a valid currency format
     * @param currencyString String to check
     * @return true if valid currency format
     */
    public static boolean isValidCurrency(String currencyString) {
        try {
            parseCurrency(currencyString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
