package ltd.fdsa.ds.plugin;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.container.PluginType;
import ltd.fdsa.ds.api.model.Result;
import ltd.fdsa.ds.api.model.Record;
import ltd.fdsa.ds.api.pipeline.Channel;
import ltd.fdsa.ds.api.config.Configuration;
import ltd.fdsa.ds.api.pipeline.impl.AbstractPipeline;

import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.Arrays;
import java.util.function.Function;

/*
使用Kafka实现数据渠道功能
*/
@Slf4j
public class KafkaChannel extends AbstractPipeline implements Channel, Function<String, Boolean> {
    KafkaClient kafkaClient;
    HttpClient httpClient = new HttpClient(null);

    @Override
    public Result<String> init(Configuration configuration) {
        this.config = configuration;
        this.name = this.config.getString("name", this.getClass().getCanonicalName());
        this.type = PluginType.valueOf(this.config.getString("type", "pipeline"));
        this.description = this.config.getString("description", this.getClass().getCanonicalName());
        String[] hosts = Arrays.stream(this.config.getString("hosts").split(",")).map(m -> m.trim()).toArray(String[]::new);
        this.kafkaClient = new KafkaClient(this.config.getString("topic"), hosts);
        // Send Job Config to Cluster
        var configurations = this.config.getConfigurations("pipelines");
        if (configurations != null && configurations.length > 0) {
            // invoke rpc to init
            httpClient.post("/api/job/init", RequestBody.create(MediaType.get("application/json"), this.config.toString()));
        }
        // Cluster will return related job Token
        // create clients to manager cluster nodes
        // then we can remote process call to collect data;
        return Result.success();
    }

    @Override
    public void start() {
        if (this.running.compareAndSet(false, true)) {
            // monitor cluster nodes
            kafkaClient.start(this::apply);
        }
    }


    @Override
    public void collect(Record... records) {
        if (!this.isRunning()) {
            return;
        }
        httpClient.post("/api/job/collect", RequestBody.create(MediaType.get("app"), ""));
    }


    @Override
    public Boolean apply(String s) {
        return false;
    }
}
