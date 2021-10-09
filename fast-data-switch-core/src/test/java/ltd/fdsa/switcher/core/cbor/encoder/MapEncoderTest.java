package ltd.fdsa.switcher.core.cbor.encoder;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Test;

import ltd.fdsa.switcher.core.cbor.CborBuilder;
import ltd.fdsa.switcher.core.cbor.CborEncoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.DataItem;

public class MapEncoderTest {

    @Test
    public void shouldEncodeMap() throws CborException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        List<DataItem> dataItems = new CborBuilder().addMap().put(1, true).put(".", true).put(3, true).put("..", true)
            .put(2, true).put("...", true).end().build();
        new CborEncoder(byteArrayOutputStream).encode(dataItems);
    }

}
