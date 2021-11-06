package ltd.fdsa.ds.api.job.context;

import ltd.fdsa.ds.api.Metric;
import ltd.fdsa.ds.api.container.PipelineProxyFactory;
import ltd.fdsa.ds.api.model.Record;
import ltd.fdsa.ds.api.pipeline.Pipeline;
import ltd.fdsa.ds.api.props.Configuration;

public class JobContext implements Pipeline {
    private final Metric metric;
    private final Configuration configuration;

    public JobContext(Metric metric, Configuration configuration) {
        this.metric = metric;
        this.configuration = configuration;
    }


    @Override
    public void start() {
        PipelineProxyFactory.getProxy(this.configuration, this);
    }

    @Override
    public void collect(Record... records) {

    }
}
