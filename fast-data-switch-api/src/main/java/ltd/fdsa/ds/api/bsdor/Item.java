package ltd.fdsa.ds.api.bsdor;

import com.google.common.base.Strings;
import lombok.var;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/*
 * 自描述数据结构
 * 第一个byte作为类型，根据类型不同: 定长时，直取内容；变长时，取N个bytes作为内容的长度length。再取length个类型的单元作为内容；
 * */
public interface Item {
    static Map createFromByteArray(byte[] input) {
        Map<String, Object> map = new HashMap<String, Object>();
        Integer offset = 0;
        var key = new BytesData(input, offset);
        if (key.getValue() == null) {
            return map;
        }
        var value = new BytesData(input, offset);
        map.put(key.getValue().toString(), value.getValue());
        return map;
    }

    static byte[] toByteArray(Map<String, Object> map) {
        ByteBuffer buffer = ByteBuffer.allocate(1);
        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            if (Strings.isNullOrEmpty(key)) {
                buffer.put((byte) Type.Empty.value);
            } else {
                var bytes = key.getBytes(StandardCharsets.UTF_8);
                var length = bytes.length;
                if (length >= 63) {
                    buffer.put((byte) 0B00111111);
                    length >>= 6;
                    while (length >= 255) {
                        buffer.put((byte) 0B11111111);
                    }
                }
                buffer.put((byte) length);
                buffer.put(bytes);
            }
            var value = entry.getValue();
            if (value instanceof Number) {
                var v = ((Number) value).byteValue();
                if (v > 0) ;
            }
        }
        return "".getBytes(StandardCharsets.UTF_8);
    }

    byte[] toByteArray();

    Object getValue();

    Type getType();

    enum Type {
        Null(0B00000000),
        Zero(0B11000000),
        //正
        P001(0B00000001),
        P059(0B00111011),
        P060(0B10001111),
        P108(0B10111111),
        P8(0B00111100),
        P16(0B00111101),
        P32(0B00111110),
        P64(0B00111111),
        //负
        N001(0B11000000),
        N059(0B11111011),
        N8(0B11111100),
        N16(0B11111101),
        N32(0B11111110),
        N64(0B11111111),
        // string
        Empty(0B01000000),
        // false
        False(0B10000000),
        // true
        True(0B10000001),
        // float
        Float(0B10000010),
        // double
        Double(0B10000011),
        // timestamp
        Timestamp(0B10000100),
        // decimal
        Decimal(0B10000101),
        // byte[]
        Bytes(0B10000110),
        // ref
        REF(0B10000111),
        Others(0B10001110);

        private final int value;

        Type(int input) {
            this.value = input;
        }
    }
}
