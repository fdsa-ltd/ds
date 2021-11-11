package ltd.fdsa.ds.api;

import com.google.iot.cbor.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @ClassName:
 * @description:
 * @since 2020-10-28
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {String.class})
@Slf4j
public class TestDataPipeline {


    @Test
    public void GoogleCBORTest() throws CborParseException {
        byte[] cborBytes = new byte[]{(byte) 0xd9, (byte) 0xd9, (byte) 0xf7,
                (byte) 0xa2, 0x61, 0x61, 0x01, 0x61,
                0x62, (byte) 0x82, 0x02, 0x03};
        CborMap map = CborMap.createFromCborByteArray(cborBytes);

// Prints out the line `toString: 55799({"a":1,"b":[2,3]})`
        System.out.println("toString: " + map.toString());

// Prints out the line `toJsonString: {"a":1,"b":[2,3]}`
        System.out.println("toJsonString: " + map.toJsonString());

        CborArray cborArray = (CborArray) map.get("b");
        System.out.println(map.get("a"));
        float sum = 0;

// Prints out `b: 2` and `b: 3`
        for (CborObject obj : cborArray) {
            System.out.println("b: " + obj);

            if (obj instanceof CborNumber) {
                sum += ((CborNumber) obj).floatValue();
            }
        }

// Prints out `Sum: 5.0`
        System.out.println("Sum: " + sum);
    }

    @Test
    public void GoogleCBORCreateTest() throws CborParseException, CborConversionException {
        CborMap map = CborMap.create();
        map.put("name", CborTextString.create("zhumingwu"));
        map.put("age", CborInteger.create(18));
        map.put("types", CborArray.createFromJavaObject(new String[]{"男", "富", "帅"}));
        System.out.println("toString: " + map.toString());
        System.out.println("toJsonString: " + map.toJsonString());
        System.out.println("toCborByteArray: " + map.toCborByteArray());
    }

    @Test
    public void test() {
        byte b = -126;
        if (b>=0)
        {

        }
        System.out.println(Integer.toBinaryString((b & 0xff)));
        System.out.println(Integer.toBinaryString((b & 0xff) >> 7));

//
//        ByteBuffer buffer = ByteBuffer.allocate(4);
//
//        int  value =  -2;
//
//        buffer.putInt(value);
//
//
//        for (var b : buffer.array()) {
//            System.out.println(Integer.toBinaryString(b& 0xff));
//        }


    }
}
