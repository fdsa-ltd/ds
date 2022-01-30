package ltd.fdsa.ds.core.store;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.var;
import ltd.fdsa.ds.core.util.CRCUtil;
import ltd.fdsa.ds.core.util.FileChannelUtil;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.concurrent.Callable;


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
            .expireAfterAccess(Duration.ofMinutes(10))
            .build();

    private final FileChannelUtil lsmFile;

    private final byte[] magic;

    private final byte[] version;

    private final String topic;

    private final long offset;

    private final long startTime;

    private final long headerSize;

    public LogStructMerge(FileChannel lsmChannel) {
        this.lsmFile = FileChannelUtil.getInstance(lsmChannel);
        this.magic = this.lsmFile.read(4, 0);
        this.version = this.lsmFile.read(4);
        var data = this.lsmFile.readVarByte();
        this.topic = new String(data);
        this.offset = this.lsmFile.readLong();
        this.startTime = this.lsmFile.readLong();
        this.headerSize = this.lsmFile.position();
    }


    public static LogStructMerge getInstance(long fileId) {
        if (FILE_HANDLER.getIfPresent(fileId) == null) {
            var path = Paths.get("./", "data", "lsm", fileId + ".lsm");
            try {
                if (!Files.exists(path)) {
                    return null;
                }
                var fileChannel = FileChannel.open(path, StandardOpenOption.READ);
                FILE_HANDLER.put(fileId, new LogStructMerge(fileChannel));
            } catch (IOException e) {
                return null;
            }

        }
        return FILE_HANDLER.getIfPresent(fileId);
    }

    public DataBlock pull(long position) {
        var crc = this.lsmFile.read(4, position);
        var timeGap = this.lsmFile.readVLen();
        var content = this.lsmFile.readVarByte();
        if (CRCUtil.check(content, crc)) {
            return new DataBlock(this.startTime + timeGap, content);
        }
        return pull(-1);
    }

    boolean push(byte[] data) {
        return push(new DataBlock(data));
    }

    boolean push(DataBlock dataBlock) {
        var crc = CRCUtil.crc32(dataBlock.payload);
        var timeGap = dataBlock.timestamp - this.startTime;
        this.lsmFile.writeByte(crc);
        this.lsmFile.writeVLen(timeGap);
        this.lsmFile.writeVarByte(dataBlock.payload);
        return true;
    }

    boolean mergeTo(long fileId) {
//        var crc = fileChannelUtil.read(4, 0);
//        var timestamp = fileChannelUtil.readLong();
//        var content = fileChannelUtil.readVarByte();
//        var lsm = LogStructMerge.getInstance(fileId);
//        if (CRCUtil.check(content, crc)) {
//            var topic = new String(content);
//            var offsetElement = topicIndex.getFirst(topic);
//            var lsm = LogStructMerge.getInstance(offsetElement.position);
//            lsm.push(content, timestamp);
//            offsetElement.size++;
//        }
        return true;
    }
}
