package ltd.fdsa.ds.api.examples;

import ltd.fdsa.ds.api.cbor.model.AbstractStringTest;

/**
 * "" -> 0x60
 */
public class Example56Test extends AbstractStringTest {

    public Example56Test() {
        super("", new byte[] { 0x60 });
    }

}
