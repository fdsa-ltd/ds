package ltd.fdsa.ds.core.examples;

import ltd.fdsa.ds.core.cbor.model.AbstractStringTest;

/**
 * "\"\\" -> 0x62225c
 */
public class Example59Test extends AbstractStringTest {

    public Example59Test() {
        super("\"\\", new byte[] { 0x62, 0x22, 0x5c });
    }

}
