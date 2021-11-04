package ltd.fdsa.ds.api.cbor.encoder;

import java.io.OutputStream;

import ltd.fdsa.ds.api.cbor.CborEncoder;
import ltd.fdsa.ds.api.cbor.CborException;
import ltd.fdsa.ds.api.cbor.model.MajorType;
import ltd.fdsa.ds.api.cbor.model.Tag;

public class TagEncoder extends AbstractEncoder<Tag> {

    public TagEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
    }

    @Override
    public void encode(Tag tag) throws CborException {
        encodeTypeAndLength(MajorType.TAG, tag.getValue());
    }

}
