package be.phury.relax;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.util.List;
import java.util.NoSuchElementException;

public class RestResourceService {

    private static final Gson GSON = new Gson();

    private JsonProvider jsonProvider = new JsonProvider();

    @PUT
    public Object add(String collection, String json) {
        List<JsonObject> list = jsonProvider.getList(collection, JsonProvider.Options.CREATE);
        JsonObject toAdd = GSON.fromJson(json, JsonObject.class);
        toAdd.addProperty("id", list.size()+1);
        list.add(toAdd);
        jsonProvider.saveList(collection, list);
        return toAdd;
    }

    @POST
    public Object update(String collection, String json) {
        List<JsonObject> list = jsonProvider.getList(collection);
        JsonObject toUpdate = GSON.fromJson(json, JsonObject.class);
        int index = toUpdate.get("id").getAsInt() - 1;
        if (index < 0 || index >= list.size()) {
            throw new NoSuchElementException("no element with id " + index);
        }
        list.set(index, toUpdate);
        jsonProvider.saveList(collection, list);
        return toUpdate;
    }

    @GET
    public Object get(String collection, Integer id) {
        return jsonProvider.findById(jsonProvider.getList(collection), id);
    }

    @GET
    public Object list(String collection, Integer offset, Integer limit) {
        return PageableList.createPaging(jsonProvider.getList(collection), offset, limit);
    }

    @DELETE
    public Object delete(String collection, Integer id) {
        List<JsonObject> list = jsonProvider.getList(collection);
        JsonObject deleted = list.get(id);
        list.remove(id);
        jsonProvider.saveList(collection, list);
        return deleted;
    }
}
