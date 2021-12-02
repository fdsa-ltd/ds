package ltd.fdsa.ds.core.pipeline;


import ltd.fdsa.ds.core.props.Props;
import ltd.fdsa.ds.core.model.Record;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface Pipeline {

    default Props context() {
        return null;
    }

    // init
    default void init() {
    }

    // start
    default void start() {
    }

    // execute
    void execute(Record... records);

    // output
    default Map<String, String> scheme() {
        return Collections.emptyMap();
    }

    default List<Pipeline> nextSteps() {
        return Collections.emptyList();
    }

    default void stop() {
    }

    default void stop(Runnable callback) {
        stop();
        callback.run();
    }

    default boolean isRunning() {
        return false;
    }
}
