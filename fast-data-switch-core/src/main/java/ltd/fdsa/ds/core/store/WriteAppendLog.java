package ltd.fdsa.ds.core.store;

import lombok.var;
import ltd.fdsa.ds.core.util.CRCUtil;
import ltd.fdsa.ds.core.util.FileChannelUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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

    private final static int WAL_SIZE = 1024 * 1024;
    private final static short WRITE_APPEND_LOG_SIZE = 4;
    private final static FileChannel[] WRITE_APPEND_LOGS = new FileChannel[WRITE_APPEND_LOG_SIZE];


    private int index = -1;

    WriteAppendLog() {
    }

    long size() {
        try {
            return this.getCurrentFile().size();
        } catch (IOException e) {
            return -1;
        }
    }

    private void ensureCapacityInternal(int minCapacity) {
        if (this.size() < 0 || this.size() + minCapacity > WAL_SIZE) {

        }
    }

    private void grow() {
        this.index++;
        if (this.index >= WRITE_APPEND_LOG_SIZE) {
            this.index %= WRITE_APPEND_LOG_SIZE;
        }

        var path = Paths.get("./", "data", "wal", this.index + ".wal");
        try {
            WRITE_APPEND_LOGS[this.index] = FileChannel.open(path, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
        }

    }

    FileChannel getCurrentFile() {
        return WRITE_APPEND_LOGS[index];
    }


    public boolean append(byte[] data) {
        var crc = CRCUtil.crc32(data);
        var timestamp = System.currentTimeMillis();
        var buffer = ByteBuffer.allocate(data.length + 12);
        buffer.put(crc);
        buffer.putLong(timestamp);
        buffer.put(data);
        ensureCapacityInternal(data.length);
        try {
            this.getCurrentFile().write(buffer);
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    // todo
    boolean mergeTo(TopicIndex topicIndex) {
        var fileChannelUtil = FileChannelUtil.getInstance(this.getCurrentFile());
        var crc = fileChannelUtil.read(4, 0);
        var timestamp = fileChannelUtil.readLong();
        var content = fileChannelUtil.readVarByte();
        if (CRCUtil.check(content, crc)) {
            var topic = new String(content);
            var offsetElement = topicIndex.getFirst(topic);
            var lsm = LogStructMerge.getInstance(offsetElement.position);
            lsm.push(new DataMessage(timestamp, content));
            offsetElement.size++;
        }
        return true;
    }
}



