package net.loune.log4j2android;

import android.content.Context;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.processor.PluginEntry;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginRegistry;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil;
import org.apache.logging.log4j.core.util.Loader;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by loune on 9/04/2016.
 */
public class AndroidLog4jHelper {


    private static Context appContext;
    private static int configResource;

    public static Context getApplicationContext() {
        return appContext;
    }

    public static void initialise(Context context, int configResource) {
        appContext = context;
        AndroidLog4jHelper.configResource = configResource;
        System.setProperty("Log4jContextSelector", "net.loune.log4j2android.AndroidContextSelector");
        System.setProperty("log4j2.disable.jmx", "true");
        injectPlugins("com.nasquan.opalsense.app.utility", new Class<?>[] { AndroidLookup.class, LogcatAppender.class });
    }

    public static void injectPlugins(String packageName, Class<?>[] classes) {
        PluginRegistry reg = PluginRegistry.getInstance();

        try {
            Field f = reg.getClass().getDeclaredField("pluginsByCategoryByPackage");
            f.setAccessible(true);
            try {
                ConcurrentMap<String, Map<String, List<PluginType<?>>>> map = (ConcurrentMap<String, Map<String, List<PluginType<?>>>>) f.get(reg);
                map.put(packageName, loadFromClasses(classes));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        PluginManager.addPackage(packageName);
    }

    static InputStream getConfig() {
        return getApplicationContext().getResources().openRawResource(configResource);
    }

    private static Map<String, List<PluginType<?>>> loadFromClasses(Class<?>[] classes) {

        final ResolverUtil resolver = new ResolverUtil();
        final ClassLoader classLoader = Loader.getClassLoader();
        if (classLoader != null) {
            resolver.setClassLoader(classLoader);
        }

        final Map<String, List<PluginType<?>>> newPluginsByCategory = new HashMap<String, List<PluginType<?>>>();
        for (final Class<?> clazz : classes ) {
            final Plugin plugin = clazz.getAnnotation(Plugin.class);
            final String categoryLowerCase = plugin.category().toLowerCase();
            List<PluginType<?>> list = newPluginsByCategory.get(categoryLowerCase);
            if (list == null) {
                newPluginsByCategory.put(categoryLowerCase, list = new ArrayList<PluginType<?>>());
            }
            final PluginEntry mainEntry = new PluginEntry();
            final String mainElementName = plugin.elementType().equals(
                    Plugin.EMPTY) ? plugin.name() : plugin.elementType();
            mainEntry.setKey(plugin.name().toLowerCase());
            mainEntry.setName(plugin.name());
            mainEntry.setCategory(plugin.category());
            mainEntry.setClassName(clazz.getName());
            mainEntry.setPrintable(plugin.printObject());
            mainEntry.setDefer(plugin.deferChildren());
            @SuppressWarnings({"unchecked","rawtypes"})
            final PluginType<?> mainType = new PluginType(mainEntry, clazz, mainElementName);
            list.add(mainType);
            final PluginAliases pluginAliases = clazz.getAnnotation(PluginAliases.class);
            if (pluginAliases != null) {
                for (final String alias : pluginAliases.value()) {
                    final PluginEntry aliasEntry = new PluginEntry();
                    final String aliasElementName = plugin.elementType().equals(
                            Plugin.EMPTY) ? alias.trim() : plugin.elementType();
                    aliasEntry.setKey(alias.trim().toLowerCase());
                    aliasEntry.setName(plugin.name());
                    aliasEntry.setCategory(plugin.category());
                    aliasEntry.setClassName(clazz.getName());
                    aliasEntry.setPrintable(plugin.printObject());
                    aliasEntry.setDefer(plugin.deferChildren());
                    @SuppressWarnings({"unchecked","rawtypes"})
                    final PluginType<?> aliasType = new PluginType(aliasEntry, clazz, aliasElementName);
                    list.add(aliasType);
                }
            }
        }

        return newPluginsByCategory;
    }
}
