package ltd.fdsa.job.admin.service.impl;


import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.core.job.coordinator.Coordinator;
import ltd.fdsa.ds.core.job.enums.HttpCode;
import ltd.fdsa.ds.core.job.model.HandleCallbackParam;
import ltd.fdsa.ds.core.model.NewService;
import ltd.fdsa.ds.core.model.Result;
import ltd.fdsa.ds.core.props.DefaultProps;
import ltd.fdsa.ds.core.util.I18nUtil;
import ltd.fdsa.job.admin.context.CoordinatorContext;
import ltd.fdsa.job.admin.jpa.entity.JobInfo;
import ltd.fdsa.job.admin.jpa.entity.JobLog;
import ltd.fdsa.job.admin.jpa.service.JobGroupService;
import ltd.fdsa.job.admin.jpa.service.JobInfoService;
import ltd.fdsa.job.admin.jpa.service.JobLogService;
import ltd.fdsa.job.admin.thread.JobTriggerPoolHelper;
import ltd.fdsa.job.admin.trigger.TriggerTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CoordinatorImpl implements Coordinator {


    @Resource
    public JobLogService JobLogDao;
    @Resource
    private JobInfoService JobInfoDao;

    @Resource
    private JobGroupService JobGroupDao;
    @Resource
    private CoordinatorContext context;

    @Override
    public Result<String> createProcess(Map<String, String> config) {
        var props = DefaultProps.fromMaps(config);
        var clazz = props.get("class");
        var name = props.get("name");
        var list = context.getExecutorsByProcess(clazz);
        var size = props.getInt("size", 1);
        if (size > list.size()) {
            size = list.size();
        }
        for (var i = 0; i < size; i++) {
            list.get(i).init(0L, config);
        }
        return null;
    }

    @Override
    public Result<String> callback(List<HandleCallbackParam> callbackParamList) {
        for (HandleCallbackParam handleCallbackParam : callbackParamList) {
            Result<String> callbackResult = callback(handleCallbackParam);
        }

        return Result.success();
    }

    private Result<String> callback(HandleCallbackParam handleCallbackParam) {
        // valid log item
        JobLog log = JobLogDao.findById(handleCallbackParam.getLogId()).get();
        if (log == null) {
            return Result.fail(5000, "log item not found.");
        }
        if (log.getHandleCode() > 0) {
            return Result.fail(5000, "log repeate callback."); // avoid repeat callback, trigger child job etc
        }

        // trigger success, to trigger child job
        String callbackMsg = null;
        if (handleCallbackParam.getExecuteResult().getCode() == 200) {
            JobInfo JobInfo = JobInfoDao.findById(log.getJobId()).get();
            if (JobInfo != null
                    && JobInfo.getChildJobId() != null
                    && JobInfo.getChildJobId().trim().length() > 0) {
                callbackMsg =
                        "<br><br><span style=\"color:#00c0ef;\" > >>>>>>>>>>>"
                                + I18nUtil.getString("jobconf_trigger_child_run")
                                + "<<<<<<<<<<< </span><br>";

                String[] childJobIds = JobInfo.getChildJobId().split(",");
                for (int i = 0; i < childJobIds.length; i++) {
                    int childJobId =
                            (childJobIds[i] != null
                                    && childJobIds[i].trim().length() > 0
                                    && isNumeric(childJobIds[i]))
                                    ? Integer.valueOf(childJobIds[i])
                                    : -1;
                    if (childJobId > 0) {

                        JobTriggerPoolHelper.trigger(childJobId, TriggerTypeEnum.PARENT, -1, null, null);
                        Result<String> triggerChildResult = Result.success();

                        // add msg
                        callbackMsg +=
                                MessageFormat.format(
                                        I18nUtil.getString("jobconf_callback_child_msg1"),
                                        (i + 1),
                                        childJobIds.length,
                                        childJobIds[i],
                                        (triggerChildResult.getCode() == HttpCode.OK.getCode()
                                                ? I18nUtil.getString("system_success")
                                                : I18nUtil.getString("system_fail")),
                                        triggerChildResult.getMessage());
                    } else {
                        callbackMsg +=
                                MessageFormat.format(
                                        I18nUtil.getString("jobconf_callback_child_msg2"),
                                        (i + 1),
                                        childJobIds.length,
                                        childJobIds[i]);
                    }
                }
            }
        }

        // handle msg
        StringBuffer handleMsg = new StringBuffer();
        if (log.getHandleMsg() != null) {
            handleMsg.append(log.getHandleMsg()).append("<br>");
        }
        if (handleCallbackParam.getExecuteResult().getMessage() != null) {
            handleMsg.append(handleCallbackParam.getExecuteResult().getMessage());
        }
        if (callbackMsg != null) {
            handleMsg.append(callbackMsg);
        }

        // success, save log
        log.setHandleTime(new Date());
        log.setHandleCode(handleCallbackParam.getExecuteResult().getCode());
        log.setHandleMsg(handleMsg.toString());
        JobLogDao.update(log);

        return Result.success();
    }

    private boolean isNumeric(String str) {
        try {
            int result = Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Result<String> registry(NewService newService) {
        // valid
        if (!StringUtils.hasText(newService.getName())
                || !StringUtils.hasText(newService.getId())
                || !StringUtils.hasText(newService.getValue())) {
            return Result.fail(500, "Illegal Argument.");
        }
        // process
        if (this.context.registry(newService) != null) {
            return Result.success();
        }
        // return
        return Result.fail(500, "failed");
    }

    @Override
    public Result<String> unRegistry(NewService newService) {

        // valid
        if (!StringUtils.hasText(newService.getName())
                || !StringUtils.hasText(newService.getId())
                || !StringUtils.hasText(newService.getValue())) {
            return Result.fail(5000, "Illegal Argument.");
        }
        // process
        if (this.context.unRegistry(newService) != null) {
            return Result.success();
        }
        // return
        return Result.fail(500, "failed");
    }

}
