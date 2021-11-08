package ltd.fdsa.ds.api.job.handler.impl;

import lombok.var;
import ltd.fdsa.ds.api.job.log.JobFileAppender;
import ltd.fdsa.ds.api.model.Record;
import ltd.fdsa.ds.api.pipeline.Process;
import ltd.fdsa.ds.api.util.ScriptUtil;

import java.io.IOException;
import java.util.Arrays;

public class ScriptJobHandler implements Process {


    @Override
    public void execute(Record... records) {
        Arrays.stream(records).map(m -> m.columnMap()).forEach(context -> {

            // log file
            String logFileName = JobFileAppender.contextHolder.get();
            var cmd = context.get("cmd").getValue().toString();
            var scriptFileName = context.get("scriptFileName").getValue().toString();
            var scriptParams = context.get("scriptParams").getValue().toString();

            try {
                ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
