package ltd.fdsa.switcher.core.sbor;

import java.nio.ByteBuffer;

public class ByteData implements Item {
    private final byte data;

    public ByteData(byte data) {
        this.data = data;
    }

    @Override
    public Item parse(byte[] bytes) {
        if (bytes.length != 2) {
            return null;
        }
        if (bytes[0] != this.getType().ordinal()) {
            return null;
        }
        return new ByteData(bytes[1]);
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put((byte) getType().ordinal());
        buffer.put(this.data);
        return buffer.array();
    }

    @Override
    public Object getValue() {
        return this.data;
    }

    @Override
    public Type getType() {
        return Type.Byte0;
    }
}
