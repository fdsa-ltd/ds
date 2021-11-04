package ltd.fdsa.ds.core.sbor;

import java.nio.ByteBuffer;

public class IntData implements Item {
    private int data;

    public IntData(int value) {
        this.data = value;
    }

    @Override
    public Item parse(byte[] bytes) {
        if (bytes.length != 5) {
            return null;
        }
        if (bytes[0] != (byte) this.getType().ordinal()) {
            return null;
        }
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length - 1);
        buffer.put(bytes, 1, bytes.length - 1);
        buffer.flip(); //need flip
        return new IntData(buffer.getInt());
    }

    @Override
    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put((byte) getType().ordinal());
        buffer.putInt(this.data);
        return buffer.array();
    }

    @Override
    public Object getValue() {
        return this.data;
    }

    @Override
    public Type getType() {
        return Type.INT0;
    }
}
