package ltd.fdsa.ds.api.bsdor;

import lombok.Data;
import lombok.var;

import java.nio.charset.StandardCharsets;

@Data
public class StringData {
    private String value;

    public StringData(byte[] input, Integer offset) {
        var type = Byte.toUnsignedInt(input[offset]);
        if (type >> 6 != 1) { // 不是字符串
            return;
        }
        var length = type << 2;
        if (length == 63) {
            offset++;
            for (; offset < input.length; offset++) {
                length += input[offset] - 1;
                if (input[offset] < 255) {
                    break;
                }
            }
        }
        this.value = new String(input, offset, length, StandardCharsets.UTF_8);
    }

}
