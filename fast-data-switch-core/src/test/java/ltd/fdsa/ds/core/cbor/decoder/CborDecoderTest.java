package ltd.fdsa.ds.core.cbor.decoder;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import ltd.fdsa.ds.core.cbor.CborBuilder;
import ltd.fdsa.ds.core.cbor.CborDecoder;
import ltd.fdsa.ds.core.cbor.CborEncoder;
import ltd.fdsa.ds.core.cbor.CborException;
import ltd.fdsa.ds.core.cbor.encoder.AbstractEncoder;
import ltd.fdsa.ds.core.cbor.model.DataItem;
import ltd.fdsa.ds.core.cbor.model.MajorType;
import ltd.fdsa.ds.core.cbor.model.RationalNumber;
import ltd.fdsa.ds.core.cbor.model.Tag;
import ltd.fdsa.ds.core.cbor.model.UnsignedInteger;

public class CborDecoderTest {

    @Test(expected = CborException.class)
    public void shouldThrowCborException() throws CborException {
        CborDecoder cborDecoder = new CborDecoder(new InputStream() {

            @Override
            public int read() throws IOException {
                throw new IOException();
            }

        });
        cborDecoder.decodeNext();
    }

    @Test(expected = CborException.class)
    public void shouldThrowCborException2() throws CborException {
        CborDecoder cborDecoder = new CborDecoder(new InputStream() {

            @Override
            public int read() throws IOException {
                return (8 << 5); // invalid major type
            }

        });
        cborDecoder.decodeNext();
    }

    @Test
    public void shouldSetAutoDecodeInfinitiveMaps() {
        InputStream inputStream = new ByteArrayInputStream(new byte[] { 0, 1, 2 });
        CborDecoder cborDecoder = new CborDecoder(inputStream);
        assertTrue(cborDecoder.isAutoDecodeInfinitiveMaps());
        cborDecoder.setAutoDecodeInfinitiveMaps(false);
        assertFalse(cborDecoder.isAutoDecodeInfinitiveMaps());
    }

    @Test
    public void shouldSetAutoDecodeRationalNumbers() {
        InputStream inputStream = new ByteArrayInputStream(new byte[] { 0, 1, 2 });
        CborDecoder cborDecoder = new CborDecoder(inputStream);
        assertTrue(cborDecoder.isAutoDecodeRationalNumbers());
        cborDecoder.setAutoDecodeRationalNumbers(false);
        assertFalse(cborDecoder.isAutoDecodeRationalNumbers());
    }

    @Test
    public void shouldSetAutoDecodeLanguageTaggedStrings() {
        InputStream inputStream = new ByteArrayInputStream(new byte[] { 0, 1, 2 });
        CborDecoder cborDecoder = new CborDecoder(inputStream);
        assertTrue(cborDecoder.isAutoDecodeLanguageTaggedStrings());
        cborDecoder.setAutoDecodeLanguageTaggedStrings(false);
        assertFalse(cborDecoder.isAutoDecodeLanguageTaggedStrings());
    }

    @Test(expected = CborException.class)
    public void shouldThrowOnRationalNumberDecode1() throws CborException {
        List<DataItem> items = new CborBuilder().addTag(30).add(true).build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(baos);
        encoder.encode(items);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        CborDecoder decoder = new CborDecoder(bais);
        decoder.decode();
    }

    @Test(expected = CborException.class)
    public void shouldThrowOnRationalNumberDecode2() throws CborException {
        List<DataItem> items = new CborBuilder().addTag(30).addArray().add(true).end().build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(baos);
        encoder.encode(items);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        CborDecoder decoder = new CborDecoder(bais);
        decoder.decode();
    }

    @Test(expected = CborException.class)
    public void shouldThrowOnRationalNumberDecode3() throws CborException {
        List<DataItem> items = new CborBuilder().addTag(30).addArray().add(true).add(true).end().build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(baos);
        encoder.encode(items);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        CborDecoder decoder = new CborDecoder(bais);
        decoder.decode();
    }

    @Test(expected = CborException.class)
    public void shouldThrowOnRationalNumberDecode4() throws CborException {
        List<DataItem> items = new CborBuilder().addTag(30).addArray().add(1).add(true).end().build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(baos);
        encoder.encode(items);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        CborDecoder decoder = new CborDecoder(bais);
        decoder.decode();
    }

    @Test
    public void shouldDecodeRationalNumber() throws CborException {
        List<DataItem> items = new CborBuilder().addTag(30).addArray().add(1).add(2).end().build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(baos);
        encoder.encode(items);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        CborDecoder decoder = new CborDecoder(bais);
        assertEquals(new RationalNumber(new UnsignedInteger(1), new UnsignedInteger(2)), decoder.decodeNext());
    }

    @Test
    public void shouldDecodeTaggedTags() throws CborException {
        DataItem decoded = CborDecoder.decode(new byte[] { (byte) 0xC1, (byte) 0xC2, 0x02 }).get(0);

        Tag outer = new Tag(1);
        Tag inner = new Tag(2);
        UnsignedInteger expected = new UnsignedInteger(2);
        inner.setTag(outer);
        expected.setTag(inner);

        assertEquals(expected, decoded);
    }

    @Test
    public void shouldDecodeTaggedRationalNumber() throws CborException {
        List<DataItem> items = new CborBuilder().addTag(1).addTag(30).addArray().add(1).add(2).end().build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CborEncoder encoder = new CborEncoder(baos);
        encoder.encode(items);
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        CborDecoder decoder = new CborDecoder(bais);

        RationalNumber expected = new RationalNumber(new UnsignedInteger(1), new UnsignedInteger(2));
        expected.getTag().setTag(new Tag(1));
        assertEquals(expected, decoder.decodeNext());
    }

    @Test
    public void shouldThrowOnItemWithForgedLength() throws CborException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        AbstractEncoder<Long> maliciousEncoder = new AbstractEncoder<Long>(null, buffer) {
            @Override
            public void encode(Long length) throws CborException {
                encodeTypeAndLength(MajorType.UNICODE_STRING, length.longValue());
            }
        };
        maliciousEncoder.encode(Long.valueOf(Integer.MAX_VALUE + 1L));
        byte[] maliciousString = buffer.toByteArray();
        try {
            CborDecoder.decode(maliciousString);
            fail("Should have failed the huge allocation");
        } catch (CborException e) {
            assertThat("Exception message", e.getMessage(), containsString("limited to INTMAX"));
        }

        buffer.reset();
        maliciousEncoder.encode(Long.valueOf(Integer.MAX_VALUE - 1));
        maliciousString = buffer.toByteArray();
        try {
            CborDecoder.decode(maliciousString);
            fail("Should have failed the huge allocation");
        } catch (OutOfMemoryError e) {
            // Expected without limit
        }
        CborDecoder decoder = new CborDecoder(new ByteArrayInputStream(maliciousString));
        decoder.setMaxPreallocationSize(1024);
        try {
            decoder.decode();
            fail("Should have failed with unexpected end of stream exception");
        } catch (CborException e) {
            // Expected with limit
        }
    }
}