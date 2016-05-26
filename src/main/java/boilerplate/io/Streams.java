package boilerplate.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utilities for stream operations
 */
public class Streams {

    /**
     * Copies the content of input stream to output stream
     * @param is
     * @param os
     * @throws IOException
     */
    public static void pipe(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[16384];
        int length;
        while ((length = is.read(bytes)) >= 0) {
            os.write(bytes, 0, length);
        }
    }

    /**
     * Copies the content of the string to output stream
     * @param s
     * @param os
     * @throws IOException
     */
    public static void pipe(String s, OutputStream os) throws IOException {
        pipe(new ByteArrayInputStream(s.getBytes()), os);
    }
}
