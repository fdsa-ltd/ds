package ltd.fdsa.ds.api.examples;

import ltd.fdsa.ds.api.cbor.model.AbstractNumberTest;

/**
 * 100 -> 0x1864
 */
public class Example07Test extends AbstractNumberTest {

    public Example07Test() {
        super(100, new byte[] { 0x18, 0x64 });
    }

}
