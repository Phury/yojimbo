package yojimbo;

import boilerplate.io.Streams;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilities to server static files
 */
public class Server {

    public static final class Response {
        private Integer status;
        private String contentType;
        private String content;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static final class MimeTypes {
        private Map<String, String> MIME_TYPES_MAP = new HashMap<String, String>(){
            {
                put(".html", "text/html");
                put(".js", "application/javascript");
                put(".css", "text/css");
            }

            @Override
            public String get(Object key) {
                return containsKey(key) ? super.get(key) : "text/html";
            }
        };

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

    private File staticFolder;
    private Integer port = 8081;
    private String contextRoot = "/yojimbo";

    public Server(File staticFolder) {
        this.staticFolder = staticFolder;
    }

    public void serve() {

        try {

            MimeTypes mimeTypes = new MimeTypes();
            InetSocketAddress address = new InetSocketAddress("localhost", getPort());
            HttpServer httpServer = HttpServer.create(address, 0);
            httpServer.createContext(contextRoot, httpExchange -> {
                String uri = httpExchange.getRequestURI().toString();
                String resource = uri.substring(uri.indexOf(contextRoot) + contextRoot.length() + 1, uri.length());
                File file = new File(staticFolder, resource);
                Response response = new Response();
                if (!file.exists()) {
                    response.setContent(notFound(uri));
                    response.setContentType(mimeTypes.getContentType(".html"));
                    response.setStatus(404);
                } else if (file.equals(staticFolder)) {
                    response.setContent(listDirectory(uri, file));
                    response.setContentType(mimeTypes.getContentType(".html"));
                    response.setStatus(200);
                } else {
                    response.setContent(new String(Files.readAllBytes(file.toPath())));
                    response.setContentType(mimeTypes.getContentType(file));
                    response.setStatus(200);
                }
                httpExchange.getResponseHeaders().put("content-type", Arrays.asList(response.getContentType()));
                httpExchange.sendResponseHeaders(response.getStatus(), response.getContent().length());
                Streams.pipe(response.getContent(), httpExchange.getResponseBody());
                httpExchange.getResponseBody().close();
            });
            httpServer.setExecutor(null); // creates a default executor
            httpServer.start();

            System.out.println("server started at : http://" + address.getHostName() + ":" + getPort() + getContextRoot() + "/");
            Thread.sleep(Long.MAX_VALUE);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String notFound(String uri) {
        return "<h1>404</h1><p>content not found</p>";
    }

    private String listDirectory(String uri, File file) {
        String fileList = "";
        for (File child : file.listFiles()) {
            fileList += "<li><a href=\"" + (uri + child.getName()) + "\">" + child.getName() + "</a></li>";
        }
        return "<h1>Index of : " + uri + "</h1><ul>" + fileList + "</ul>";
    }

    public Integer getPort() {
        return port;
    }

    public Server setPort(Integer port) {
        this.port = port;
        return this;
    }

    public String getContextRoot() {
        return contextRoot;
    }

    public Server setContextRoot(String contextRoot) {
        this.contextRoot = contextRoot;
        return this;
    }

    public File getStaticFolder() {
        return staticFolder;
    }

    public Server setStaticFolder(String staticFolder) {
        this.staticFolder = new File(staticFolder);
        if (!this.staticFolder.isDirectory()) {
            throw new RuntimeException("static folder must be a directory, was: " + staticFolder);
        }
        return this;
    }
}
