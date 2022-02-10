package ltd.fdsa.job.admin.trigger;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.core.job.enums.ExecutorBlockStrategyEnum;
import ltd.fdsa.ds.core.job.executor.Executor;
import ltd.fdsa.ds.core.job.model.TriggerParam;
import ltd.fdsa.ds.core.model.Result;
import ltd.fdsa.ds.core.util.I18nUtil;
import ltd.fdsa.job.admin.context.ApplicationContextHolder;
import ltd.fdsa.job.admin.entity.JobGroup;
import ltd.fdsa.job.admin.entity.JobInfo;
import ltd.fdsa.job.admin.entity.JobLog;
import ltd.fdsa.job.admin.repository.JobGroupRepository;
import ltd.fdsa.job.admin.repository.JobInfoRepository;
import ltd.fdsa.job.admin.repository.JobLogRepository;
import ltd.fdsa.job.admin.route.ExecutorRouteStrategyEnum;
import ltd.fdsa.job.admin.scheduler.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Date;

@Slf4j
public class JobTrigger {

    /**
     * trigger job
     *
     * @param jobId
     * @param triggerType
     * @param failRetryCount        >=0: use this param <0: use param from job info config
     * @param executorShardingParam
     * @param executorParam         null: use job param not null: cover job param
     */
    public static void trigger(int jobId, TriggerTypeEnum triggerType, int failRetryCount, String executorShardingParam, String executorParam) throws UnknownHostException {
        // load data
        JobInfo jobInfo = ApplicationContextHolder.getBean(JobInfoRepository.class).findById(jobId).get();
        if (jobInfo == null) {
            log.warn(">>>>>>>>>>>> trigger fail, jobId invalid，jobId={}", jobId);
            return;
        }
        if (executorParam != null) {
            jobInfo.setExecutorParam(executorParam);
        }
        int finalFailRetryCount =
                failRetryCount >= 0 ? failRetryCount : jobInfo.getExecutorFailRetryCount();
        JobGroup group = ApplicationContextHolder.getBean(JobGroupRepository.class).findById(jobInfo.getGroupId()).get();

        // sharding param
        int[] shardingParam = null;
        if (executorShardingParam != null) {
            String[] shardingArr = executorShardingParam.split("/");
            if (shardingArr.length == 2 && isNumeric(shardingArr[0]) && isNumeric(shardingArr[1])) {
                shardingParam = new int[2];
                shardingParam[0] = Integer.valueOf(shardingArr[0]);
                shardingParam[1] = Integer.valueOf(shardingArr[1]);
            }
        }
//        if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST
//                == ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null)
//                && group.getRegistryList() != null
//                && !group.getRegistryList().isEmpty()
//                && shardingParam == null) {
//            for (int i = 0; i < group.getRegistryList().size(); i++) {
//                processTrigger(
//                        group, jobInfo, finalFailRetryCount, triggerType, i, group.getRegistryList().size());
//            }
//        } else {
        if (shardingParam == null) {
            shardingParam = new int[]{0, 1};
        }
        processTrigger(
                group, jobInfo, finalFailRetryCount, triggerType, shardingParam[0], shardingParam[1]);
//        }
    }

    private static boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * @param group               job group, registry list may be empty
     * @param jobInfo
     * @param finalFailRetryCount
     * @param triggerType
     * @param index               sharding index
     * @param total               sharding index
     */
    private static void processTrigger(JobGroup group, JobInfo jobInfo, int finalFailRetryCount, TriggerTypeEnum triggerType, int index, int total) throws UnknownHostException {

        // param
        // block strategy
        ExecutorBlockStrategyEnum blockStrategy = ExecutorBlockStrategyEnum.match(jobInfo.getExecutorBlockStrategy(), ExecutorBlockStrategyEnum.SERIAL_EXECUTION);
        // route strategy
        ExecutorRouteStrategyEnum executorRouteStrategyEnum = ExecutorRouteStrategyEnum.match(jobInfo.getExecutorRouteStrategy(), null);
        String shardingParam = (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum)
                ? String.valueOf(index).concat("/").concat(String.valueOf(total))
                : null;

        // 1、save log-id
        JobLog jobLog = new JobLog();
        jobLog.setJobGroup(jobInfo.getGroupId());
        jobLog.setJobId(jobInfo.getId());
        jobLog.setTriggerTime(new Date());
        ApplicationContextHolder.getBean(JobLogRepository.class).save(jobLog);

        // 2、init trigger-param
        TriggerParam triggerParam = new TriggerParam();
        triggerParam.setJobId(jobInfo.getId());
        triggerParam.setExecutorHandler(jobInfo.getExecutorHandler());
//todo        triggerParam.setExecutorParams( jobInfo.getExecutorParam());
        triggerParam.setExecutorBlockStrategy(jobInfo.getExecutorBlockStrategy());
        triggerParam.setExecutorTimeout(jobInfo.getExecutorTimeout());
        triggerParam.setLogId(jobLog.getId());
        triggerParam.setLogDateTime(jobLog.getTriggerTime().getTime());
        triggerParam.setBroadcastIndex(index);
        triggerParam.setBroadcastTotal(total);

        // 3、init address
        String address = null;
        Result<String> routeAddressResult = null;
//        if (group.getRegistryList() != null && !group.getRegistryList().isEmpty()) {
//            if (ExecutorRouteStrategyEnum.SHARDING_BROADCAST == executorRouteStrategyEnum) {
//                if (index < group.getRegistryList().size()) {
//                    address = group.getRegistryList().get(index);
//                } else {
//                    address = group.getRegistryList().get(0);
//                }
//            } else {
//                routeAddressResult =
//                        executorRouteStrategyEnum.getRouter().route(triggerParam, group.getRegistryList());
//                if (routeAddressResult.getCode() == Result.success().getCode()) {
//                    address = routeAddressResult.getData();
//                }
//            }
//        } else {
        routeAddressResult = Result.fail(500, I18nUtil.getInstance("").getString("jobconf_trigger_address_empty"));
//        }

        // 4、trigger remote executor
        Result<String> triggerResult = null;
        if (address != null) {
            triggerResult = runExecutor(triggerParam, address);
        } else {
            triggerResult = Result.fail(500, null);
        }

        // 5、collection trigger info
        StringBuffer triggerMsgSb = new StringBuffer();
        triggerMsgSb
                .append(I18nUtil.getInstance("").getString("jobconf_trigger_type"))
                .append("：")
                .append(triggerType.getTitle());
        triggerMsgSb
                .append("<br>")
                .append(I18nUtil.getInstance("").getString("jobconf_trigger_admin_adress"))
                .append("：")
                .append(Inet4Address.getLocalHost().getHostAddress());
        triggerMsgSb
                .append("<br>")
                .append(I18nUtil.getInstance("").getString("jobconf_trigger_exe_regtype"))
                .append("：")
                .append(
                        (group.getType() == 0)
                                ? I18nUtil.getInstance("").getString("jobgroup_field_addressType_0")
                                : I18nUtil.getInstance("").getString("jobgroup_field_addressType_1"));
        triggerMsgSb
                .append("<br>")
                .append(I18nUtil.getInstance("").getString("jobconf_trigger_exe_regaddress"))
                .append("：")
                .append("");
        triggerMsgSb
                .append("<br>")
                .append(I18nUtil.getInstance("").getString("jobinfo_field_executorRouteStrategy"))
                .append("：")
                .append(executorRouteStrategyEnum.getTitle());
        if (shardingParam != null) {
            triggerMsgSb.append("(" + shardingParam + ")");
        }
        triggerMsgSb
                .append("<br>")
                .append(I18nUtil.getInstance("").getString("jobinfo_field_executorBlockStrategy"))
                .append("：")
                .append(blockStrategy.getTitle());
        triggerMsgSb
                .append("<br>")
                .append(I18nUtil.getInstance("").getString("jobinfo_field_timeout"))
                .append("：")
                .append(jobInfo.getExecutorTimeout());
        triggerMsgSb
                .append("<br>")
                .append(I18nUtil.getInstance("").getString("jobinfo_field_executorFailRetryCount"))
                .append("：")
                .append(finalFailRetryCount);

        triggerMsgSb
                .append(
                        "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"
                                + I18nUtil.getInstance("").getString("jobconf_trigger_run")
                                + "<<<<<<<<<<< </span><br>")
                .append(
                        (routeAddressResult != null && routeAddressResult.getMessage() != null)
                                ? routeAddressResult.getMessage() + "<br><br>"
                                : "")
                .append(triggerResult.getMessage() != null ? triggerResult.getMessage() : "");

        // 6、save log trigger-info
        jobLog.setExecutorAddress(address);
        jobLog.setExecutorHandler(jobInfo.getExecutorHandler());
        jobLog.setExecutorParam(jobInfo.getExecutorParam());
        jobLog.setExecutorShardingParam(shardingParam);
        jobLog.setExecutorFailRetryCount(finalFailRetryCount);
        // jobLog.setTriggerTime();
        jobLog.setTriggerCode(triggerResult.getCode());
        jobLog.setTriggerMsg(triggerMsgSb.toString());
        ApplicationContextHolder.getBean(JobLogRepository.class).save(jobLog);
    }

    /**
     * run executor
     *
     * @param triggerParam
     * @param address
     * @return
     */
    public static Result<String> runExecutor(TriggerParam triggerParam, String address) {
        Result<String> runResult = null;
        try {
            Executor executorBiz = JobScheduler.getExecutorClient(address);
            var config = triggerParam.getExecutorParams();
            config.put("class", triggerParam.getExecutorHandler());
            config.put("strategy", triggerParam.getExecutorBlockStrategy());
            config.put("timeout", triggerParam.getExecutorTimeout() + "");
            runResult = executorBiz.run(triggerParam.getJobId(), config);
        } catch (Exception ex) {
            runResult = Result.error(ex);
        }

        StringBuffer runResultSB = new StringBuffer(I18nUtil.getInstance("").getString("jobconf_trigger_run") + "：");
        runResultSB.append("<br>address：").append(address);
        runResultSB.append("<br>code：").append(runResult.getCode());
        runResultSB.append("<br>msg：").append(runResult.getMessage());

        runResult.setMessage(runResultSB.toString());
        return runResult;
    }
}
