package cn.zhumingwu.dataswitch.core.job.executor;

import cn.zhumingwu.dataswitch.core.job.model.LogResult;
import cn.zhumingwu.dataswitch.core.model.Result;

import java.util.List;
import java.util.Map;

public interface Executor {
    /**
     * coordinators send a job run to executor
     *
     * @return task id
     */
    Result<Long> start(Long jobId, Map<String, String> config);

    /**
     * coordinators send stop to executor
     *
     * @param jobId  全局唯一的具体任务编号
     * @param taskId 全局唯一的具体任务处理编号
     * @return 成功或失败
     */
    Result<String> stop(Long jobId, Long taskId);

    /**
     * coordinator send head beat to executors, then executor provides cpu and memory info
     *
     * @return map for cpu and memory info
     */
    Result<Map<String, String>> stat();

    /**
     * coordinator send head beat to executors, then executor provides task info for the job
     *
     * @param taskId 全局唯一的具体任务处理编号
     * @return map for task info
     */
    Result<Map<String, String>> stat(Long taskId);


    /**
     * coordinators get job's log
     *
     * @param taskId      全局唯一的具体任务处理编号
     * @param lastVersion 日志最后编号
     * @return LogResult
     */
    Result<LogResult> stat(Long taskId, Long lastVersion);

}
