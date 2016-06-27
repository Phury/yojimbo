package be.phury.relax;

/**
 * Converts data.
 */
public interface Serializer {

    <T> String toString(T data);

    <T> T fromString(String str, Class<T> typeOf);
}
