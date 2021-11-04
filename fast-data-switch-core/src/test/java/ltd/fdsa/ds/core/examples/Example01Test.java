package ltd.fdsa.ds.core.examples;

import ltd.fdsa.ds.core.cbor.model.AbstractNumberTest;

/**
 * 0 -> 0x00
 */
public class Example01Test extends AbstractNumberTest {

    public Example01Test() {
        super(0, new byte[] { 0x00 });
    }

}
