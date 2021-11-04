package ltd.fdsa.ds.core.cbor.decoder;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import ltd.fdsa.ds.core.cbor.CborDecoder;
import ltd.fdsa.ds.core.cbor.CborException;
import ltd.fdsa.ds.core.cbor.model.ByteString;
import ltd.fdsa.ds.core.cbor.model.DataItem;
import ltd.fdsa.ds.core.cbor.model.MajorType;
import ltd.fdsa.ds.core.cbor.model.Special;

public class ByteStringDecoder extends AbstractDecoder<ByteString> {

    public ByteStringDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
    }

    @Override
    public ByteString decode(int initialByte) throws CborException {
        long length = getLength(initialByte);
        if (length == INFINITY) {
            if (decoder.isAutoDecodeInfinitiveByteStrings()) {
                return decodeInfinitiveLength();
            } else {
                ByteString byteString = new ByteString(null);
                byteString.setChunked(true);
                return byteString;
            }
        } else {
            return decodeFixedLength(length);
        }
    }

    private ByteString decodeInfinitiveLength() throws CborException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        for (;;) {
            DataItem dataItem = decoder.decodeNext();
            if (dataItem == null) {
                throw new CborException("Unexpected end of stream");
            }
            MajorType majorType = dataItem.getMajorType();
            if (Special.BREAK.equals(dataItem)) {
                break;
            } else if (majorType == MajorType.BYTE_STRING) {
                ByteString byteString = (ByteString) dataItem;
                byte[] byteArray = byteString.getBytes();
                if (byteArray != null) {
                    bytes.write(byteArray, 0, byteArray.length);
                }
            } else {
                throw new CborException("Unexpected major type " + majorType);
            }
        }
        return new ByteString(bytes.toByteArray());
    }

    private ByteString decodeFixedLength(long length) throws CborException {
        return new ByteString(decodeBytes(length));
    }

}
