package ltd.fdsa.switcher.core.cbor.decoder;

import java.io.InputStream;

import ltd.fdsa.switcher.core.cbor.CborDecoder;
import ltd.fdsa.switcher.core.cbor.CborException;
import ltd.fdsa.switcher.core.cbor.model.Tag;

public class TagDecoder extends AbstractDecoder<Tag> {

    public TagDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
    }

    @Override
    public Tag decode(int initialByte) throws CborException {
        return new Tag(getLength(initialByte));
    }

}
