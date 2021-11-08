package ltd.fdsa.ds.api.pipeline.impl;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.model.Record;
import ltd.fdsa.ds.api.pipeline.Channel;
import ltd.fdsa.ds.api.pipeline.Pipeline;

import java.util.List;
import java.util.concurrent.Executors;

@Slf4j
public class DisruptorChannel implements Channel {
    private Disruptor<Record> disruptor;

    @Override
    public void init() {
        int bufferSize = this.context().getInt("bufferSize", 1024);
        // Construct the Disruptor
        this.disruptor = new Disruptor<Record>(Record::new, bufferSize, Executors.defaultThreadFactory());
    }

    @Override
    public void start() {
        // Connect the handler
        disruptor.handleEventsWith(new RecordHandler(this.nextSteps()));
        // Start the Disruptor, starts all threads running
        disruptor.start();
    }

    @Override
    public void execute(Record... records) {
        for (var record : records) {
            this.disruptor.publishEvent(new RecordSender(record));
        }
    }

    @AllArgsConstructor
    class RecordHandler implements EventHandler<Record> {
        private final List<Pipeline> sinks;

        @Override
        public void onEvent(Record record, long l, boolean b) throws Exception {
            for (var sink : sinks) {
                sink.execute(record);
            }
        }
    }

    @AllArgsConstructor
    class RecordSender implements EventTranslator<Record> {
        private Record record;

        @Override
        public void translateTo(Record record, long l) {
        }
    }
}
