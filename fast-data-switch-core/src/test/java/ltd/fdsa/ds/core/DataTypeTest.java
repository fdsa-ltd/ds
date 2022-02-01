package ltd.fdsa.ds.core;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.Test;

@Slf4j
public class DataTypeTest {

    @Test
    public void TestData() {
        Double d = 3.15D;
        var l = Double.doubleToLongBits(d);
        System.out.println(l);
    }

    @Test
    public void TestBoolData() {
        Float f = 3.14f;

        System.out.println(f.toString());
        var i = Float.floatToIntBits(f);
        System.out.println(i);

    }

}
