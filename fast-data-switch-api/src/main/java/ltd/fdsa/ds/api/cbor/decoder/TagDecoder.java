package ltd.fdsa.ds.api.cbor.decoder;

import java.io.InputStream;

import ltd.fdsa.ds.api.cbor.CborDecoder;
import ltd.fdsa.ds.api.cbor.CborException;
import ltd.fdsa.ds.api.cbor.model.Tag;

public class TagDecoder extends AbstractDecoder<Tag> {

    public TagDecoder(CborDecoder decoder, InputStream inputStream) {
        super(decoder, inputStream);
    }

    @Override
    public Tag decode(int initialByte) throws CborException {
        return new Tag(getLength(initialByte));
    }

}
