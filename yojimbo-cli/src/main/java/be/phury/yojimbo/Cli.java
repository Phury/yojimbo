package be.phury.yojimbo;

import be.phury.boilerplate.collections.MapBuilder;
import be.phury.boilerplate.lang.Strings;
import be.phury.boilerplate.config.ConfigurationLoader;
import be.phury.boilerplate.log.Loggable;
import org.slf4j.Logger;

import java.util.Map;

/**
 * command line interpreter
 */
public class Cli implements Loggable {

    private Logger logger = getLogger();
    private YojimboConfiguration config = new ConfigurationLoader().loadConfiguration(new YojimboConfiguration());
    private Map<String, Command> commands = MapBuilder.<String, Command>newBuilder()
            .put("merge", str -> {
                logger.debug("running merge command");
                new Yojimbo()
                        .setInputFolder(config.getInputFolder() + "/dev")
                        .setOutputFolder(config.getInputFolder() + "/dist")
                        .jsCompress()
                        .merge("app.js");
            })
            .put("compress", str -> {
                logger.debug("running compress command");
                new Yojimbo()
                        .setInputFolder(config.getInputFolder() + "/dist")
                        .setOutputFolder(config.getInputFolder() + "/dist")
                        .jsCompress()
                        .compress("app.js");
            })
            .put("bower_install", str -> {
                logger.debug("running bower_install command");
                new Yojimbo()
                        .setInputFolder(config.getInputFolder() + "/bower_components")
                        .setOutputFolder(config.getInputFolder() + "/bower_components")
                        .bower()
                        .installAll("babel", "bootstrap", "react", "underscore", "keycloak");
            })
            .put("serve", str -> {
                logger.debug("running serve command");
                new Yojimbo()
                        .server()
                        .setPort(config.getServerPort())
                        .setContextRoot(config.getServerContextRoot())
                        .setStaticFolder(config.getServerStaticFolder())
                        .serve();
            })
            .defaultValue(str -> {
                System.out.printf("command [%s] does not exist, try: [compress, server]\n", str);
            })
            .build();

    public static void main(String... args) {
        System.out.println("[" + Strings.join(args, ", ") + "]");
        new Cli().execute(args);
    }

    private void execute(String[] args) {
        for (String arg : args) {
            commands.get(arg).execute(arg);
        }
    }

    public interface Command extends Loggable {
        void execute(String commandStr);
    }
}
