package ltd.fdsa.ds.core;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
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
public class BsdorTypeTest {

    @Test
    public void TestData() {
        byte i = -127;



        var ss = Math.pow(2, 64);
        System.out.println(ss);
        var type = 0B11101111;
        System.out.println(type);

        var mainType = type >> 6;
        System.out.println(mainType);

        var subType = type & 0B00111111;
        System.out.println(subType);
    }
}
