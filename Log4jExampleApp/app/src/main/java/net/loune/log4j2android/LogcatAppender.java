package net.loune.log4j2android;

import android.util.Log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.nio.charset.Charset;
import org.apache.logging.log4j.core.Filter;

/**
 * Created by loune on 16/05/2015.
 */
@Plugin(name = "Logcat", category = "Core", elementType = "appender", printObject = true)
public final class LogcatAppender extends AbstractAppender {

    private final Charset UTF8_CHARSET = Charset.forName("UTF-8");
    private LogcatAppender(String name, Layout<? extends Serializable> layout, Filter filter,
                         boolean ignoreExceptions) {
        super(name, filter, layout);
    }

    @PluginFactory
    public static LogcatAppender createAppender(@PluginAttribute("name") String name,
                                              @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
                                              @PluginElement("Layout") Layout layout,
                                              @PluginElement("Filters") Filter filter) {

        if (name == null) {
            LOGGER.error("No name provided for LogcatAppender");
            return null;
        }

        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new LogcatAppender(name, layout, filter, ignoreExceptions);
    }

    @Override
    public void append(LogEvent event) {
        byte[] b = getLayout().toByteArray(event);
        String message = new String(b, UTF8_CHARSET);

        if (event.getLevel() == Level.DEBUG) {
            Log.d(event.getLoggerName(), message);
        }
        else if (event.getLevel() == Level.ERROR || event.getLevel() == Level.FATAL) {
            Log.e(event.getLoggerName(), message);
        }
        else if (event.getLevel() == Level.INFO) {
            Log.i(event.getLoggerName(), message);
        }
        else if (event.getLevel() == Level.WARN) {
            Log.w(event.getLoggerName(), message);
        }
        else {
            Log.d(event.getLoggerName(), message);
        }
    }
}