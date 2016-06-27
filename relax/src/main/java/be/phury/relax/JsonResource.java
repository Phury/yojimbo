package be.phury.relax;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

/**
 * REST endpoint to retrieve json objects
 */
public interface JsonResource {
    @PUT
    Object add(String collection, String json);

    @POST
    Object update(String collection, String json);

    @GET
    Object get(String collection, Integer id);

    @GET
    Object list(String collection, Integer offset, Integer limit);

    @DELETE
    Object delete(String collection, Integer id);
}
