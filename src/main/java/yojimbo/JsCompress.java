package yojimbo;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Javascript compression utils
 */
public class JsCompress {

    private File inputFolder;
    private File outputFolder;

    public JsCompress(File inputFolder, File outputFolder) {
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
    }

    public JsCompress merge(String outputFilename) {
        List<String> fileNames = Arrays.asList(inputFolder.listFiles())
                .stream()
                .map(f -> f.getName())
                .collect(Collectors.toList());
        return merge(outputFilename, fileNames);
    }

    public JsCompress merge(String outputFilename, String... inputFilenames) {
        return merge(outputFilename, Arrays.asList(inputFilenames));
    }

    private JsCompress merge(String outputFilename, List<String> inputFilenames) {
        File output = new File(outputFolder, outputFilename);
        if (output.exists()) {
            output.delete();
        }
        inputFilenames
                .stream()
                .forEach(name -> {
                    try {
                        final Path path = new File(inputFolder, name).toPath();
                        Files.write(output.toPath(), Files.readAllBytes(path), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        return this;
    }

    public JsCompress compress(String inputFilename) {
        return compress(inputFilename, inputFilename.replace(".js", ".min.js"));
    }

    public JsCompress compress(String inputFilename, String outputFilename) {
        return compress(inputFilename, outputFilename, new Options());
    }

    public JsCompress compress(String inputFilename, String outputFilename, Options o) {
        File input = new File(inputFolder, inputFilename);
        File output = new File(outputFolder, outputFilename);

        try (
                Reader in = new InputStreamReader(new FileInputStream(input), o.charset);
                Writer out = new OutputStreamWriter(new FileOutputStream(output), o.charset)
        ){

            JavaScriptCompressor compressor = new JavaScriptCompressor(in, new ErrorReporter() {
                @Override
                public void warning(String s, String s1, int i, String s2, int i1) {
                }

                @Override
                public void error(String s, String s1, int i, String s2, int i1) {
                }

                @Override
                public EvaluatorException runtimeError(String s, String s1, int i, String s2, int i1) {
                    return null;
                }
            });
            compressor.compress(out, o.lineBreakPos, o.munge, o.verbose, o.preserveAllSemiColons, o.disableOptimizations);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return this;
    }

    public static class Options {
        public String charset = "UTF-8";
        public int lineBreakPos = -1;
        public boolean munge = true;
        public boolean verbose = false;
        public boolean preserveAllSemiColons = false;
        public boolean disableOptimizations = false;
    }
}
