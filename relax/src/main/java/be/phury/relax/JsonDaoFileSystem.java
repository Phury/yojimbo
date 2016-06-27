package be.phury.relax;

import be.phury.boilerplate.exceptions.SystemException;
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
public class JsonDaoFileSystem implements JsonDao {

    private static final Gson GSON = new Gson();

    private static final File database = new File("\\tmp\\yojimbo"); // TODO: configurable and injectable

    @Override
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

    @Override
    public List<JsonObject> getList(String collection) {
        return getList(collection, Options.NONE);
    }

    @Override
    public JsonObject findById(List<JsonObject> list, Integer id) {
        return list.stream().filter(e -> id.equals(e.get("id").getAsInt())).findFirst().get();
    }

    @Override
    public void saveList(String collection, List<JsonObject> toSave) {
        try (
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(new File(database, "api/" + collection + ".json")))
        ) {

            GSON.toJson(toSave, writer);

        } catch (FileNotFoundException e) {
            throw new SystemException(e);
        } catch (IOException e) {
            throw new SystemException(e);
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
            throw new SystemException(e);
        } catch (IOException e) {
            throw new SystemException(e);
        }
    }
}
