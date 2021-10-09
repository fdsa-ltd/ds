package ltd.fdsa.switcher.core.pipeline;


import ltd.fdsa.switcher.core.config.Configuration;
import ltd.fdsa.switcher.core.job.model.Result;
import ltd.fdsa.switcher.core.model.Record;

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
