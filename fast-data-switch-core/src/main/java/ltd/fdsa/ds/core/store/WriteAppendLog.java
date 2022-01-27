package ltd.fdsa.ds.core.store;

import lombok.var;
import ltd.fdsa.ds.core.util.CRCUtil;
import ltd.fdsa.ds.core.util.FileChannelUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Fast to write message data into wal file
 * <p>
 * crc - 4 byte
 * timestamp - 8 byte
 * payload - var byte
 *
 * @author zhumingwu
 * @since 2022/1/27 17:40
 */
public class WriteAppendLog {
    private static short WRITE_APPEND_LOG_SIZE = 4;
    private static WriteAppendLog[] WRITE_APPEND_LOGS = new WriteAppendLog[WRITE_APPEND_LOG_SIZE];
    private static AtomicInteger CURRENT_WAL = new AtomicInteger(0);

    static {
        for (int i = 0; i < WRITE_APPEND_LOG_SIZE; i++) {
            WRITE_APPEND_LOGS[i] = newInstance(MemTable.WAL_SIZE);
        }
    }

    private final AtomicInteger counter = new AtomicInteger(0);
    private final FileChannel fileChannel;
    private final int size;

    public WriteAppendLog(FileChannel fileChannel, int size) {
        this.fileChannel = fileChannel;
        this.size = size;
    }

    boolean tooBig() {
        return this.counter.get() > size;
    }

    public static WriteAppendLog getWriteAppendLogs(TopicIndex topicIndex) {
        var writeAppendLog = WRITE_APPEND_LOGS[CURRENT_WAL.get() % WRITE_APPEND_LOG_SIZE];
        if (writeAppendLog.tooBig()) {
            writeAppendLog.mergeTo(null);
            return WRITE_APPEND_LOGS[CURRENT_WAL.incrementAndGet() % WRITE_APPEND_LOG_SIZE];
        }
        return writeAppendLog;
    }

    static WriteAppendLog newInstance(int size) {
        var path = Paths.get("./", "data", "wal", System.currentTimeMillis() + ".wal");
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path.getParent());
            }
            var fileChannel = FileChannel.open(path, StandardOpenOption.APPEND);
            return new WriteAppendLog(fileChannel, size);
        } catch (IOException e) {
            return null;
        }
    }


    public boolean append(byte[] data) {
        var expect = this.counter.get();
        if (this.counter.compareAndSet(expect, expect + data.length)) {
            var crc = CRCUtil.crc32(data);
            var timestamp = System.currentTimeMillis();
            var buffer = ByteBuffer.allocate(data.length + 12);
            buffer.put(crc);
            buffer.putLong(timestamp);
            buffer.put(data);
            try {
                fileChannel.write(buffer);
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    boolean mergeTo(TopicIndex topicIndex) {
        var fileChannelUtil = FileChannelUtil.getInstance(this.fileChannel);
        var crc = fileChannelUtil.read(4, 0);
        var timestamp = fileChannelUtil.readLong();
        var content = fileChannelUtil.readVarByte();
        if (CRCUtil.check(content, crc)) {
            var topic = new String(content);
            var offsetElement = topicIndex.getFirst(topic);
            var lsm = LogStructMerge.getInstance(offsetElement.position);
            lsm.push(new DataBlock(timestamp, content));
            offsetElement.size++;
        }
        return true;
    }
}



