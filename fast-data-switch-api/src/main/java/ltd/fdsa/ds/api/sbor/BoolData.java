package ltd.fdsa.ds.api.sbor;

import java.nio.ByteBuffer;

/*
 * 使用一个字节保存一个布尔值
 * 当字节为正数时表示true
 * 当字节为非正时表示false
 * */
public class BoolData implements Item {
    private final byte data;

    public BoolData(boolean value) {
        if (value) {
            this.data = 1;
        } else {
            this.data = 0;
        }
    }

    public BoolData(byte data) {
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
        return new BoolData(bytes[1]);
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.put((byte) getType().ordinal());
        buffer.put(this.data);
        return buffer.array();
    }

    @Override
    public Object getValue() {
        return this.data > 0;
    }

    @Override
    public Type getType() {
        return Type.BOOL0;
    }
}
