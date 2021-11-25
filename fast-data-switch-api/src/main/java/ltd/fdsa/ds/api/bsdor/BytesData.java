package ltd.fdsa.ds.api.bsdor;

import lombok.Data;
import lombok.var;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Data
public class BytesData {
    private final Object value;
    public BytesData(byte[] input, Integer offset) {
        var type = input[offset];
        var mainType = type >> 6;        //高2位
        var subType = type & 0B00111111; //低6位

        switch (mainType) {
            case 0: // 空与正整数类型
                offset++;
                if (subType == 0) {
                    this.value = null;
                } else if (subType <= 59) {// 1 - 59
                    this.value = subType;
                } else {  // 60,61,62,63
                    this.value = getNumber(input, offset, subType, false);
                }
                break;
            case 1: // 字符串类型
                this.value = this.getString(input, offset, subType);
                break;
            case 2: // 扩展数据类型
                this.value = null;
                break;
            case 3: // 零与负整数类型
                if (subType == 0) {
                    this.value = 0;
                } else if (subType <= 59) {
                    this.value = -subType;
                } else {
                    offset++;
                    this.value = getNumber(input, offset, subType, true);
                }
                break;
            default:
                this.value = null;
                return;
        }
    }
    private Number getNumber(byte[] input, Integer offset, int type, boolean negate) {
        ByteBuffer buffer;
        switch (type) {
            case 60:
                buffer = ByteBuffer.allocate(1);
                buffer.put(input, offset, 1);
                break;
            case 61:
                buffer = ByteBuffer.allocate(2);
                buffer.put(input, offset, 2);
                break;
            case 62:
                buffer = ByteBuffer.allocate(4);
                buffer.put(input, offset, 4);
                break;
            default:
                buffer = ByteBuffer.allocate(8);
                buffer.put(input, offset, 8);
                break;
        }
        buffer.flip();
        BigInteger b = new BigInteger(buffer.array());

        if (negate) {
            return b.negate();
        }
        return b;
    }
    private String getString(byte[] input, Integer offset, int type) {
        var length = type;
        if (length == 63) {
            offset++;
            for (; offset < input.length; offset++) {
                var size = Byte.toUnsignedInt(input[offset]);
                length += size;
                if (size < 255) {
                    break;
                }
            }
        }
        return new String(input, offset, length, StandardCharsets.UTF_8);
    }
}
