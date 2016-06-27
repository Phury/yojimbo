package be.phury.relax;

import java.io.File;

/**
 * Handles compression of files.
 */
public interface FileCompressor {

    /**
     * Compresses all the files in the given directory
     * @param rootDir the root directory to search for
     * @param fileNames all the files in that directory to compress
     * @return a new file which is the result of the file compression
     */
    File compress(File rootDir, String[] fileNames);
}
