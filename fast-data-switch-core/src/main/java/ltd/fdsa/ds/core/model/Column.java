package ltd.fdsa.ds.core.model;

import lombok.Data;
import lombok.var;
import ltd.fdsa.ds.core.sbor.DateData;
import ltd.fdsa.ds.core.sbor.StringData;

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
