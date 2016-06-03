package be.phury.relax;


import be.phury.boilerplate.io.Streams;

import java.io.*;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipService {

    public File zipFiles(File rootDir, String query) {
        String[] fileNames = query.split(";");

        File zipFile = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + ".zip");

        try (
                FileOutputStream fos = new FileOutputStream(zipFile);
                ZipOutputStream zos = new ZipOutputStream(fos)
        ) {

            for (String fileName : fileNames) {
                File file = new File(rootDir, fileName);
                if (file.exists() && !file.isDirectory()) {
                    ZipEntry ze = new ZipEntry(fileName);
                    zos.putNextEntry(ze);

                    try (InputStream is = new FileInputStream(file)) {
                        Streams.pipe(is, zos);
                    }

                }
            }

        } catch (FileNotFoundException e) {
            throw new ZipException(e);
        } catch (IOException e) {
            throw new ZipException(e);
        }

        return zipFile;
    }

    public static final class ZipException extends RuntimeException {
        public ZipException(Throwable cause) {
            super(cause);
        }
    }
}
