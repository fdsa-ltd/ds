package ltd.fdsa.switcher.core.cbor.decoder;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.Test;

import ltd.fdsa.switcher.core.cbor.CborDecoder;
import ltd.fdsa.switcher.core.cbor.CborEncoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.DataItem;
import ltd.fdsa.switcher.core.cbor.model.SimpleValue;
import ltd.fdsa.switcher.core.cbor.model.Special;

public class SimpleValueDecoderTest {

    @Test
    public void shouldDecodeBoolean() throws CborException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(byteArrayOutputStream);
        encoder.encode(SimpleValue.TRUE);
        encoder.encode(SimpleValue.FALSE);
        byte[] encodedBytes = byteArrayOutputStream.toByteArray();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(encodedBytes);
        List<DataItem> dataItems = new CborDecoder(byteArrayInputStream).decode();
        int result = 0;
        int position = 1;
        for (DataItem dataItem : dataItems) {
            position++;
            switch (dataItem.getMajorType()) {
            case SPECIAL:
                Special special = (Special) dataItem;
                switch (special.getSpecialType()) {
                case SIMPLE_VALUE:
                    SimpleValue simpleValue = (SimpleValue) special;
                    switch (simpleValue.getSimpleValueType()) {
                    case FALSE:
                        result += position * 2;
                        break;
                    case TRUE:
                        result += position * 3;
                        break;
                    default:
                        break;
                    }
                    break;
                default:
                    break;
                }
                break;
            default:
                break;
            }
        }
        assertEquals(12, result);
    }

}
