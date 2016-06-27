package be.phury.relax;

import com.google.gson.Gson;

/**
 * converts data to and from json
 */
public class JsonSerializer<T> implements Serializer {

    private final Gson GSON = new Gson();

    @Override
    public <T> String toString(T data) {
        return GSON.toJson(data);
    }

    @Override
    public <T> T fromString(String str, Class<T> typeOf) {
        return GSON.fromJson(str, typeOf);
    }
}
