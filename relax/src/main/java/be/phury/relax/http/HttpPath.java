package be.phury.relax.http;

import be.phury.boilerplate.lang.Strings;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Http path
 */
public class HttpPath {

    private final String root;
    private final String[] pathParams;
    private final Map<String, String> queryParams;

    public HttpPath(String root, String[] pathParams, Map<String, String> queryParams) {
        this.root = root;
        this.pathParams = pathParams;
        this.queryParams = queryParams;
    }

    public static HttpPath parse(String uri) {
        String path;
        String queryParam;
        if (uri.contains("?")) {
            String[] tokens = uri.split("\\?");
            path = tokens[0];
            queryParam = tokens[1];
        } else {
            path = uri;
            queryParam = "";
        }
        String[] pathParams = path.split("/");
        Map<String, String> queryParamMap = new HashMap<>();
        if (!queryParam.isEmpty()) {
            String[] queryParams = queryParam.split("\\&");
            for (String queryPair : queryParams) {
                if (!queryPair.isEmpty() && queryPair.contains("=")) {
                    String[] kv = queryPair.split("\\=");
                    queryParamMap.put(kv[0], kv[1]);
                }
            }
        }
        return new HttpPath("api", pathParams, queryParamMap);
    }

    public boolean hasPathParamAt(int indexOf) {
        final int startIndex = ArrayUtils.indexOf(pathParams, root);
        return pathParams.length > startIndex+indexOf;
    }

    public String getPathParamAt(int index) {
        final int startIndex = ArrayUtils.indexOf(pathParams, root);
        return pathParams[startIndex+index];
    }

    public Integer getPathParamAsIntAt(int index) {
        return Integer.parseInt(getPathParamAt(index));
    }

    public String toUri() {
        return Strings.join(pathParams, "/");
    }

    public boolean hasQueryParam(String name) {
        return queryParams.containsKey(name);
    }

    public String getQueryParam(String name) {
        return queryParams.get(name);
    }

    public String getQueryParam(String name, String defaultValue) {
        return hasQueryParam(name) ? queryParams.get(name) : defaultValue;
    }

    public int getQueryParamAsInt(String name) {
        return Integer.parseInt(getQueryParam(name));
    }

    public int getQueryParamAsInt(String name, Integer defaultValue) {
        return hasQueryParam(name) ? Integer.parseInt(getQueryParam(name)) : defaultValue;
    }
}
