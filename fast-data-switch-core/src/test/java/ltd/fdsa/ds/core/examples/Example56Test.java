package ltd.fdsa.ds.core.examples;

import ltd.fdsa.ds.core.cbor.model.AbstractStringTest;

/**
 * "" -> 0x60
 */
public class Example56Test extends AbstractStringTest {

    public Example56Test() {
        super("", new byte[] { 0x60 });
    }

}
