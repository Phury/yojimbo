package be.phury.relax.http;


/**
 * Http status definition
 */
public enum HttpStatus {
    OK(200);


    private final Integer code;

    HttpStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
