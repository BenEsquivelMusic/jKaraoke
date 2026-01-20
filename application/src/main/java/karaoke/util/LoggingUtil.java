package karaoke.util;

import java.util.logging.Logger;

public final class LoggingUtil {

    private static final String ANSI_ESCAPE_PATTERN = "\u001B\\[[;\\d]*m";

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
     * Sanitizes a string for logging by replacing newlines, carriage returns, tabs,
     * and other control characters with safe characters to prevent log injection attacks.
     * Also removes ANSI escape sequences.
     *
     * @param input the input string to sanitize
     * @return sanitized string safe for logging, or empty string if input is null
     */
    public static String sanitizeForLogging(String input) {
        if (input == null) {
            return "";
        }
        // Remove ANSI escape sequences
        String sanitized = input.replaceAll(ANSI_ESCAPE_PATTERN, "");
        // Replace control characters with safe alternatives
        sanitized = sanitized.replace('\n', '_')
                             .replace('\r', '_')
                             .replace('\t', ' ')
                             .replace('\f', ' ')
                             .replace('\u000B', ' '); // vertical tab
        // Remove any remaining control characters
        return sanitized.replaceAll("\\p{Cntrl}", "");
    }

}
