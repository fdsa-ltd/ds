package ltd.fdsa.ds.core.job.executor;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.core.job.enums.ExecutorBlockStrategyEnum;
import ltd.fdsa.ds.core.job.log.JobFileAppender;
import ltd.fdsa.ds.core.job.model.LogResult;
import ltd.fdsa.ds.core.job.model.TriggerParam;
import ltd.fdsa.ds.core.job.thread.JobThread;
import ltd.fdsa.ds.core.model.Result;
import ltd.fdsa.ds.core.pipeline.Process;
import ltd.fdsa.ds.core.props.DefaultProps;

import java.util.Map;

/**
 * 客户端具体实现
 *
 */
@Slf4j
public class ExecutorImpl implements Executor {
    @Override
    public Result<String> beat() {
        return Result.success();
    }

    @Override
    public Result<String> idleBeat(int jobId) {
        boolean isRunningOrHasQueue = false;
        JobThread jobThread = JobExecutor.loadJobThread(jobId);
        if (jobThread != null && jobThread.isRunningOrHasQueue()) {
            isRunningOrHasQueue = true;
        }
        if (isRunningOrHasQueue) {
            return Result.fail(500, "job thread is running or has trigger queue.");
        }
        return Result.success();
    }

    @Override
    public Result<String> stop(int jobId) {
        // kill handlerThread, and create new one
        JobThread jobThread = JobExecutor.loadJobThread(jobId);
        if (jobThread != null) {
            JobExecutor.stopJob(jobId, "scheduling center kill job.");
            return Result.success();
        }

        return Result.success("job thread already killed.");
    }

    @Override
    public Result<LogResult> log(int jobId, String lastVersion) {
        String logFileName = JobFileAppender.getLogFile(jobId);

        LogResult logResult = JobFileAppender.readLog(logFileName, Integer.parseInt(lastVersion));
        return Result.success(logResult);
    }

    @Override
    public Result<String> run(int jobId, Map<String, String> config) {
        var props = DefaultProps.fromMaps(config);
        String handler = props.get("class");
        String blockStrategy = props.get("strategy");
        long timeout = props.getLong("timeout", Long.MAX_VALUE);

        if (Strings.isNullOrEmpty(handler)) {
            return Result.fail(404, "no job handler");
        }
        // new job handler
        var newJobHandler = JobExecutor.loadJobHandler(handler);
        if (newJobHandler == null) {
            return Result.fail(404, "job handler [" + handler + "] not found.");
        }
        // load old：jobHandler + jobThread
        JobThread jobThread = JobExecutor.loadJobThread(jobId);
        Process jobHandler = jobThread != null ? jobThread.getHandler() : null;
        String removeOldReason = null;
        // valid old jobThread
        if (jobThread != null && jobHandler != newJobHandler) {
            // change handler, need kill old thread
            removeOldReason = "change job handler or glue type, and terminate the old job thread.";
            jobThread = null;
            jobHandler = newJobHandler;
        }
        // executor block strategy
        if (jobThread != null) {
            ExecutorBlockStrategyEnum blockStrategyEnum = ExecutorBlockStrategyEnum.match(blockStrategy, null);
            if (ExecutorBlockStrategyEnum.DISCARD_LATER == blockStrategyEnum) {
                // discard when running
                if (jobThread.isRunningOrHasQueue()) {
                    return Result.fail(500, "block strategy effect：" + ExecutorBlockStrategyEnum.DISCARD_LATER.getTitle());
                }
            } else if (ExecutorBlockStrategyEnum.COVER_EARLY == blockStrategyEnum) {
                // kill running jobThread
                if (jobThread.isRunningOrHasQueue()) {
                    removeOldReason = "block strategy effect：" + ExecutorBlockStrategyEnum.COVER_EARLY.getTitle();

                    jobThread = null;
                }
            } else {
                // just queue trigger
            }
        }

        // replace thread (new or exists invalid)
        if (jobThread == null) {
            jobThread = JobExecutor.startJob(jobId, jobHandler, removeOldReason);
        }
        // push data to queue
        var triggerParam = new TriggerParam();
        triggerParam.setExecutorHandler(handler);
        triggerParam.setJobId(jobId);
        Result<String> pushResult = jobThread.pushTriggerQueue(triggerParam);
        return pushResult;
    }

    @Override
    public Result<String> init(Long processId, Map<String, String> config) {
        var props = DefaultProps.fromMaps(config);
        String handler = props.get("class");
        String blockStrategy = props.get("strategy");
        long timeout = props.getLong("timeout", Long.MAX_VALUE);
        return null;
    }

    @Override
    public Result<String> start(Long processId) {
        return null;
    }
}
