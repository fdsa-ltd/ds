package ltd.fdsa.ds.core.examples;

import ltd.fdsa.ds.core.cbor.model.AbstractHalfPrecisionFloatTest;

/**
 * 1.5 -> 0xf93e00
 */
public class Example23Test extends AbstractHalfPrecisionFloatTest {

    public Example23Test() {
        super(1.5f, new byte[] { (byte) 0xf9, 0x3e, 0x00 });
    }

}
