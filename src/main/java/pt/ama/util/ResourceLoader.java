package pt.ama.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader {

    private ResourceLoader() {
        super();
    }

    public static InputStream load(String path) throws IOException {
        if (path == null) {
            throw new IOException("Path is required!");
        }

        if (path.startsWith("file:")) {
            return new FileInputStream(path.replace("file:", ""));
        }

        if (path.startsWith("classpath:")) {
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(path.replace("classpath:", ""));
        }

        throw new IOException("URI Scheme " + path + " not supported!");
    }

}
