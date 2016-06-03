import org.testng.annotations.Test;
import be.phury.yojimbo.Yojimbo;

@Test(enabled = false)
public class YojimboTest {

    public static final String RESOURCE_PATH = "/Users/hvandevelde/Documents/java/yojimbo/src/test/resources";

    @Test
    public void testJsCompress() {
        new Yojimbo()
                .setInputFolder(getResourcePath())
                .setOutputFolder(getResourcePath() + "/out")
                .jsCompress()
                .merge(
                        "app.js",
                        "1.js",
                        "2.js",
                        "3.js")
                .compress("out/app.js", "app.min.js");
    }

    @Test
    public void testBower() {
        new Yojimbo()
                .setOutputFolder(getResourcePath() + "/boilerplate/bower_components")
                .bower()
                .install("react");
    }

    @Test
    public void testServer() {
        new Yojimbo()
                .server()
                .setPort(8001)
                .setContextRoot("/foobar")
                .setStaticFolder(getResourcePath())
                .serve();
    }
    
    private String getResourcePath() {
        return RESOURCE_PATH;
    }
}
