package yojimbo;

import boilerplate.io.Streams;
import boilerplate.log.Loggable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Bower utilities
 */
public class Bower implements Loggable {

    private static final Gson GSON = new Gson();

    private List<BowerPackage> bowerPackages;

    private File inputFolder;
    private File outputFolder;

    public Bower(File inputFolder, File outputFolder) {
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
    }

    public Bower installAll(String... dependencies) {
        for (String dependency : dependencies) {
            install(dependency);
        }
        return this;
    }

    public Bower install(String dependency) {
        List<BowerPackage> bowerPackages = find(dependency);
        if (bowerPackages.size() > 0) {
            BowerPackage bowerPackage = bowerPackages.get(0);
            getLogger().debug("installing dependency : {}", dependency);
            download(bowerPackage);
            extract(bowerPackage);
        } else {
            getLogger().debug("could not find dependency : {}", dependency);
        }
        return this;
    }

    private void extract(BowerPackage bowerPackage) {
        String zip = "/tmp/" + bowerPackage.getName() + ".zip";
        getLogger().debug("reading archive : {}", zip);

        try {
            ZipFile zipFile = new ZipFile(zip);
            Enumeration<?> enu = zipFile.entries();

            String rootName = null;

            while (enu.hasMoreElements()) {
                ZipEntry zipEntry = (ZipEntry) enu.nextElement();

                String name =  zipEntry.getName();
                if (rootName == null) {
                    rootName = name;
                }

                File file = new File("/tmp/", name);
                if (name.endsWith("/")) {
                    file.mkdirs();
                    continue;
                }

                File parent = file.getParentFile();
                if (parent != null) {
                    parent.mkdirs();
                }

                getLogger().debug("extracting : {}", file.getPath());

                InputStream is = zipFile.getInputStream(zipEntry);
                FileOutputStream fos = new FileOutputStream(file);
                Streams.pipe(is, fos);
                is.close();
                fos.close();

            }
            zipFile.close();

            moveTemp(bowerPackage, rootName);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void moveTemp(BowerPackage bowerPackage, String rootName) {
        File srt = new File("/tmp/", rootName);
        File dst = new File(outputFolder, bowerPackage.getName());
        if (srt.isDirectory()) {
            getLogger().debug("moving : {} to: {}", srt, dst);
            srt.renameTo(dst);
        }
    }

    private void download(BowerPackage bowerPackage) {
        String url = bowerPackage.getWebsite() + "/archive/master.zip";
        String zip = "/tmp/" + bowerPackage.getName() + ".zip";

        getLogger().debug("downloading package from : {}", url);

        try (
                FileOutputStream fos = new FileOutputStream(zip)
        ) {

            URL masterZip = new URL(bowerPackage.getWebsite() + "/archive/master.zip");
            ReadableByteChannel rbc = Channels.newChannel(masterZip.openStream());
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getLogger().debug("downloaded package to : {}", zip);
    }

    public List<BowerPackage> find(String name) {
        List<BowerPackage> matchingPackages = listPackages()
                .stream()
                .filter(p -> p.getName().equals(name))
                .collect(Collectors.toList());
        return matchingPackages;
    }

    private List<BowerPackage> listPackages() {
        if (bowerPackages == null) {
            try (
                    InputStreamReader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("bower.json"))
            ) {

                final URI uri = ClassLoader.getSystemResource("bower.json").toURI();
                getLogger().debug("loading bower repository from : {}", uri);
                final Type listType = new TypeToken<ArrayList<BowerPackage>>() {}.getType();
                bowerPackages = GSON.fromJson(reader, listType);

            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return bowerPackages;
    }
}
