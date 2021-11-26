package ltd.fdsa.ds.api.bsdor;

import lombok.var;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;


public class DataParse {

    private final ByteArrayInputStream input;

    public DataParse(ByteArrayInputStream input) {
        this.input = input;
    }

    private long make(int b3, int b2, int b1, int b0) {
        return (((b3 & 0xff) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) << 8) |
                ((b0 & 0xff)));
    }

    private int make(int b1, int b0) {
        return (((b1 & 0xff) << 8) |
                ((b0 & 0xff)));
    }

    public Item Parse() {
        var type = Item.Type.valueOf(input.read());
        var mainType = type.getMainType();        //高2位
        Integer subType = type.getSubType(); //低6位

        switch (mainType) {
            case 0: // 空与正整数类型
                if (subType == 0) {
                    return new NullItem();
                }
                // 1 - 59
                if (subType <= 59) {
                    return new Number8Item(subType);
                }
                // 60,61,62,63
                switch (subType) {
                    case 60:
                        return new Number8Item(input.read());
                    case 61:
                        return new Number16Item(this.make(input.read(), input.read()));
                    case 62:
                        return new Number32Item(this.make(input.read(), input.read(), input.read(), input.read()));
                    case 63:
                        byte[] b = new byte[8];
                        try {
                            input.read(b);
                            return new Number64Item(b);
                        } catch (IOException e) {
                            return new NullItem();
                        }

                }
            case 1: // 字符串类型
                return this.getString(subType);
            case 3: // 零与负整数类型
                if (subType == 0) {
                    return new Number8Item(0);
                }
                if (subType <= 59) {
                    return new Number8Item(-subType);
                }

                // 60,61,62,63
                switch (subType) {
                    case 60:
                        return new Number8Item(-input.read());
                    case 61:
                        return new Number16Item(-this.make(input.read(), input.read()));
                    case 62:
                        return new Number32Item(-this.make(input.read(), input.read(), input.read(), input.read()));
                    case 63:

                        try {
                            byte[] b = new byte[8];
                            input.read(b);
                            var data = new BigInteger(b);
                            return new Number64Item(data.negate());
                        } catch (IOException e) {
                            return new NullItem();
                        }

                }

            default: // 扩展数据类型
                if (subType >= 15) {
                    return new Number8Item(subType + 45);
                }
                switch (subType) {
                    case 0:
                        return new BoolItem(false);
                    case 1:
                        return new BoolItem(true);
                    case 2: //float

                        try {
                            byte[] buffer = new byte[4];
                            input.read(buffer);
                            BigInteger data = new BigInteger(buffer);
                            return new FloatItem(data.floatValue());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    case 3: //double
                        try {
                            byte[] buffer = new byte[8];
                            input.read(buffer);
                            BigInteger data = new BigInteger(buffer);
                            return new DoubleItem(data.doubleValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    case 4: //timestamp

                        try {
                            byte[] buffer = new byte[8];
                            input.read(buffer);
                            BigInteger data = new BigInteger(buffer);
                            return new DateTimeItem(data.longValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    case 5: //DECIMAL,
                        try {
                            Long length = this.make(input.read(), input.read(), input.read(), input.read()) * 4;
                            Long scale = this.make(input.read(), input.read(), input.read(), input.read());
                            byte[] buffer = new byte[length.intValue()];
                            input.read(buffer);
                            BigInteger data = new BigInteger(buffer);
                            return new DecimalItem(new BigDecimal(data, scale.intValue()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    case 6: //BYTES,
                    case 7: //REF
                    default:   // RESERVE1, RESERVE2,RESERVE3,RESERVE4,RESERVE5,RESERVE6,RESERVE7,
                        return new NullItem();

                }
        }
    }

    private StringItem getString(int type) {
        var length = type;
        if (length == 63) {

            while (true) {
                var size = input.read();
                length += size;
                if (size < 255) {
                    break;
                }
            }
        }

        try {
            byte[] buffer = new byte[length];
            input.read(buffer);
            return new StringItem(new String(buffer, StandardCharsets.UTF_8));
        } catch (IOException e) {
            return new StringItem("");
        }

    }
}
