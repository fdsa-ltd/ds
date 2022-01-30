package ltd.fdsa.ds.core.store;

import lombok.var;

import java.util.HashMap;
import java.util.Map;

/**
 * Memory Table to cache data
 * <p>fast to get recent data from memory.
 *
 * @author zhumingwu
 * @since 2022/1/27 17:37
 */
public class MemTable {
    final static int MAX_SIZE = 1024;
    // memory buffer cache
    private final Map<String, RingBuffer> ringBufferMap;
    // memory topic index
    private final TopicIndex topicIndex;

    public MemTable() {
        this.topicIndex = TopicIndex.loadTopicIndex("topics");
        var topics = this.topicIndex.topics();
        this.ringBufferMap = new HashMap<>(topics.size());
        for (var topic : topics) {
            var first = this.topicIndex.getFirst(topic);
            this.ringBufferMap.put(topic, new RingBuffer(first.offset + first.size, MAX_SIZE));
        }
    }

    public boolean put(String key, byte[] data) {
        if (!this.topicIndex.ensureKey(key)) {
            this.ringBufferMap.put(key, new RingBuffer(-1, MAX_SIZE));
        }
        var result = WriteAppendLog.getWriteAppendLogs(this.topicIndex).append(data);
        if (result) {
            DataBlock dataBlock = new DataBlock(System.currentTimeMillis(), data);
            var offset = this.ringBufferMap.get(key).push(dataBlock);
            return true;
        }
        return false;
    }

    public DataBlock get(String key, long offset) {
        if (!this.ringBufferMap.containsKey(key)) {
            return getFromFile(key, offset);
        }
        // 最近的数据
        var buffer = this.ringBufferMap.get(key);
        if (offset >= buffer.getOffset() && offset <= buffer.getOffset() + buffer.getSize()) {
            return buffer.pull(offset);
        }
        // 读取文件
        return getFromFile(key, offset);
    }

    DataBlock getFromFile(String key, long offset) {
        // 读取topic文件
        var offsetElement = this.topicIndex.search(key, offset);
        if (offsetElement == null) {
            return null;
        }
        var fileId = offsetElement.position;
        // 历史数据中取
        return LogStructMerge.getInstance(fileId).pull(offset);
    }
}
