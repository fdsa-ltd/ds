package ltd.fdsa.ds.core.cbor.decoder;

import org.junit.Test;

import ltd.fdsa.ds.core.cbor.CborException;

public class SpecialDecoderTest {

    @Test(expected = CborException.class)
    public void shouldThrowExceptionOnUnallocated() throws CborException {
        SpecialDecoder decoder = new SpecialDecoder(null, null);
        decoder.decode(28);
    }

}
