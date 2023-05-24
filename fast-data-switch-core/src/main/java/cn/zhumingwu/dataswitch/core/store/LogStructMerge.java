package cn.zhumingwu.dataswitch.core.store;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.iot.cbor.CborMap;
import com.google.iot.cbor.CborParseException;
import lombok.var;
import cn.zhumingwu.dataswitch.core.util.CRCUtil;
import cn.zhumingwu.dataswitch.core.util.FileChannelUtil;
import cn.zhumingwu.dataswitch.core.util.VIntUtil;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.HashMap;


/**
 * LSM, the log-structured merge-tree is a data structure with performance characteristics that make it attractive for providing indexed access to file with high insert volume
 * LSM trees maintain data in two or more separate structures, each of which is optimized for its respective underlying storage medium;
 *
 * <p>data is synchronized between the two structures efficiently, in batches.
 *
 * @author zhumingwu
 * @since 1.0.0
 */
public class LogStructMerge {

    static Cache<Long, LogStructMerge> FILE_HANDLER = CacheBuilder.newBuilder().maximumSize(1024)
            .expireAfterAccess(Duration.ofMinutes(10)).removalListener(new RemovalListener<Long, LogStructMerge>() {
                @Override
                public void onRemoval(RemovalNotification<Long, LogStructMerge> removalNotification) {
                    var fileId = removalNotification.getKey();
                    var lsm = removalNotification.getValue();
                    lsm.close();
                }
            })
            .build();

    private final FileChannel lsmFile;
    private final String topic;
    private final byte version;
    private final byte status;
    private final int schema;
    private final long offset;
    private final long start;
    private int size;
    private int end;

    public LogStructMerge(FileChannel lsmChannel, String topic) throws IOException {
        this.topic = topic;
        this.lsmFile = lsmChannel;
        byte[] header = new byte[0];

        header = FileChannelUtil.read(this.lsmFile, 2);

        this.version = header[0];
        this.status = header[1];
        this.offset = FileChannelUtil.readLong(this.lsmFile);
        this.start = FileChannelUtil.readLong(this.lsmFile);
        this.schema = FileChannelUtil.readInt(this.lsmFile);
        FileChannelUtil.newPosition(this.lsmFile, -8);
        this.size = FileChannelUtil.readInt(this.lsmFile);
        this.end = FileChannelUtil.readInt(this.lsmFile);
    }

    public static LogStructMerge getInstance(String topic, long fileId) {
        if (FILE_HANDLER.getIfPresent(fileId) == null) {
            var path = Paths.get("./", "data", "lsm", topic, fileId + ".lsm");
            try {
                if (!Files.exists(path)) {
                    return null;
                }
                var fileChannel = FileChannel.open(path, StandardOpenOption.READ);
                FILE_HANDLER.put(fileId, new LogStructMerge(fileChannel, topic));
            } catch (IOException e) {
                return null;
            }

        }
        return FILE_HANDLER.getIfPresent(fileId);
    }

    public EventMessage read(long position, Long offsetExcept) {
        try {
            FileChannelUtil.newPosition(this.lsmFile, position);
            var offset = FileChannelUtil.readVLen(this.lsmFile);
            if (offset <= 0) {
                return null;
            }
            var status = FileChannelUtil.read(this.lsmFile, 1)[0];
            switch (status) {
                case -1:
                    var size = FileChannelUtil.readVLen(this.lsmFile);
//                FileChannelUtil.newPosition(this.lsmFile, this.lsmFile.position() +size);
                    return read(this.lsmFile.position() + size, offsetExcept);
            }
            var size = FileChannelUtil.readVLen(this.lsmFile);
            var crc = FileChannelUtil.readInt(this.lsmFile);
            var timestamp = FileChannelUtil.readVLen(this.lsmFile) + this.start;
            var header = new HashMap<String, String>();
            try {
                var map = CborMap.createFromCborByteArray(FileChannelUtil.readVarByte(this.lsmFile));
                for (var entry : map.entrySet()) {
                    header.put(entry.getKey().toString(), entry.getValue().toString());
                }
            } catch (CborParseException e) {

            }
            var content = FileChannelUtil.readVarByte(this.lsmFile);
            if (CRCUtil.crc32(content).check(crc)) {
                return new EventMessage(this.topic, content, timestamp, header);
            }
        } catch (IOException e) {

        }
        return null;
    }

    public boolean write(EventMessage message, long offset) {
        var offsetDelta = VIntUtil.vintEncode(offset - this.offset);
        var tsDelta = VIntUtil.vintEncode(message.getTimestamp() - this.start);
        var header = message.getHeader().toCborByteArray();
        var headerLength = VIntUtil.vintEncode(header.length);
        var payload = message.getPayload();
        var payloadLength = VIntUtil.vintEncode(payload.length);
        var length = header.length + payload.length + 20;
        var crc = CRCUtil.crc32(offsetDelta);
        crc.update(tsDelta);
        crc.update(header);
        crc.update(payload);
        try {
            FileChannelUtil.newPosition(this.lsmFile, -8);
            FileChannelUtil.writeVarByte(this.lsmFile, offsetDelta);
            FileChannelUtil.writeByte(this.lsmFile, new byte[]{1});
            FileChannelUtil.writeVLen(this.lsmFile, length);
            FileChannelUtil.writeByte(this.lsmFile, crc.getBytes());
            FileChannelUtil.writeVarByte(this.lsmFile, tsDelta);
            FileChannelUtil.writeVarByte(this.lsmFile, header);
            FileChannelUtil.writeVarByte(this.lsmFile, payload);
            this.size++;
            this.end = (int) (message.getTimestamp() - this.start);
            FileChannelUtil.writeInt(this.lsmFile, this.size);
            FileChannelUtil.writeInt(this.lsmFile, this.end);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void close() {
        try {
            this.lsmFile.close();
        } catch (IOException e) {
//            throw new RuntimeException(e);
        }
    }
}
