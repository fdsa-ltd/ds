package ltd.fdsa.switcher.core.cbor.encoder;

import java.io.OutputStream;

import ltd.fdsa.switcher.core.cbor.CborEncoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.ByteString;
import ltd.fdsa.switcher.core.cbor.model.MajorType;
import ltd.fdsa.switcher.core.cbor.model.SimpleValue;

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
