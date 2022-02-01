package ltd.fdsa.ds.core.store;

import lombok.var;
import ltd.fdsa.ds.core.util.FileChannelUtil;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.channels.FileChannel;
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

    TopicIndex() {
        var root = new File("./data/lsm/");
        var files = listAllFiles(root, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith("topics.idx"));
            }
        });
        for (var item : files) {
            FileChannel fileChannel = null;
            try {
                fileChannel = FileChannel.open(item.toPath(), StandardOpenOption.READ);
                load(FileChannelUtil.getInstance(fileChannel));
            } catch (IOException e) {

            } finally {
                if (fileChannel != null) {
                    try {
                        fileChannel.close();
                    } catch (IOException e) {

                    }
                    fileChannel = null;
                }
            }
        }
        for (var entry : this.fileIndex.values()) {
            entry.sort();
        }
    }

    private List<File> listAllFiles(File root, FilenameFilter filter) {
        List<File> list = new LinkedList<>();
        {
            for (var file : root.listFiles()) {
                if (file.isDirectory()) {
                    list.addAll(listAllFiles(file, filter));
                }
                if (filter.accept(file, file.getName())) {
                    list.add(file);
                }
            }
        }
        return list;
    }

    public static TopicIndex loadTopicIndex() {
        if (instance == null) {
            instance = new TopicIndex();
        }
        return instance;
    }

    private void load(FileChannelUtil file){
        while (true) {
            // topic
            var length = (int) file.readVLen();
            if (length <= 0) {
                break;
            }
            var data = file.read(length);
            var topic = new String(data);
            this.ensureKey(topic);
            // offset and file id
            var offset = file.readVLen();
            var size = file.readVLen();
            var fileId = file.readVLen();
            var element = new SortedOffset.OffsetElement(offset, (int) size, fileId);
            this.fileIndex.get(topic).add(element);
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
