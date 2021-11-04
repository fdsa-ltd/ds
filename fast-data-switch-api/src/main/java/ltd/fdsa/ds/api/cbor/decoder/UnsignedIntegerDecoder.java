package ltd.fdsa.ds.api.cbor.decoder;

import java.io.InputStream;

import ltd.fdsa.ds.api.cbor.CborDecoder;
import ltd.fdsa.ds.api.cbor.CborException;
import ltd.fdsa.ds.api.cbor.model.UnsignedInteger;

public class UnsignedIntegerDecoder extends AbstractDecoder<UnsignedInteger> {

    public UnsignedIntegerDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
    }

    @Override
    public UnsignedInteger decode(int initialByte) throws CborException {
        return new UnsignedInteger(getLengthAsBigInteger(initialByte));
    }

}
