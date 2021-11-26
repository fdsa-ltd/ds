package ltd.fdsa.ds.api.bsdor;

import lombok.Data;

import java.nio.ByteBuffer;
import java.util.Date;

@Data
public class DateTimeItem implements Item<Date> {
    private final long timestamp;

    public DateTimeItem(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(9);
        buffer.put(this.getType().getValue().byteValue());
        buffer.putLong(this.timestamp);
        return buffer.array();
    }

    @Override
    public Date getValue() {
        return new Date(this.timestamp);
    }

    @Override
    public Type getType() {
        return Type.TIMESTAMP;
    }
}
