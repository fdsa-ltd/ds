package ltd.fdsa.ds.api.cbor.decoder;

import org.junit.Test;

import ltd.fdsa.ds.api.cbor.CborDecoder;
import ltd.fdsa.ds.api.cbor.CborException;

public class ArrayDecoderTest {
    @Test(expected = CborException.class)
    public void shouldThrowOnIncompleteArray() throws CborException {
        byte[] bytes = new byte[] { (byte) 0x82, 0x01 };
        CborDecoder.decode(bytes);
    }

    @Test(expected = CborException.class)
    public void shouldThrowInIncompleteIndefiniteLengthArray() throws CborException {
        byte[] bytes = new byte[] { (byte) 0x9f, 0x01, 0x02 };
        CborDecoder.decode(bytes);
    }
}
