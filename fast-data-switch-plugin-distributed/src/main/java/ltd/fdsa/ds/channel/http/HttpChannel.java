package ltd.fdsa.ds.channel.http;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.model.Record;
import ltd.fdsa.ds.api.pipeline.Channel;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import java.util.LinkedList;

/*
使用Http RPC
*/
@Slf4j
public class HttpChannel implements Channel {
    private final HttpClient client = new HttpClient(new LinkedList<>());

    @Override
    public void init() {
        // Send Job Config to Cluster
        var configurations = this.config().getConfigurations("pipelines");
        if (configurations != null && configurations.length > 0) {
            // invoke rpc to init
            client.post("/api/job/init", RequestBody.create(MediaType.get("application/json"), this.config().toString()));
        }
    }

    @Override
    public void collect(Record... records) {
        if (!this.isRunning()) {
            return;
        }
        client.post("/test", RequestBody.create(MediaType.get("app"), ""));
    }
}
