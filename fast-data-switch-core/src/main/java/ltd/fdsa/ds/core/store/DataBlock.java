package ltd.fdsa.ds.core.store;

/**
 * represents the message data
 * <p>
 *
 * @author zhumingwu
 * @since 2022/1/27 17:41
 */
public class DataBlock {

    final long timestamp;
    final byte[] payload;

    public DataBlock(long timestamp, byte[] payload) {
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public DataBlock(byte[] payload) {
        this.timestamp = System.currentTimeMillis();
        this.payload = payload;
    }
}

