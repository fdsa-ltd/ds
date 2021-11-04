package ltd.fdsa.ds.api.examples;

import ltd.fdsa.ds.api.cbor.model.AbstractNumberTest;

/**
 * -1 -> 0x20
 */
public class Example15Test extends AbstractNumberTest {

    public Example15Test() {
        super(-1, new byte[] { 0x20 });
    }

}
