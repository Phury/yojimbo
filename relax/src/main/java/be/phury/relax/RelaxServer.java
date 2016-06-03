package be.phury.relax;

import be.phury.boilerplate.collections.MapBuilder;
import be.phury.boilerplate.io.Streams;
import be.phury.relax.http.HttpPath;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Utilities to server static files
 */
public class RelaxServer {

    public static final String HEADER_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String HEADER_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String HEADER_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ALLOW_ANY = "*";


    private static final Gson GSON = new Gson();

    public static final class Error {
        private Integer code;
        private String codeMeaning;
        private String message;

        public Integer getCode() {
            return code;
        }

        public Error setCode(Integer code) {
            this.code = code;
            return this;
        }

        public String getCodeMeaning() {
            return codeMeaning;
        }

        public Error setCodeMeaning(String codeMeaning) {
            this.codeMeaning = codeMeaning;
            return this;
        }

        public String getMessage() {
            return message;
        }

        public Error setMessage(String message) {
            this.message = message;
            return this;
        }
    }

    public static final class Response {
        private Integer status;
        private String contentType;
        private InputStream content;
        private Map<String, String> headers;

        public Integer getStatus() {
            return status;
        }

        public Response setStatus(Integer status) {
            this.status = status;
            return this;
        }

        public String getContentType() {
            return contentType;
        }

        public Response setContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public InputStream getContent() {
            return content;
        }

        public Response setContent(File content) {
            try {
                return setContent(new FileInputStream(content));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public Response setContent(InputStream content) {
            this.content = content;
            return this;
        }

        public Response setContent(String content) {
            return setContent(new ByteArrayInputStream(content.getBytes()));
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public Response setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }
    }

    public static final class MimeTypes {
        private static final Map<String, String> MIME_TYPES_MAP = new MapBuilder<String, String>()
                .put(".html", "text/html")
                .put(".js", "application/javascript")
                .put(".css", "text/css")
                .put(".json", "application/json")
                .put(".jpg", "image/jpeg")
                .put(".jpeg", "image/jpeg")
                .put(".gif", "image/gif")
                .put(".png", "image/png")
                .put(".pdf", "application/pdf")
                .put(".zip", "application/zip")
                .defaultValue("application/octet-stream")
                .build();

        public String getContentType(String fileType) {
            return MIME_TYPES_MAP.get(fileType);
        }

        public String getContentType(File file) {
            String fileName = getFileName(file);
            return getContentType(fileName);
        }

        private String getFileName(File file) {
            String path = file.getPath();
            return path.substring(path.lastIndexOf('.'), path.length());
        }
    }

    public interface RequestHandler {
        Response handle(HttpExchange httpExchange, HttpPath httpPath);
    }

    private File staticFolder;
    private Integer port = 8081;
    private String contextRoot = "/yojimbo";
    private MimeTypes mimeTypes = new MimeTypes();
    private Map<String, RequestHandler> requestHandlers = new MapBuilder<String, RequestHandler>()
            .put("/api", (httpExchange, httpPath) -> {
                RestResourceService restResourceService = new RestResourceService();
                try {
                    Object object;
                    switch (httpExchange.getRequestMethod()) {
                        case "GET":
                            if (httpPath.hasPathParamAt(2)) {
                                object = restResourceService.get(httpPath.getPathParamAt(1), httpPath.getPathParamAsIntAt(2));
                            } else {
                                object = restResourceService.list(
                                        httpPath.getPathParamAt(1),
                                        httpPath.getQueryParamAsInt("offset", 0),
                                        httpPath.getQueryParamAsInt("limit", -1));
                            }
                            break;
                        case "POST":
                            object = restResourceService.update(httpPath.getPathParamAt(1), Streams.toString(httpExchange.getRequestBody()));
                            break;
                        case "PUT":
                            object = restResourceService.add(httpPath.getPathParamAt(1), Streams.toString(httpExchange.getRequestBody()));
                            break;
                        case "DELETE":
                            object = restResourceService.get(httpPath.getPathParamAt(1), httpPath.getPathParamAsIntAt(2));
                            break;
                        case "OPTIONS":
                            return corsHeaders(httpPath.toUri());
                        default:
                            return jsonNotFound(httpPath.toUri(), new UnsupportedOperationException(httpExchange.getRequestMethod() + " method not supported"));
                    }

                    return new Response()
                            .setContent(GSON.toJson(object))
                            .setContentType(mimeTypes.getContentType(".json"))
                            .setStatus(200);

                } catch (NoSuchElementException e) {
                    return jsonNotFound(httpPath.toUri(), e);
                } catch (JsonSyntaxException e) {
                    return jsonBadRequest(httpPath.toUri(), e);
                }
            })
            .put("/download", (httpExchange, httpPath) -> {
                String uri = httpPath.toUri();
                String resource = getResource(uri);


                if (httpPath.getQueryParam("zip") != null) {
                    File file = new ZipService().zipFiles(new File(staticFolder, resource), httpPath.getQueryParam("zip"));
                    return new Response()
                            .setContent(file)
                            .setContentType(mimeTypes.getContentType(file))
                            .setStatus(200)
                            .setHeaders(new MapBuilder<String, String>()
                                    .put("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                                    .build());
                } else {
                    File file = new File(staticFolder, resource);
                    if (!file.exists() || file.isDirectory()) {
                        return jsonBadRequest(uri, new IllegalArgumentException("could not download file [" + uri + "]"));
                    } else {
                        return new Response()
                                .setContent(file)
                                .setContentType(mimeTypes.getContentType(file))
                                .setStatus(200)
                                .setHeaders(new MapBuilder<String, String>()
                                        .put("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                                        .build());
                    }
                }
            })
            .defaultValue((httpExchange, httpPath) -> {
                Response response = new Response();

                String uri = httpPath.toUri();
                String resource = getResource(uri);
                File file = new File(staticFolder, resource);

                if (!file.exists()) {
                    return htmlNotFound(httpPath.toUri());
                } else if (file.isDirectory()) {
                    response.setContent(htmlListDirectory(file));
                    response.setContentType(mimeTypes.getContentType(".html"));
                    response.setStatus(200);
                } else {
                    response.setContent(file);
                    response.setContentType(mimeTypes.getContentType(file));
                    response.setStatus(200);
                }

                return response;
            })
            .build();

    private String getResource(String uri) {
        return uri.equals(contextRoot) ?
                "" :
                uri.substring(uri.indexOf(contextRoot) + contextRoot.length() + 1, uri.length());
    }

    private Response corsHeaders(String uri) {
        return new Response()
                .setHeaders(new MapBuilder<String, String>()
                        .put(HEADER_ALLOW_ORIGIN, ALLOW_ANY)
                        .put(HEADER_ALLOW_METHODS, "GET, POST, PUT, DELETE")
                        .put(HEADER_ALLOW_HEADERS, "Origin, X-Requested-With, Content-Type, Accept, Authorization")
                        .build());
    }

    public RelaxServer(File staticFolder) {
        this.staticFolder = staticFolder;
    }

    public void serve() {

        try {

            InetSocketAddress address = new InetSocketAddress("localhost", getPort());
            HttpServer httpServer = HttpServer.create(address, 0);
            httpServer.createContext(contextRoot, httpExchange -> {
                String uri = httpExchange.getRequestURI().toString();

                Response response;
                try {
                    response = getRequestHandler(uri).handle(httpExchange, HttpPath.parse(uri));
                } catch (Exception e) {
                    e.printStackTrace();
                    response = htmlServerError(uri, e);
                }

                if (response.getHeaders() != null) {
                    for (Map.Entry<String, String> e : response.getHeaders().entrySet()) {
                        httpExchange.getResponseHeaders().put(e.getKey(), Arrays.asList(e.getValue()));
                    }
                }

                httpExchange.getResponseHeaders().put("content-type", Arrays.asList(response.getContentType()));
                httpExchange.sendResponseHeaders(response.getStatus(), 0);
                Streams.pipe(response.getContent(), httpExchange.getResponseBody());
                httpExchange.getResponseBody().close();

            });
            httpServer.setExecutor(null); // creates a default executor
            httpServer.start();

            System.out.println(">>> server started at : http://" + address.getHostName() + ":" + getPort() + getContextRoot() + "/");
            System.out.println(">>> context root path : " + getStaticFolder());
            System.out.println(">>> api endpoint : http://" + address.getHostName() + ":" + getPort() + getContextRoot() + "/api/");
            System.out.println(">>> api collections path : \\tmp\\api");// ser RestService.database
            Thread.sleep(Long.MAX_VALUE);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private RequestHandler getRequestHandler(String uri) {
        for (Map.Entry<String, RequestHandler> e : requestHandlers.entrySet()) {
            if (uri.contains(e.getKey())) {
                return e.getValue();
            }
        }
        return requestHandlers.get("default");
    }

    private Response jsonNotFound(String uri, Throwable cause) {
        return new Response()
                .setContent(GSON.toJson(new Error()
                        .setCode(404)
                        .setCodeMeaning("content not found")
                        .setMessage(cause.getMessage())))
                .setContentType(mimeTypes.getContentType(".json"))
                .setStatus(404);
    }

    private Response jsonBadRequest(String uri, Throwable cause) {
        return new Response()
                .setContent(GSON.toJson(new Error()
                        .setCode(400)
                        .setCodeMeaning("bad request")
                        .setMessage(cause.getMessage())))
                .setContentType(mimeTypes.getContentType(".json"))
                .setStatus(400);
    }

    private Response htmlNotFound(String uri) {
        return new Response()
                .setContent("<h1>404</h1><p>content not found</p>")
                .setContentType(mimeTypes.getContentType(".html"))
                .setStatus(404);
    }

    private Response htmlServerError(String uri, Exception e) {
        return new Response()
                .setContent("<h1>500</h1><pre>"+ getStackTrace(e) +"</pre>")
                .setContentType(mimeTypes.getContentType(".html"))
                .setStatus(500);
    }

    private String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    private String htmlListDirectory(File file) {
        String fileList = "";
        for (File child : file.listFiles()) {
            fileList += "<li><a href=\"" + toUri(child) + "\">" + child.getName() + "</a></li>";
        }
        return "<h1>Index of : " + toUri(file) + "</h1><ul>" + fileList + "</ul>";
    }

    /**
     * Converts the file path to a navigable uri path
     * @param file
     * @return
     */
    private String toUri(File file) {
        String uri = getContextRoot() + file.getPath().toString().replace(getStaticFolder().getPath().toString(), "");
        return uri.replace(System.getProperty("file.separator"), "/");
    }

    public Integer getPort() {
        return port;
    }

    public RelaxServer setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public RelaxServer setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
        return this;
    }

    public File getStaticFolder() {
        return staticFolder;
    }

    public RelaxServer setStaticFolder(String staticFolder) {
        this.staticFolder = new File(staticFolder);
        if (!this.staticFolder.isDirectory()) {
            throw new RuntimeException("static folder must be a directory, was: " + staticFolder);
        }
        return this;
    }
}
