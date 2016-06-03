package be.phury.relax.http;

/**
 * Http headers definition
 */
public enum HttpHeaders {

    ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),
    ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers"),
    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),
    CONTENT_DISPOSITION("Content-Disposition");

    private final String value;

    HttpHeaders(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}