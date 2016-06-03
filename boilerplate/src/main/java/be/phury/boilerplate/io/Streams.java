package be.phury.boilerplate.io;

import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

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

    public static String toString(InputStream is) {
        return new Scanner(is, "utf-8").useDelimiter("\\Z").next();
    }

    public static String toString(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()), "UTF-8");
        } catch (IOException e) {
            throw new StreamsException(e);
        }
    }

    public static class StreamsException extends RuntimeException {

        public StreamsException(Throwable cause) {
            super(cause);
        }
    }
}
