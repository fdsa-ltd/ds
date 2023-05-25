package cn.zhumingwu.dataswitch.core.job.executor;


import cn.zhumingwu.dataswitch.core.job.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;
import cn.zhumingwu.dataswitch.core.job.log.JobFileAppender;
import cn.zhumingwu.dataswitch.core.job.thread.JobThread;
import cn.zhumingwu.dataswitch.core.job.thread.TriggerCallbackThread;
import cn.zhumingwu.dataswitch.core.pipeline.Process;
import lombok.var;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * 客户端执行器
 */
@Slf4j
public class JobExecutor {
    // ---------------------- job handler repository ----------------------
    private static ConcurrentMap<String, IJobHandler> jobProcessRepository = new ConcurrentHashMap<String, IJobHandler>();
    // ---------------------- job thread repository ----------------------
    private static ConcurrentMap<Long, JobThread> jobThreadRepository = new ConcurrentHashMap<Long, JobThread>();
    private final String appName;
    private final String ip;
    private final int port;
    private final String accessToken;
    private final String logPath;
    private final int logRetentionDays;

    public JobExecutor(Properties properties) {
        this.appName = properties.getProperty("name", "");
        this.ip = properties.getProperty("ip");
        this.port = Integer.parseInt(properties.getProperty("port", "8080"));
        this.logPath = properties.getProperty("log_path", "./logs");
        this.logRetentionDays = Integer.parseInt(properties.getProperty("log_days", "7"));
        this.accessToken = properties.getProperty("access_token", "");
    }

    /**
     * 注册本地Job Handler
     */
    public static IJobHandler registerJobHandler(String name, IJobHandler jobHandler) {
        return jobProcessRepository.put(name, jobHandler);
    }

    public static IJobHandler loadJobHandler(String name) {
        return jobProcessRepository.get(name);
    }

    public static JobThread startJob(Long jobId, IJobHandler handler, String... reasons) {
        //如果job已经运行，需要停止运行
        stopJob(jobId, reasons);
        JobThread newJobThread = new JobThread(jobId, handler);
        newJobThread.start();
        return jobThreadRepository.put(jobId, newJobThread);
    }

    public static void stopJob(Long jobId, String... removeOldReason) {
        JobThread oldJobThread = jobThreadRepository.remove(jobId);
        if (oldJobThread != null) {
            oldJobThread.toStop(String.join("\n", removeOldReason));
            oldJobThread.interrupt();
        }
    }

    public static JobThread loadJobThread(Long jobId) {
        JobThread jobThread = jobThreadRepository.get(jobId);
        return jobThread;
    }

    public void start() throws Exception {
        // init logpath
        JobFileAppender.initLogPath(logPath);


        // init TriggerCallbackThread
        TriggerCallbackThread.getInstance().start();

        // init executor-server todo

    }

    public void destroy() {
        // destory executor-server

        // destory jobThreadRepository
        if (jobThreadRepository.size() > 0) {
            for (var item : jobThreadRepository.entrySet()) {
                stopJob(item.getKey(), "web container destroy and kill the job.");
            }
            jobThreadRepository.clear();
        }
        jobProcessRepository.clear();

        // destory TriggerCallbackThread
        TriggerCallbackThread.getInstance().toStop();
    }

}
