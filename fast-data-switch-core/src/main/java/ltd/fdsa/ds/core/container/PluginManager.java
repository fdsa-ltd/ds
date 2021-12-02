package ltd.fdsa.ds.core.container;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.core.props.DefaultProps;
import ltd.fdsa.ds.core.props.Props;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class PluginManager {
    private final Map<String, PluginClassLoader> classLoaderCache = new HashMap<String, PluginClassLoader>();
    private final Map<String, Props> propsCache = new HashMap<String, Props>();

    final ObjectMapper mapper = new ObjectMapper();

    public PluginManager(String pluginPath) {
        if (Strings.isNullOrEmpty(pluginPath)) {
            throw new IllegalArgumentException("basePath can not be empty!");
        }
        File directory = new File(pluginPath);
        if (!directory.exists()) {
            throw new IllegalArgumentException("basePath not exists:" + pluginPath);
        }
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("basePath must be a directory:" + pluginPath);
        }
        for (var dir : directory.list()) {

            var pluginFile = new File(dir + "/plugins.json");
            if (!pluginFile.exists()) {
                continue;
            }
            PluginClassLoader classLoader = new PluginClassLoader(dir);
            try {
                var content = String.join("\n", Files.readLines(pluginFile, StandardCharsets.UTF_8));
                var props = DefaultProps.fromJson(content);
                for (var pluginInfo : props.getConfigurations("")) {
                    classLoaderCache.put(pluginInfo.get("className"), classLoader);
                    propsCache.put(pluginInfo.get("className"), pluginInfo);
                }
            } catch (IOException e) {
                log.error("file miss", e);
            }
        }
    }

    private Class<?> loadPluginClass(String className) {
        var classLoader = this.classLoaderCache.get(className);
        try {
            return (Class<?>) classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public PluginClassLoader getClassLoader(String className) {
        return this.classLoaderCache.get(className);
    }

    public Map<String, Props> getPlugins() {
        return propsCache;
    }

    public <T> T getInstance(String className, Class<T> required) {
        Class<?> cls = this.loadPluginClass(className);
        if (required.isAssignableFrom(cls)) {
            try {
                return (T) cls.newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("can not newInstance class:" + className, e);
            }
        }
        throw new IllegalArgumentException("class:" + className + " not sub class of " + required);
    }

    public <T> T getInstance(String className, Class<T> required, Object... objects) {
        Class<?> cls = this.loadPluginClass(className);
        try {
            if (required.isAssignableFrom(cls)) {
                for (var constructor : cls.getConstructors()) {
                    return (T) constructor.newInstance(objects);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("class:" + className + " not sub class of " + required);
    }

}
