package net.loune.log4j2android;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.selector.ContextSelector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by loune on 16/05/2015.
 */
public class AndroidContextSelector implements ContextSelector {

    private static final LoggerContext CONTEXT = new LoggerContext("Default");

    //private static boolean isStarted = false;

    private void start(LoggerContext context) {
        InputStream stream = AndroidLog4jHelper.getConfig();

        ConfigurationSource source = null;
        try {
            source = new ConfigurationSource(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Configuration config = org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory.getInstance().getConfiguration(source);
        context.start(config);
    }

    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext) {
        if (!CONTEXT.isStarted()) {
            start(CONTEXT);
        }
        final LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
        return ctx != null ? ctx : CONTEXT;
    }


    @Override
    public LoggerContext getContext(final String fqcn, final ClassLoader loader, final boolean currentContext,
                                    final URI configLocation) {
        if (!CONTEXT.isStarted()) {
            start(CONTEXT);
        }
        final LoggerContext ctx = ContextAnchor.THREAD_CONTEXT.get();
        return ctx != null ? ctx : CONTEXT;
    }

    public LoggerContext locateContext(final String name, final String configLocation) {
        if (!CONTEXT.isStarted()) {
            start(CONTEXT);
        }
        return CONTEXT;
    }

    @Override
    public void removeContext(final LoggerContext context) {
    }

    @Override
    public List<LoggerContext> getLoggerContexts() {
        if (!CONTEXT.isStarted()) {
            start(CONTEXT);
        }
        final List<LoggerContext> list = new ArrayList<LoggerContext>();
        list.add(CONTEXT);
        return Collections.unmodifiableList(list);
    }
}
