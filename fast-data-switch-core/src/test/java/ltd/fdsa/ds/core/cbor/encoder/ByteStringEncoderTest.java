package ltd.fdsa.ds.core.cbor.encoder;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Test;

import ltd.fdsa.ds.core.cbor.CborBuilder;
import ltd.fdsa.ds.core.cbor.CborEncoder;
import ltd.fdsa.ds.core.cbor.CborException;
import ltd.fdsa.ds.core.cbor.model.ByteString;
import ltd.fdsa.ds.core.cbor.model.DataItem;

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
