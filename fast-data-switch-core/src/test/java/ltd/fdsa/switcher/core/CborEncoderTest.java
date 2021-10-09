package ltd.fdsa.switcher.core;

import java.io.ByteArrayOutputStream;

import ltd.fdsa.switcher.core.cbor.CborEncoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import org.junit.Before;
import org.junit.Test;

import ltd.fdsa.switcher.core.cbor.model.DataItem;
import ltd.fdsa.switcher.core.cbor.model.MajorType;

public class CborEncoderTest {

    private class InvalidDataItem extends DataItem {

        public InvalidDataItem() {
            super(MajorType.INVALID);
        }

    }

    private CborEncoder encoder;

    @Before
    public void setup() {
        encoder = new CborEncoder(new ByteArrayOutputStream());
    }

    @Test(expected = CborException.class)
    public void shouldNotEncodeInvalidMajorType() throws CborException {
        encoder.encode(new InvalidDataItem());
    }

}
