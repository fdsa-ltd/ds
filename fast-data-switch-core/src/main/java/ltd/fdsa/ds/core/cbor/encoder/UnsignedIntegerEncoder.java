package ltd.fdsa.ds.core.cbor.encoder;

import java.io.OutputStream;

import ltd.fdsa.ds.core.cbor.CborEncoder;
import ltd.fdsa.ds.core.cbor.CborException;
import ltd.fdsa.ds.core.cbor.model.MajorType;
import ltd.fdsa.ds.core.cbor.model.UnsignedInteger;

public class UnsignedIntegerEncoder extends AbstractEncoder<UnsignedInteger> {

    public UnsignedIntegerEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
    }

    @Override
    public void encode(UnsignedInteger dataItem) throws CborException {
        encodeTypeAndLength(MajorType.UNSIGNED_INTEGER, dataItem.getValue());
    }

}
