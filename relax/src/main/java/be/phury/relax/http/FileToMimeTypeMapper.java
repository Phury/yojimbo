package be.phury.relax.http;

import be.phury.boilerplate.collections.MapBuilder;

import java.io.File;
import java.util.Map;

/**
 * Maps file to mime types
 */
public final class FileToMimeTypeMapper {
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

    public String getMimeType(String fileType) {
        return MIME_TYPES_MAP.get(fileType);
    }

    public String getMimeType(File file) {
        String fileName = getFileName(file);
        return getMimeType(fileName);
    }

    private String getFileName(File file) {
        String path = file.getPath();
        return path.substring(path.lastIndexOf('.'), path.length());
    }
}
