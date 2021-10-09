package ltd.fdsa.switcher.core.cbor.encoder;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Test;

import ltd.fdsa.switcher.core.cbor.CborBuilder;
import ltd.fdsa.switcher.core.cbor.CborEncoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.ByteString;
import ltd.fdsa.switcher.core.cbor.model.DataItem;

public class ByteStringEncoderTest {

    @Test
    public void shouldEncodeNullString() throws CborException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        List<DataItem> dataItems = new CborBuilder().add((ByteString) null).build();
        new CborEncoder(byteArrayOutputStream).encode(dataItems);
    }

    @Test
    public void shouldEncodeChunkedString() throws CborException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        List<DataItem> dataItems = new CborBuilder().add(new ByteString(null)).add(new ByteString("test".getBytes()))
            .startByteString("test".getBytes()).end().build();
        new CborEncoder(byteArrayOutputStream).encode(dataItems);
    }

}
