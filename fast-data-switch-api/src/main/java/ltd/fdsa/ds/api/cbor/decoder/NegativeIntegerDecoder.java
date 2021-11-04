package ltd.fdsa.ds.api.cbor.decoder;

import java.io.InputStream;
import java.math.BigInteger;

import ltd.fdsa.ds.api.cbor.CborDecoder;
import ltd.fdsa.ds.api.cbor.CborException;
import ltd.fdsa.ds.api.cbor.model.NegativeInteger;

public class NegativeIntegerDecoder extends AbstractDecoder<NegativeInteger> {

    private static final BigInteger MINUS_ONE = BigInteger.valueOf(-1);

    public NegativeIntegerDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
    }

    @Override
    public NegativeInteger decode(int initialByte) throws CborException {
        return new NegativeInteger(MINUS_ONE.subtract(getLengthAsBigInteger(initialByte)));
    }

}
