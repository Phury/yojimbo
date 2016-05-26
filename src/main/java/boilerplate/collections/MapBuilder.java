package boilerplate.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Builds an unmodifiable map that can return a default value.
 * @param <T>
 * @param <U>
 */
public class MapBuilder<T, U> {
    private Map<T, U> map = new HashMap<>();
    private U defaultValue;

    public static <T, U> MapBuilder<T, U> newBuilder() {
        return new MapBuilder<>();
    }

    public MapBuilder<T, U> defaultValue(U defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public MapBuilder<T, U> put(T key, U value) {
        map.put(key, value);
        return this;
    }

    public Map<T, U> build() {
        return Collections.unmodifiableMap(new HashMap<T, U>(map) {
            @Override
            public U get(Object key) {
                return containsKey(key) ? super.get(key) : defaultValue;
            }
        });
    }
}
