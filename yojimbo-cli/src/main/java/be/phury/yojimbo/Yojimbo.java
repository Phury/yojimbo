package be.phury. yojimbo;

import be.phury.relax.RelaxServer;

import java.io.File;

/**
 * Entry point of the application
 */
public class Yojimbo {

    private File inputFolder;
    private File outputFolder;

    public JsCompress jsCompress() {
        return new JsCompress(inputFolder, outputFolder);
    }

    public Bower bower() {
        return new Bower(inputFolder, outputFolder);
    }

    public RelaxServer server() {
        return new RelaxServer().setStaticFolder(inputFolder);
    }

    public Yojimbo setInputFolder(String inputFolderName) {
        this.inputFolder = new File(inputFolderName);
        if (!inputFolder.isDirectory()) {
            throw new RuntimeException("root folder is not a directory : " + inputFolderName);
        }
        return this;
    }

    public Yojimbo setOutputFolder(String outputFolderName) {
        this.outputFolder = new File(outputFolderName);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        return this;
    }

}
