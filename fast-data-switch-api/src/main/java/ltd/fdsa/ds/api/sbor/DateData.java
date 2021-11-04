package ltd.fdsa.ds.api.sbor;

import java.nio.ByteBuffer;
import java.util.Date;

/*
 * 使用 long 存储 millisecond
 * */
public class DateData implements Item {
    private long data;

    public DateData(long value) {
        this.data = value;
    }

    @Override
    public Item parse(byte[] bytes) {
        if (bytes.length != 9) {
            return null;
        }
        if (bytes[0] != (byte) this.getType().ordinal()) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length - 1);
        buffer.put(bytes, 1, bytes.length - 1);
        buffer.flip(); //need flip
        return new DateData(buffer.getLong());
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put((byte) getType().ordinal());
        buffer.putLong(this.data);
        return buffer.array();
    }

    @Override
    public Object getValue() {
        return new Date(this.data);
    }

    @Override
    public Type getType() {
        return Type.DATE0;
    }
}
