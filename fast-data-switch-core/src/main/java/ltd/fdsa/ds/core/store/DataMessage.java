package ltd.fdsa.ds.core.store;

/**
 * represents the message data
 * <p>
 *
 * @author zhumingwu
 * @since 2022/1/27 17:41
 */
public class DataMessage {

    final long timestamp;
    final byte[] payload;

    public DataMessage(long timestamp, byte[] payload) {
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public DataMessage(byte[] payload) {
        this.timestamp = System.currentTimeMillis();
        this.payload = payload;
    }
}

