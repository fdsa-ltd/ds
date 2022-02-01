package ltd.fdsa.ds.core.store;

import lombok.var;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>
 *
 * @author zhumingwu
 * @since 2022/1/30 13:23
 */
public class RingBuffer {
    private final DataMessage[] buffer;
    private final int size;
    private final AtomicLong counter;

    public RingBuffer(long initialValue, int size) {
        if (size > Integer.MAX_VALUE) {
            size = Integer.MAX_VALUE;
        }

        this.counter = new AtomicLong(initialValue);
        this.size = size;
        this.buffer = new DataMessage[size];
    }

    public long getOffset() {
        return this.counter.get();
    }

    public int getSize() {
        return this.size;
    }

    public DataMessage pull(long offset) {
        if (offset > this.size) {
            offset %= this.size;
        }
        return this.buffer[(int) offset];
    }

    public long push(DataMessage data) {
        var offset = this.counter.incrementAndGet();
        if (offset > this.size) {
            offset %= this.size;
        }
        this.buffer[(int) offset] = data;
        return this.counter.get();
    }
}

