package be.phury.boilerplate.collections;


import be.phury.boilerplate.lang.Strings;
import be.phury.boilerplate.log.Loggable;
import be.phury.boilerplate.config.ConfigurationException;

import java.io.*;
import java.util.Properties;

/**
 * Handles loading of properties files.
 */
public class PropertyLoader implements Loggable {

    /**
     * Loads a properties file from classpath
     * (Friendly accessibility for unit testing)
     *
     * @param resourceName the name of the resource to load
     * @return a Properties loaded with the {key, value} pairs from the given file
     */
    public Properties loadFromClasspath(String resourceName) {
        return loadFromInputStream(resourceName, new Properties(), Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName));
    }

    /**
     * Loads a properties file from disk
     * (Friendly accessibility for unit testing)
     *
     * @param path the path to the file to load
     * @return a Properties loaded with the {key, value} pairs from the given file
     */
    public Properties loadFromPath(String path) {
        return loadFromPath(path, new Properties());
    }

    /**
     * Loads a properties file from disk
     * (Friendly accessibility for unit testing)
     *
     * @param path       the path to the file to load
     * @param properties the properties to load
     * @return the properties loaded with the {key, value} pairs from the given file. This method returns null if the
     * provided properties is null
     */
    Properties loadFromPath(String path, Properties properties) {
        if (Strings.isNotEmpty(path) && new File(path).exists()) {
            try {
                return loadFromInputStream(path, properties, new FileInputStream(new File(path)));
            } catch (FileNotFoundException e) {
                getLogger().error("cannot find the file specified : {}", path);
                throw new ConfigurationException("unable to load property file [%s]", path);
            }
        }
        return properties;
    }

    /**
     * Loads a properties from the given input stream
     * (Friendly accessibility for unit testing)
     *
     * @param resourceName the name of the resource that is loaded
     * @param properties   the properties to load
     * @param inputStream  the input stream from which to load
     * @return the properties loaded with the {key, value} pairs from the given file. This method returns null if the
     * provided properties is null
     */
    Properties loadFromInputStream(String resourceName, Properties properties, InputStream inputStream) {
        if (properties != null && inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {

                getLogger().error("unable to load property file ", resourceName);
                throw new ConfigurationException("unable to load property file [%s]", resourceName);

            } finally {
                closeSilently(resourceName, inputStream);
            }
        }
        return properties;
    }

    /**
     * Silently closes an input stream
     * (Friendly accessibility for unit testing)
     *
     * @param resourceName the name of the resource being closed
     * @param is           the input stream being closed.
     */
    void closeSilently(String resourceName, InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                getLogger().warn("Unable to close input stream after loading property file [" + resourceName + "]", e);
            }
        }
    }

}