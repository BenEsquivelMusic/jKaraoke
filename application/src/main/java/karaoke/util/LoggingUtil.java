package karaoke.util;

import java.util.logging.Logger;

public final class LoggingUtil {

    private LoggingUtil() {
        /* Utility class */
    }

    /**
     * Creates a logger for the given class.
     *
     * @param clazz the class to create the logger for
     * @return a logger instance for the class
     */
    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }

    /**
     * Sanitizes a string for logging by replacing newlines, carriage returns, and tabs
     * with safe characters to prevent log injection attacks.
     *
     * @param input the input string to sanitize
     * @return sanitized string safe for logging, or empty string if input is null
     */
    public static String sanitizeForLogging(String input) {
        if (input == null) {
            return "";
        }
        return input.replace('\n', '_').replace('\r', '_').replace('\t', ' ');
    }

}
