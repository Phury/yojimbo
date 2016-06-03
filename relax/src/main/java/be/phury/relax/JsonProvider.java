package be.phury.relax;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Handles loading and saving of json collections.
 */
public class JsonProvider {

    private static final Gson GSON = new Gson();

    private static final File database = new File("\\tmp\\yojimbo"); // TODO: configurable and injectable

    public List<JsonObject> getList(String collection, Options options) {
        List<JsonObject> entities = loadList(collection);
        if (entities == null) {
            entities = new LinkedList<>();
            if (options == Options.CREATE) {
                saveList(collection, entities);
            }
        }
        return entities;
    }

    public List<JsonObject> getList(String collection) {
        return getList(collection, Options.NONE);
    }

    public JsonObject findById(List<JsonObject> list, Integer id) {
        return list.stream().filter(e -> id.equals(e.get("id").getAsInt())).findFirst().get();
    }

    public void saveList(String collection, List<JsonObject> toSave) {
        try (
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(database, "api/" + collection + ".json")))
        ) {

            GSON.toJson(toSave, writer);

        } catch (FileNotFoundException e) {
            throw new JsonException(e);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    private List<JsonObject> loadList(String collection) {
        File collectionFile = new File(database, "api/" + collection + ".json");
        if (!collectionFile.exists()) {
            return null;
        }

        try (
                InputStreamReader reader = new InputStreamReader(new FileInputStream(collectionFile))
        ) {

            final Type listType = new TypeToken<ArrayList<JsonObject>>() {}.getType();
            return GSON.fromJson(reader, listType);

        } catch (FileNotFoundException e) {
            throw new JsonException(e);
        } catch (IOException e) {
            throw new JsonException(e);
        }
    }

    public enum Options {
        CREATE, NONE;
    }

    public static final class JsonException extends RuntimeException {
        public JsonException(Throwable cause) {
            super(cause);
        }
    }
}
