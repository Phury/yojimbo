package be.phury.boilerplate.config;

import be.phury.boilerplate.collections.PropertyLoader;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Handles loading of configuration files and annotated @Config beans
 */
public class ConfigurationLoader {
    public <T> T loadConfiguration(T bean) {
        Class<?> c = bean.getClass();
        ConfigFile configFile = c.getDeclaredAnnotation(ConfigFile.class);
        if (configFile != null) {
            Properties properties = new PropertyLoader().loadFromClasspath(configFile.value());
            for (Field f : c.getDeclaredFields()) {
                Config config = f.getDeclaredAnnotation(Config.class);
                if (config != null) {
                    String value = properties.getProperty(config.value());
                    if (value != null) {
                        boolean accessible = f.isAccessible();
                        Class<?> type = f.getType();
                        try {
                            f.setAccessible(true);
                            if (type.isAssignableFrom(String.class)) {
                                f.set(bean, value);
                            } else if (type.isAssignableFrom(Integer.class) || type.isAssignableFrom(int.class)) {
                                f.set(bean, Integer.parseInt(value));
                            } else if (type.isAssignableFrom(Double.class) || type.isAssignableFrom(double.class)) {
                                f.set(bean, Double.parseDouble(value));
                            } else if (type.isAssignableFrom(Float.class) || type.isAssignableFrom(float.class)) {
                                f.set(bean, Float.parseFloat(value));
                            } else if (type.isAssignableFrom(Boolean.class) || type.isAssignableFrom(boolean.class)) {
                                f.set(bean, Boolean.parseBoolean(value));
                            } else if (type.isAssignableFrom(Date.class)) {
                                f.set(bean, new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SS").parse(value));
                            }
                        } catch (IllegalAccessException e) {
                            throw new ConfigurationException(e, "unable to set property %s of field %s in bean %s", value, f, bean.getClass().getName());
                        } catch (ParseException e) {
                            throw new ConfigurationException(e, "unable to convert property %s of field %s in bean %s to %s", value, f, bean.getClass().getName(), type);
                        } finally {
                            f.setAccessible(accessible);
                        }
                    }
                }
            }
        }
        return bean;
    }
}
