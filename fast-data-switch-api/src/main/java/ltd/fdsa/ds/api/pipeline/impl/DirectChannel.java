package ltd.fdsa.ds.api.pipeline.impl;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.model.Record;
import ltd.fdsa.ds.api.pipeline.Channel;

@Slf4j
public class DirectChannel implements Channel {

    @Override
    public void collect(Record... records) {
        if (!this.isRunning()) {
            return;
        }
        for (var item : this.nextSteps()) {
            item.collect(records);
        }
    }
}