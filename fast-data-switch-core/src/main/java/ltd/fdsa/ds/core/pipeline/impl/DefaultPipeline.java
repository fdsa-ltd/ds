package ltd.fdsa.ds.core.pipeline.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.core.model.Record;
import ltd.fdsa.ds.core.pipeline.Process;

@Slf4j
public class DefaultPipeline extends AbstractPipeline implements Process {

    @Override
    public void collect(Record... records) {
        if (this.isRunning()) {
            for (var map : records) {
                StringBuilder sb = new StringBuilder();
                for (var entry : map.entrySet()) {
                    sb.append(entry.getKey());
                    sb.append('=').append('"');
                    sb.append(entry.getValue());
                    sb.append('"');
                    sb.append(',').append(' ');
                }
                log.info("{}:{}", this.name, sb.toString());
            }
        }
    }


}