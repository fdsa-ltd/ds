package ltd.fdsa.switcher.core.examples;

import ltd.fdsa.switcher.core.cbor.model.AbstractHalfPrecisionFloatTest;

/**
 * 65504.0 -> 0xf97bff
 */
public class Example24Test extends AbstractHalfPrecisionFloatTest {

    public Example24Test() {
        super(65504.0f, new byte[] { (byte) 0xf9, 0x7b, (byte) 0xff });
    }

}
