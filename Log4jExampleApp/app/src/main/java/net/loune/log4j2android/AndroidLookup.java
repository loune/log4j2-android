package net.loune.log4j2android;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

import java.io.File;

/**
 * Created by loune on 16/05/2015.
 */
@Plugin(name = "android", category = "Lookup")
public class AndroidLookup implements StrLookup {
    /**
     * Lookup the value for the key.
     * @param key  the key to be looked up, may be null
     * @return The value for the key.
     */
    public String lookup(String key) {
        if (key.equals("filesdir")) {
            return AndroidLog4jHelper.getApplicationContext().getFilesDir().getAbsolutePath();
        }

        if (key.equals("externalfilesdir")) {
            File f = AndroidLog4jHelper.getApplicationContext().getExternalFilesDir(null);
            if (f == null) {
                return null;
            }
            return f.getAbsolutePath();
        }

        if (key.equals("logfilesdir")) {
            File f = AndroidLog4jHelper.getApplicationContext().getExternalFilesDir(null);
            if (f != null) {
                return new File(f, "logs").getAbsolutePath();
            }
            return new File(AndroidLog4jHelper.getApplicationContext().getFilesDir(), "logs").getAbsolutePath();
        }

        return null;
    }

    /**
     * Lookup the value for the key using the data in the LogEvent.
     * @param event The current LogEvent.
     * @param key  the key to be looked up, may be null
     * @return The value associated with the key.
     */
    public String lookup(LogEvent event, String key) {
        return lookup(key);
    }
}
