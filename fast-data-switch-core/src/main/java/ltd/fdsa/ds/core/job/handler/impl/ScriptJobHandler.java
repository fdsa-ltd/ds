package ltd.fdsa.ds.core.job.handler.impl;

import lombok.var;
import ltd.fdsa.ds.core.job.handler.JobHandler;
import ltd.fdsa.ds.core.job.log.JobFileAppender;
import ltd.fdsa.ds.core.job.model.Result;
import ltd.fdsa.ds.core.util.ScriptUtil;


import java.io.IOException;
import java.util.Map;

public class ScriptJobHandler implements JobHandler {

    @Override
    public Result<Object> execute(Map<String, String> context)   {
        // log file
        String logFileName = JobFileAppender.contextHolder.get();
        var cmd = context.get("cmd");
        var scriptFileName = context.get("scriptFileName");
        var scriptParams = context.get("scriptParams");
        int exitValue = 0;
        try {
            exitValue = ScriptUtil.execToFile(cmd, scriptFileName, logFileName, scriptParams);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (exitValue == 0) {
            return Result.success();
        } else {
            return Result.fail(JobHandler.FAIL.getCode(), "script exit value(" + exitValue + ") is failed");
        }
    }
}
