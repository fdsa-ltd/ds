package ltd.fdsa.ds.api.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Record {

    private final List<Column> columns;

    public Record(Column... columns) {
        this.columns = Arrays.stream(columns).collect(Collectors.toList());
    }

    public boolean add(Column... columns) {
        if (columns.length == 1) {
            return this.columns.add(columns[0]);
        }
        if (columns.length > 1) {
            return this.columns.addAll(Arrays.stream(columns).collect(Collectors.toList()));
        }
        return true;
    }

    public int size() {
        return this.columns.size();
    }

    public boolean isEmpty() {
        return this.columns == null || this.columns.size() <= 0;
    }

    public Collection<Column> entrySet() {
        return this.columns;
    }

    public Map<String, Column> columnMap() {
        Map<String, Column> map = new HashMap<>(this.columns.size());
        for (var item : this.columns) {
            map.put(item.getKey(), item);
        }
        return map;
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this.columnMap());
        } catch (JsonProcessingException e) {
            log.error("json failed", e);
            return "";
        }
    }
}
