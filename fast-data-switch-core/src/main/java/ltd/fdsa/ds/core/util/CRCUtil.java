package ltd.fdsa.ds.core.util;

import lombok.var;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class CRCUtil {


    private static CRC32 crc32 = new CRC32();

    private CRCUtil() {
    }

    public static byte[] crc32(byte[] content) {
        crc32.reset();
        crc32.update(content);
        var value = crc32.getValue();
        return toBytes(value);
    }

    static byte[] toBytes(long value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value & 0xff);
        bytes[1] = (byte) ((value & 0xff00) >> 8);
        bytes[2] = (byte) ((value & 0xff0000) >> 16);
        bytes[3] = (byte) ((value & 0xff000000) >> 24);
        return bytes;
    }

    public static boolean check(byte[] content, byte[] crc32) {
        var origenal = crc32(content);
        for (int i = 0; i < 4; i++) {
            if (origenal[i] != crc32[i]) {
                return false;
            }
        }
        return true;
//        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
//         buffer.put( crc32(content));
//        buffer.flip();//need flip
//        var origenal =    buffer.getLong();
//

    }

}
