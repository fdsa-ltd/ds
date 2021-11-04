package ltd.fdsa.ds.api.examples;

import ltd.fdsa.ds.api.cbor.model.AbstractStringTest;

/**
 * "a" -> 0x6161
 */
public class Example57Test extends AbstractStringTest {

    public Example57Test() {
        super("a", new byte[] { 0x61, 0x61 });
    }

}
