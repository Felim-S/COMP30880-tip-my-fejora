package atlas;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class AtlasLogger {
    private static final Logger ROOT = init();

    private static Logger init() {
        Logger logger = Logger.getLogger("atlas");
        try {
            FileHandler fh = new FileHandler("atlas.log", true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            System.err.println("Could not set up log file: " + e.getMessage());
        }
        return logger;
    }

    public static Logger getLogger(Class<?> clazz) {
        return Logger.getLogger(clazz.getName());
    }
}
