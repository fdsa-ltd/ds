package ltd.fdsa.ds.core.store;

import lombok.var;
import ltd.fdsa.ds.core.util.FileChannelUtil;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
/**
 * The map of topic and file id
 * <p>fast to get file id of one topic
 *
 * @author zhumingwu
 * @since 2022/1/27 17:41
 */ 
public class TopicIndex {
    private static TopicIndex instance;
    private final Map<String, SortedOffset> fileIndex = new HashMap<>();
    private final FileChannelUtil file;

    TopicIndex(FileChannel fileChannel) {
        this.file = FileChannelUtil.getInstance(fileChannel);
        load();
    }

    public static TopicIndex loadTopicIndex(String fileName) {
        if (instance == null) {
            try {
                var path = Paths.get("./", "data", "lsm", fileName + ".idx");
                instance = new TopicIndex(FileChannel.open(path, StandardOpenOption.READ));
            } catch (IOException e) {
                return null;
            }
        }
        return instance;
    }

    private void load() {
        while (true) {
            // topic
            var length = (int) this.file.readVLen();
            if (length <= 0) {
                break;
            }
            var data = this.file.read(length);
            var topic = new String(data);
            if (!this.fileIndex.containsKey(topic)) {
                this.fileIndex.put(topic, new SortedOffset());
            }
            //
            var offset = this.file.readVLen();
            var size = this.file.readVLen();
            var fileId = this.file.readVLen();
            var element = new SortedOffset.OffsetElement(offset, (int) size, fileId);
            this.fileIndex.get(topic).add(element);
        }
        for (var entry : this.fileIndex.values()) {
            entry.sort();
        }
    }

    public SortedOffset.OffsetElement getFirst(String topic) {
        if (!this.fileIndex.containsKey(topic)) {
            return null;
        }
        return this.fileIndex.get(topic).get(0);
    }

    public SortedOffset.OffsetElement search(String topic, long offset) {
        if (!this.fileIndex.containsKey(topic)) {
            return null;
        }
        var index = this.fileIndex.get(topic).search(offset);
        if (index >= 0) {
            return this.fileIndex.get(topic).get(index);
        }
        return null;
    }

    public Set<String> topics() {
        return this.fileIndex.keySet();
    }

    public boolean ensureKey(String key) {
        if (this.fileIndex.containsKey(key)) {
            return true;
        }
        this.fileIndex.put(key, new SortedOffset());
        return false;
    }
}
