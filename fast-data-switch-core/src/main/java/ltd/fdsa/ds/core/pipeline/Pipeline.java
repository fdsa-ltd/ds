package ltd.fdsa.ds.core.pipeline;


import ltd.fdsa.ds.core.config.Configuration;
import ltd.fdsa.ds.core.job.model.Result;
import ltd.fdsa.ds.core.model.Record;

import org.springframework.context.SmartLifecycle;

import java.util.Map;

public interface Pipeline extends SmartLifecycle {

    // init
    Result<String> init(Configuration configuration);

    // collector
    void collect(Record... records);

    // output
    Map<String, String> scheme();
}
