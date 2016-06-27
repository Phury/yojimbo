package be.phury.relax;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * Provides json data
 */
public interface JsonDao {

    List<JsonObject> getList(String collection, Options options);

    List<JsonObject> getList(String collection);

    JsonObject findById(List<JsonObject> list, Integer id);

    void saveList(String collection, List<JsonObject> toSave);

    enum Options {
        CREATE, NONE;
    }
}
