package ltd.fdsa.ds.api.model;

import com.google.iot.cbor.CborConversionException;
import com.google.iot.cbor.CborObject;
import com.google.iot.cbor.CborTextString;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.sbor.Item;

import java.nio.ByteBuffer;

@Data
@Slf4j
public class Column implements Item {
    private final String key;
    private final Object value;

    public Column(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Item parse(byte[] bytes) {
        return null;
    }

    public byte[] toByteArray() {
        try {
            var dataItem = CborObject.createFromJavaObject(this.value).toCborByteArray();
            var keyItem = CborTextString.create(this.key).toCborByteArray();
            ByteBuffer buffer = ByteBuffer.allocate(keyItem.length + dataItem.length);
            buffer.put(keyItem);
            buffer.put(dataItem);
            return buffer.array();
        } catch (CborConversionException e) {
            log.error("cbor conversion exception", e);
            return new byte[0];
        }
    }

    @Override
    public Type getType() {
        return null;
    }
}
