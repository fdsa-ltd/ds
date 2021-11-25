package ltd.fdsa.ds.api.bsdor;

import com.google.common.base.Strings;
import lombok.var;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/*
 * 自描述数据结构
 * 第一个byte作为类型，根据类型不同: 定长时，直取内容；变长时，取N个bytes作为内容的长度length。再取length个类型的单元作为内容；
 * */
public interface Item<T> {
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

    static byte[] toByteArray(Map<String, Item> map) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (var entry : map.entrySet()) {
            var key = entry.getKey();
            if (Strings.isNullOrEmpty(key)) {
                buffer.write(Type.Empty.value);
            } else {
                try {
                    buffer.write(new StringItem(key).toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            var value = entry.getValue();
            try {
                buffer.write(value.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toByteArray();
    }

    byte[] toByteArray();

    T getValue();

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
        S63(0B01111111),
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

        private final short value;
        private static final Type[] list;

        static {
            list = Arrays.stream(Type.values()).sorted().toArray(Type[]::new);
        }

        public static Type valueOf(Integer value) {
            return list[value];
        }

        Type(Integer value) {
            this.value = value.shortValue();
        }

        public short getValue() {
            return value;
        }

        public int getMainType() {
            return this.value >> 6;
        }

        public int getSubType() {
            return this.value & 0B00111111;
        }
    }
}
