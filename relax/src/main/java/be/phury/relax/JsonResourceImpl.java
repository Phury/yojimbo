package be.phury.relax;

import com.google.gson.JsonObject;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import java.util.List;
import java.util.NoSuchElementException;

public class JsonResourceImpl implements JsonResource {

    @Inject
    private Serializer serializer;

    @Inject
    private JsonDao jsonDao;

    @Override
    @PUT
    public Object add(String collection, String json) {
        List<JsonObject> list = jsonDao.getList(collection, JsonDao.Options.CREATE);
        JsonObject toAdd = serializer.fromString(json, JsonObject.class);
        toAdd.addProperty("id", list.size()+1);
        list.add(toAdd);
        jsonDao.saveList(collection, list);
        return toAdd;
    }

    @Override
    @POST
    public Object update(String collection, String json) {
        List<JsonObject> list = jsonDao.getList(collection);
        JsonObject toUpdate = serializer.fromString(json, JsonObject.class);
        int index = toUpdate.get("id").getAsInt() - 1;
        if (index < 0 || index >= list.size()) {
            throw new NoSuchElementException("no element with id " + index);
        }
        list.set(index, toUpdate);
        jsonDao.saveList(collection, list);
        return toUpdate;
    }

    @Override
    @GET
    public Object get(String collection, Integer id) {
        return jsonDao.findById(jsonDao.getList(collection), id);
    }

    @Override
    @GET
    public Object list(String collection, Integer offset, Integer limit) {
        return PageableList.createPaging(jsonDao.getList(collection), offset, limit);
    }

    @Override
    @DELETE
    public Object delete(String collection, Integer id) {
        List<JsonObject> list = jsonDao.getList(collection);
        JsonObject deleted = list.get(id);
        list.remove(id);
        jsonDao.saveList(collection, list);
        return deleted;
    }
}
