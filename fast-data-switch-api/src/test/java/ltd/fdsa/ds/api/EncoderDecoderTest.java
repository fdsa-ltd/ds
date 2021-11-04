package ltd.fdsa.ds.api;

import java.io.ByteArrayOutputStream;

import ltd.fdsa.ds.api.cbor.CborDecoder;
import ltd.fdsa.ds.api.cbor.CborEncoder;
import ltd.fdsa.ds.api.cbor.CborException;
import org.junit.Assert;
import org.junit.Test;

import ltd.fdsa.ds.api.cbor.model.DataItem;
import ltd.fdsa.ds.api.cbor.model.NegativeInteger;
import ltd.fdsa.ds.api.cbor.model.UnsignedInteger;

public class EncoderDecoderTest {

    @Test
    public void test() throws CborException {
        UnsignedInteger a = new UnsignedInteger(1);
        NegativeInteger x = new NegativeInteger(-2);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(byteArrayOutputStream);
        encoder.encode(a);
        encoder.encode(x);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        DataItem object = CborDecoder.decode(bytes).get(0);
        Assert.assertEquals(a, object);
    }

    @Test
    public void testTagging() throws CborException {
        UnsignedInteger a = new UnsignedInteger(1);
        NegativeInteger x = new NegativeInteger(-2);

        a.setTag(1);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(byteArrayOutputStream);
        encoder.encode(a);
        encoder.encode(x);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        DataItem object = CborDecoder.decode(bytes).get(0);
        Assert.assertEquals(a, object);
        Assert.assertTrue(object.hasTag());
        Assert.assertEquals(1L, object.getTag().getValue());
    }

}
