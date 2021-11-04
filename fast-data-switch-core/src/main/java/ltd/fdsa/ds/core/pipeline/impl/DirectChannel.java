package ltd.fdsa.ds.core.pipeline.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.core.model.Record;
import ltd.fdsa.ds.core.pipeline.Channel;

@Slf4j
public class DirectChannel extends AbstractPipeline implements Channel {


    @Override
    public void collect(Record... records) {
        if (!this.isRunning()) {
            return;
        }
        for (var item : this.nextSteps) {
            item.collect(records);
        }
    }
}