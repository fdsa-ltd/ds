package ltd.fdsa.switcher.core.cbor.decoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Test;

import ltd.fdsa.switcher.core.cbor.CborBuilder;
import ltd.fdsa.switcher.core.cbor.CborDecoder;
import ltd.fdsa.switcher.core.cbor.CborEncoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.DataItem;

public class UnicodeStringDecoderTest {

    @Test
    public void shouldDecodeChunkedUnicodeString() throws CborException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(baos);
        encoder.encode(new CborBuilder().startString().add("foo").add("bar").end().build());
        byte[] encodedBytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(encodedBytes);
        CborDecoder decoder = new CborDecoder(bais);
        List<DataItem> dataItems = decoder.decode();
        assertNotNull(dataItems);
        assertEquals(1, dataItems.size());
    }

    @Test(expected = CborException.class)
    public void shouldThrowOnIncompleteString() throws CborException {
        byte[] bytes = new byte[] { 0x62, 0x61 };
        CborDecoder.decode(bytes);
    }

    @Test(expected = CborException.class)
    public void shouldThrowOnMissingBreak() throws CborException {
        byte[] bytes = new byte[] { 0x7f, 0x61, 0x61 };
        CborDecoder.decode(bytes);
    }

}
