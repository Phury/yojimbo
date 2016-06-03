package be.phury.yojimbo;

import be.phury.boilerplate.config.Config;
import be.phury.boilerplate.config.ConfigFile;

/**
 * Options for yojimbo cli
 */
@ConfigFile("yojimbo.properties")
public class YojimboConfiguration {

    @Config("yojimbo.input.folder")
    private String inputFolder;

    @Config("yojimbo.server.contextRoot")
    private String serverContextRoot;

    @Config("yojimbo.server.port")
    private Integer serverPort;

    @Config("yojimbo.server.staticFolder")
    private String serverStaticFolder;

    public String getInputFolder() {
        return inputFolder;
    }

    public void setInputFolder(String inputFolder) {
        this.inputFolder = inputFolder;
    }

    public String getServerContextRoot() {
        return serverContextRoot;
    }

    public void setServerContextRoot(String serverContextRoot) {
        this.serverContextRoot = serverContextRoot;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerStaticFolder() {
        return serverStaticFolder;
    }

    public void setServerStaticFolder(String serverStaticFolder) {
        this.serverStaticFolder = serverStaticFolder;
    }
}
