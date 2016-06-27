package be.phury.boilerplate.exceptions;

/**
 * Non recoverable exceptions thrown by the system.
 * (FileSystem for instance)
 */
public  final class SystemException extends RuntimeException {
    public SystemException(Throwable cause) {
        super(cause);
    }
}