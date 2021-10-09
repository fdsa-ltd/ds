package ltd.fdsa.switcher.core.cbor.encoder;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import ltd.fdsa.switcher.core.cbor.CborEncoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.MajorType;
import ltd.fdsa.switcher.core.cbor.model.SimpleValue;
import ltd.fdsa.switcher.core.cbor.model.UnicodeString;

public class UnicodeStringEncoder extends AbstractEncoder<UnicodeString> {

    public UnicodeStringEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
    }

    @Override
    public void encode(UnicodeString dataItem) throws CborException {
        String string = dataItem.getString();
        if (dataItem.isChunked()) {
            encodeTypeChunked(MajorType.UNICODE_STRING);
            if (string != null) {
                encode(new UnicodeString(string));
            }
        } else if (string == null) {
            encoder.encode(SimpleValue.NULL);
        } else {
            byte[] bytes;
            bytes = string.getBytes(StandardCharsets.UTF_8);
            encodeTypeAndLength(MajorType.UNICODE_STRING, bytes.length);
            write(bytes);
        }
    }

}
