package be.phury.boilerplate.log;

import org.slf4j.LoggerFactory;

/**
 * Defines object that can get hold of a logger
 */
public interface Loggable {
    default org.slf4j.Logger getLogger() {
        final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
        return logger;
    }
}
