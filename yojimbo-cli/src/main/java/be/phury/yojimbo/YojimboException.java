package be.phury.yojimbo;

/**
 * Root exception for yojimbo errors.
 */
public class YojimboException extends RuntimeException {

    public YojimboException(String messageFormat, Object... messageArgs) {
        super(String.format(messageFormat, messageArgs));
    }
}
