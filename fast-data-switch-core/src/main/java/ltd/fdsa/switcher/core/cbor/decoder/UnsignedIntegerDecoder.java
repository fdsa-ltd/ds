package ltd.fdsa.switcher.core.cbor.decoder;

import java.io.InputStream;

import ltd.fdsa.switcher.core.cbor.CborDecoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.UnsignedInteger;

public class UnsignedIntegerDecoder extends AbstractDecoder<UnsignedInteger> {

    public UnsignedIntegerDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
    }

    @Override
    public UnsignedInteger decode(int initialByte) throws CborException {
        return new UnsignedInteger(getLengthAsBigInteger(initialByte));
    }

}
