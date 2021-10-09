package ltd.fdsa.switcher.core.model;

import lombok.Data;
import lombok.var;
import ltd.fdsa.switcher.core.cbor.CborBuilder;
import ltd.fdsa.switcher.core.cbor.CborDecoder;
import ltd.fdsa.switcher.core.cbor.CborEncoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.DataItem;
import ltd.fdsa.switcher.core.cbor.model.MajorType;
import ltd.fdsa.switcher.core.sbor.DateData;
import ltd.fdsa.switcher.core.sbor.StringData;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

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
