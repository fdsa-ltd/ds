package ltd.fdsa.ds.api.bsdor;

import lombok.Data;

@Data
public class NullItem implements Item<Object> {
    @Override
    public byte[] toByteArray() {
        return new byte[]{this.getType().getValue().byteValue()};
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public Type getType() {
        return Type.NULL;
    }
}
