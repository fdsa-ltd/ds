
package ltd.fdsa.ds.core.props;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.IOException;
import java.util.*;

@Slf4j
public class DefaultProps implements Props {
    static final String DOT = ".";
    static final ObjectMapper JM = new ObjectMapper();
    static final YAMLMapper YM = new YAMLMapper();
    static final JavaPropsMapper PM = new JavaPropsMapper();

    static {
        JM.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
    }

    public static Props fromProps(String content) {
        try {
            var jsonNode = PM.readTree(content);
            var map = PM.writeValueAsMap(jsonNode);
            return new DefaultProps(map);
        } catch (IOException e) {
            log.error("getPropsConfig failed", e);
        }
        return null;
    }

    public static Props fromJson(String content) {
        try {
            var jsonNode = JM.readTree(content);
            var map = PM.writeValueAsMap(jsonNode);
            return new DefaultProps(map);
        } catch (IOException e) {
            log.error("getJsonConfig failed", e);
        }
        return null;
    }

    public static Props fromYaml(String content) {
        try {
            var jsonNode = YM.readTree(content);
            var map = PM.writeValueAsMap(jsonNode);
            return new DefaultProps(map);
        } catch (IOException e) {
            log.error("getYamlConfig failed", e);
        }
        return null;
    }

    public static Props fromMaps(Map<String, String> config) {
        return new DefaultProps(config);
    }

    private final Map<String, String> config;

    DefaultProps(Map<String, String> map) {
        this.config = map;
    }

    @Override
    public Props[] getConfigurations(String path) {
        List<Props> result = new LinkedList<>();
        for (var i = 1; i <= 1024; i++) {
            Map<String, String> map = new HashMap<>();
            var key = Strings.isNullOrEmpty(path) ? i + DOT : path + DOT + i + DOT;
            for (var entry : this.config.entrySet()) {
                if (entry.getKey().startsWith(key)) {
                    map.put(entry.getKey().substring(key.length()), entry.getValue());
                }
            }
            if (map.size() > 0) {
                result.add(new DefaultProps(map));
            } else {
                break;
            }
        }
        return result.toArray(new Props[0]);
    }

    @Override
    public Props getConfiguration(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return this;
        }
        Map<String, String> map = new HashMap<>();
        var key = path + DOT;
        for (var entry : this.config.entrySet()) {
            if (entry.getKey().startsWith(key)) {
                map.put(entry.getKey().substring(key.length()), entry.getValue());
            }
        }
        if (map.size() > 0) {
            return new DefaultProps(map);
        }
        return null;
    }

    @Override
    public void set(String path, String value) {
        this.config.put(path, value);
    }

    @Override
    public Props clone() {
        Map<String, String> map = new HashMap<>();
        for (var entry : this.config.entrySet()) {
            map.put(entry.getKey(), entry.getValue());
        }
        return new DefaultProps(map);
    }

    @Override
    public String get(String path) {
        return this.config.get(path);
    }

    @Override
    public String toString() {
        try {
            return PM.writeValueAsString(this.config);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public String toJson() {
        try {
            var jsonNode = PM.readTree(this.toString());
            return JM.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public String toYaml() throws JsonProcessingException {
        try {
            var jsonNode = PM.readTree(this.toString());
            return YM.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}