package ltd.fdsa.ds.api.cbor.encoder;

import java.io.OutputStream;

import ltd.fdsa.ds.api.cbor.CborEncoder;
import ltd.fdsa.ds.api.cbor.CborException;
import ltd.fdsa.ds.api.cbor.model.ByteString;
import ltd.fdsa.ds.api.cbor.model.MajorType;
import ltd.fdsa.ds.api.cbor.model.SimpleValue;

public class ByteStringEncoder extends AbstractEncoder<ByteString> {

    public ByteStringEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
    }

    @Override
    public void encode(ByteString byteString) throws CborException {
        byte[] bytes = byteString.getBytes();
        if (byteString.isChunked()) {
            encodeTypeChunked(MajorType.BYTE_STRING);
            if (bytes != null) {
                encode(new ByteString(bytes));
            }
        } else if (bytes == null) {
            encoder.encode(SimpleValue.NULL);
        } else {
            encodeTypeAndLength(MajorType.BYTE_STRING, bytes.length);
            write(bytes);
        }
    }

}
