package ltd.fdsa.ds.core.cbor.encoder;

import java.io.OutputStream;

import ltd.fdsa.ds.core.cbor.CborEncoder;
import ltd.fdsa.ds.core.cbor.CborException;
import ltd.fdsa.ds.core.cbor.model.DoublePrecisionFloat;
import ltd.fdsa.ds.core.cbor.model.HalfPrecisionFloat;
import ltd.fdsa.ds.core.cbor.model.SimpleValue;
import ltd.fdsa.ds.core.cbor.model.SimpleValueType;
import ltd.fdsa.ds.core.cbor.model.SinglePrecisionFloat;
import ltd.fdsa.ds.core.cbor.model.Special;

public class SpecialEncoder extends AbstractEncoder<Special> {

    private final HalfPrecisionFloatEncoder halfPrecisionFloatEncoder;
    private final SinglePrecisionFloatEncoder singlePrecisionFloatEncoder;
    private final DoublePrecisionFloatEncoder doublePrecisionFloatEncoder;

    public SpecialEncoder(CborEncoder encoder, OutputStream outputStream) {
        super(encoder, outputStream);
        halfPrecisionFloatEncoder = new HalfPrecisionFloatEncoder(encoder, outputStream);
        singlePrecisionFloatEncoder = new SinglePrecisionFloatEncoder(encoder, outputStream);
        doublePrecisionFloatEncoder = new DoublePrecisionFloatEncoder(encoder, outputStream);
    }

    @Override
    public void encode(Special dataItem) throws CborException {
        switch (dataItem.getSpecialType()) {
        case BREAK:
            write((7 << 5) | 31);
            break;
        case SIMPLE_VALUE:
            SimpleValue simpleValue = (SimpleValue) dataItem;
            switch (simpleValue.getSimpleValueType()) {
            case FALSE:
            case NULL:
            case TRUE:
            case UNDEFINED:
                SimpleValueType type = simpleValue.getSimpleValueType();
                write((7 << 5) | type.getValue());
                break;
            case UNALLOCATED:
                write((7 << 5) | simpleValue.getValue());
                break;
            case RESERVED:
                break;
            }
            break;
        case UNALLOCATED:
            throw new CborException("Unallocated special type");
        case IEEE_754_HALF_PRECISION_FLOAT:
            if (!(dataItem instanceof HalfPrecisionFloat)) {
                throw new CborException("Wrong data item type");
            }
            halfPrecisionFloatEncoder.encode((HalfPrecisionFloat) dataItem);
            break;
        case IEEE_754_SINGLE_PRECISION_FLOAT:
            if (!(dataItem instanceof SinglePrecisionFloat)) {
                throw new CborException("Wrong data item type");
            }
            singlePrecisionFloatEncoder.encode((SinglePrecisionFloat) dataItem);
            break;
        case IEEE_754_DOUBLE_PRECISION_FLOAT:
            if (!(dataItem instanceof DoublePrecisionFloat)) {
                throw new CborException("Wrong data item type");
            }
            doublePrecisionFloatEncoder.encode((DoublePrecisionFloat) dataItem);
            break;
        case SIMPLE_VALUE_NEXT_BYTE:
            if (!(dataItem instanceof SimpleValue)) {
                throw new CborException("Wrong data item type");
            }
            SimpleValue simpleValueNextByte = (SimpleValue) dataItem;
            write((byte) ((7 << 5) | 24), (byte) simpleValueNextByte.getValue());
            break;
        }
    }

}
