package cn.zhumingwu.dataswitch.core.job.executor;

import cn.zhumingwu.dataswitch.core.job.model.LogResult;
import cn.zhumingwu.dataswitch.core.model.Result;

import java.util.Map;

public interface Executor {
    /**
     * coordinators send head beat
     *
     * @return
     */
    Result<String> beat();

    /**
     * idle beat for job
     *
     * @param jobId
     * @return
     */
    Result<String> idleBeat(int jobId);

    /**
     * coordinators send stop to executor
     *
     * @param jobId
     * @return
     */
    Result<String> stop(int jobId);

    /**
     * coordinators get job's log
     *
     * @param jobId
     * @param lastVersion
     * @return
     */
    Result<LogResult> log(int jobId, String lastVersion);

    /**
     * coordinators send a job run to executor
     *
     * @return
     */
    Result<String> run(int jobId, Map<String, String> config);

    Result<String> init(Long processId, Map<String, String> config);

    Result<String> start(Long processId);


}
