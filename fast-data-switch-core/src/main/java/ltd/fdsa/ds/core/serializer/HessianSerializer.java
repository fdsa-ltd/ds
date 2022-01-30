package ltd.fdsa.ds.core.serializer;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianProtocolException;
import com.caucho.hessian.io.SerializerFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.core.job.model.HandleCallbackParam;

import java.io.*;
import java.util.Base64;

@Slf4j
public class HessianSerializer implements Serializer {

    private static SerializerFactory SERIALIZER_FACTORY = SerializerFactory.createDefault();


    public static String serialize(Object obj) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Hessian2Output output = new Hessian2Output(outputStream);
            output.writeObject(obj);
            output.close();
            var result = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            log.info("serialize:{}", result);
            return result;
        } catch (IOException e) {
            log.error("serialize failed:", e);
            return "";
        }
    }

    public static <T> T deserialize(String data, Class<T> clazz) {
        log.info("deserialize:{}", data);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data))) {
            Hessian2Input input = new Hessian2Input(new BufferedInputStream(inputStream));
            var result = (T) input.readObject();
            input.close();
            return result;
        } catch (IOException e) {
            log.error("deserialize failed:", e);
            return null;
        }
    }

    @Override
    public void write(DataOutput dataOutput, Object input) throws IOException {


        try {
            Hessian2Output output = new Hessian2Output();
            output.writeObject(input);
            output.close();
        } catch (IOException e) {
            log.error("serialize failed:", e);
        }
    }

    @Override
    public Object read(DataInput dataInput) throws IOException {

        try {
            Hessian2Input input = new Hessian2Input(new ByteArrayInputStream(new byte[0]));
            var result = input.readObject();
            input.close();
            return result;
        } catch (IOException e) {
            log.error("deserialize failed:", e);
            return null;
        }
    }

    @Override
    public int getWeight(Object instance) {
        return 0;
    }
}
