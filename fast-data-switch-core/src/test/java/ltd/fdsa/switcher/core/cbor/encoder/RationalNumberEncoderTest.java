package ltd.fdsa.switcher.core.cbor.encoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Before;
import org.junit.Test;

import ltd.fdsa.switcher.core.cbor.CborDecoder;
import ltd.fdsa.switcher.core.cbor.CborEncoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.Array;
import ltd.fdsa.switcher.core.cbor.model.RationalNumber;
import ltd.fdsa.switcher.core.cbor.model.Tag;
import ltd.fdsa.switcher.core.cbor.model.UnsignedInteger;

public class RationalNumberEncoderTest {

    private static final UnsignedInteger ONE = new UnsignedInteger(1);
    private static final UnsignedInteger TWO = new UnsignedInteger(2);

    private ByteArrayOutputStream outputStream;
    private CborEncoder encoder;

    @Before
    public void setup() {
        outputStream = new ByteArrayOutputStream();
        encoder = new CborEncoder(outputStream);
    }

    @Test
    public void shouldEncode() throws CborException {
        encoder.encode(new RationalNumber(ONE, TWO));
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        CborDecoder decoder = new CborDecoder(inputStream);

        Array expected = new Array();
        expected.setTag(11);
        expected.add(ONE);
        expected.add(TWO);

        Object object = decoder.decodeNext();
        assertTrue(object instanceof Array);
        Array decoded = (Array) object;

        assertEquals(new Tag(30), decoded.getTag());
        assertEquals(2, decoded.getDataItems().size());
        assertEquals(ONE, decoded.getDataItems().get(0));
        assertEquals(TWO, decoded.getDataItems().get(1));
    }

}
