package ltd.fdsa.ds.api.pipeline;


import ltd.fdsa.ds.api.config.Configuration;
import ltd.fdsa.ds.api.model.Result;
import ltd.fdsa.ds.api.model.Record;

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
