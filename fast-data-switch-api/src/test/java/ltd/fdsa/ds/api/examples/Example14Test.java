package ltd.fdsa.ds.api.examples;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import ltd.fdsa.ds.api.cbor.CborDecoder;
import ltd.fdsa.ds.api.cbor.CborEncoder;
import ltd.fdsa.ds.api.cbor.CborException;
import ltd.fdsa.ds.api.cbor.model.ByteString;
import ltd.fdsa.ds.api.cbor.model.DataItem;
import ltd.fdsa.ds.api.cbor.model.NegativeInteger;
import ltd.fdsa.ds.api.cbor.model.Tag;

/**
 * -18446744073709551617 -> 0xc349010000000000000000
 */
public class Example14Test {

    @Test
    public void shouldEncode() throws CborException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(byteArrayOutputStream);
        encoder.encode(new NegativeInteger(new BigInteger("-18446744073709551617")));
        Assert.assertArrayEquals(new byte[] { (byte) 0xc3, 0x49, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
            byteArrayOutputStream.toByteArray());
    }

    @Test
    public void shouldDecode() throws CborException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
            new byte[] { (byte) 0xc3, 0x49, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 });
        CborDecoder decoder = new CborDecoder(byteArrayInputStream);
        DataItem b = decoder.decodeNext();

        Assert.assertTrue(b.hasTag());
        Tag tag = b.getTag();
        Assert.assertEquals(3, tag.getValue());

        Assert.assertTrue(b instanceof ByteString);
        ByteString byteString = (ByteString) b;
        Assert.assertArrayEquals(new byte[] { (byte) 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
            byteString.getBytes());
    }

}
