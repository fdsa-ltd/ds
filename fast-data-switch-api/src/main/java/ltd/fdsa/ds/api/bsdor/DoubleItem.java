package ltd.fdsa.ds.api.bsdor;

import lombok.Data;

import java.nio.ByteBuffer;
import java.util.Date;

@Data
public class DoubleItem implements Item<Double> {
    private final Double value;

    public DoubleItem(Double value) {
        this.value = value;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put(this.getType().getValue().byteValue());
        buffer.putDouble(this.value);
        return buffer.array();
    }

    @Override
    public Double getValue() {
        return this.value;
    }

    @Override
    public Type getType() {
        return Type.DOUBLE;
    }
}
