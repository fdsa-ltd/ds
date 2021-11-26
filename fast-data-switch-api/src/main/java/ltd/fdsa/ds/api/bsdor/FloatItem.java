package ltd.fdsa.ds.api.bsdor;

import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class FloatItem implements Item<Float> {
    private final Float value;

    public FloatItem(Float value) {
        this.value = value;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(5);
        buffer.put(this.getType().getValue().byteValue());
        buffer.putFloat(this.value);
        return buffer.array();
    }

    @Override
    public Float getValue() {
        return this.value;
    }

    @Override
    public Type getType() {
        return Type.FLOAT;
    }
}
