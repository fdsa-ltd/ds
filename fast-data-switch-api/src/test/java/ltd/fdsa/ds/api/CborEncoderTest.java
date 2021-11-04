package ltd.fdsa.ds.api;

import java.io.ByteArrayOutputStream;

import ltd.fdsa.ds.api.cbor.CborEncoder;
import ltd.fdsa.ds.api.cbor.CborException;
import org.junit.Before;
import org.junit.Test;

import ltd.fdsa.ds.api.cbor.model.DataItem;
import ltd.fdsa.ds.api.cbor.model.MajorType;

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
