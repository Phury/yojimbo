package be.phury.boilerplate.config;

/**
 * Configuration errors.
 */
public class ConfigurationException extends RuntimeException {

    public ConfigurationException(Throwable cause, String messageFormat, Object...messageArgs) {
        super(String.format(messageFormat, messageArgs), cause);
    }

    public ConfigurationException(String messageFormat, Object...messageArgs) {
        super(String.format(messageFormat, messageArgs));
    }
}
