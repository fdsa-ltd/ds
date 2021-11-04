package ltd.fdsa.ds.core.examples;

import ltd.fdsa.ds.core.cbor.model.AbstractNumberTest;

/**
 * 10 -> 0x0a
 */
public class Example03Test extends AbstractNumberTest {

    public Example03Test() {
        super(10, new byte[] { 0x0a });
    }

}
