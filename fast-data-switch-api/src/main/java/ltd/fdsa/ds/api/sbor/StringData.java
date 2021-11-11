package ltd.fdsa.ds.api.sbor;

import lombok.var;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringData implements Item {
    private String data;

    public StringData(String data) {
        this.data = data;
    }

    @Override
    public Item parse(byte[] bytes) {
        if (bytes.length < 1 || bytes[0] != this.getType().ordinal()) {
            return null;
        }
        int length = 0;
        for (var i = 1; i < bytes.length; i++) {
            int c = bytes[i] & 0xff;
            if (c < 255) {
                length += c;
                ByteBuffer byteBuffer = ByteBuffer.allocate(length);
                byteBuffer.put(bytes, i, length);
                byteBuffer.flip();
                var data = new String(byteBuffer.array(), StandardCharsets.UTF_8);
                return new StringData(data);
            } else {
                length += bytes[i];
            }
        }
        return null;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer;
        var content = this.data.getBytes(StandardCharsets.UTF_8);
        var length = content.length + 1;
        var c = content.length / 255;
        if (c == 0) {  //如果小于255长度
            buffer = ByteBuffer.allocate(length + c + 1);
            buffer.put((byte) getType().ordinal());
            buffer.put((byte) content.length);
        } else {
            var s = content.length % 255;
            if (s == 0) { //如果整除
                buffer = ByteBuffer.allocate(length + c);
                buffer.put((byte) getType().ordinal());
                for (int i = 0; i < c; i++) {
                    buffer.put((byte) 255);
                }
            } else {
                buffer = ByteBuffer.allocate(length + c + 1);
                buffer.put((byte) getType().ordinal());
                for (int i = 0; i < c; i++) {
                    buffer.put((byte) 255);
                }
                buffer.put((byte) s);
            }
        }
        buffer.put(content);
        return buffer.array();
    }

    @Override
    public Object getValue() {
        return this.data;
    }

    @Override
    public Type getType() {
        return Type.Chars;
    }
}
