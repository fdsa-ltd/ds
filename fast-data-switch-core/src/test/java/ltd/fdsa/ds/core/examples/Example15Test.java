package ltd.fdsa.ds.core.examples;

import ltd.fdsa.ds.core.cbor.model.AbstractNumberTest;

/**
 * -1 -> 0x20
 */
public class Example15Test extends AbstractNumberTest {

    public Example15Test() {
        super(-1, new byte[] { 0x20 });
    }

}
