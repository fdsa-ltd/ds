package ltd.fdsa.ds.core.cbor.encoder;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Test;

import ltd.fdsa.ds.core.cbor.CborBuilder;
import ltd.fdsa.ds.core.cbor.CborEncoder;
import ltd.fdsa.ds.core.cbor.CborException;
import ltd.fdsa.ds.core.cbor.model.DataItem;

public class MapEncoderTest {

    @Test
    public void shouldEncodeMap() throws CborException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        List<DataItem> dataItems = new CborBuilder().addMap().put(1, true).put(".", true).put(3, true).put("..", true)
            .put(2, true).put("...", true).end().build();
        new CborEncoder(byteArrayOutputStream).encode(dataItems);
    }

}
