package ltd.fdsa.ds.api.cbor.encoder;

import java.io.OutputStream;

import ltd.fdsa.ds.api.cbor.CborEncoder;
import ltd.fdsa.ds.api.cbor.CborException;
import ltd.fdsa.ds.api.cbor.model.DoublePrecisionFloat;

public class DoublePrecisionFloatEncoder extends AbstractEncoder<DoublePrecisionFloat> {

    public DoublePrecisionFloatEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
    }

    @Override
    public void encode(DoublePrecisionFloat dataItem) throws CborException {
        long bits = Double.doubleToRawLongBits(dataItem.getValue());
        write((byte) ((7 << 5) | 27), (byte) ((bits >> 56) & 0xFF), (byte) ((bits >> 48) & 0xFF),
            (byte) ((bits >> 40) & 0xFF), (byte) ((bits >> 32) & 0xFF), (byte) ((bits >> 24) & 0xFF),
            (byte) ((bits >> 16) & 0xFF), (byte) ((bits >> 8) & 0xFF), (byte) ((bits >> 0) & 0xFF));
    }

}
