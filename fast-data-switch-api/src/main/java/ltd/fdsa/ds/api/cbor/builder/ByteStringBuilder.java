package ltd.fdsa.ds.api.cbor.builder;

import ltd.fdsa.ds.api.cbor.model.SimpleValue;

public class ByteStringBuilder<T extends AbstractBuilder<?>> extends AbstractBuilder<T> {

    public ByteStringBuilder(T parent) {
        super(parent);
    }

    public ByteStringBuilder<T> add(byte[] bytes) {
        getParent().addChunk(convert(bytes));
        return this;
    }

    public T end() {
        getParent().addChunk(SimpleValue.BREAK);
        return getParent();
    }

}
