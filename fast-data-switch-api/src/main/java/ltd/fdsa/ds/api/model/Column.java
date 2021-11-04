package ltd.fdsa.ds.api.model;

import lombok.Data;
import lombok.var;
import ltd.fdsa.ds.api.sbor.DateData;
import ltd.fdsa.ds.api.sbor.StringData;

@Data
public class Column {
    private final String key;
    private final Object value;

    public Column(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public byte[] toByteArray() {
        var item = new StringData(key);
        var kya = new DateData((long) this.value);
        return item.toBytes();
    }
}
