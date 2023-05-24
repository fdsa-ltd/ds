package cn.zhumingwu.dataswitch.admin.route.strategy;

import cn.zhumingwu.dataswitch.admin.scheduler.JobScheduler;
import cn.zhumingwu.dataswitch.admin.route.ExecutorRouter;
import cn.zhumingwu.dataswitch.core.job.executor.Executor;
import cn.zhumingwu.dataswitch.core.model.Result;
import cn.zhumingwu.dataswitch.core.job.model.TriggerParam;
import cn.zhumingwu.dataswitch.core.util.I18nUtil;


import java.util.List;

public class ExecutorRouteFailover extends ExecutorRouter {

    @Override
    public Result<String> route(TriggerParam triggerParam, List<String> addressList) {

        StringBuffer beatResultSB = new StringBuffer();
        for (String address : addressList) {
            // beat
            Result<String> beatResult = null;
            try {
                Executor executorBiz = JobScheduler.getExecutorClient(address);
                beatResult = executorBiz.beat();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                beatResult = Result.fail(500, "" + e);
            }
            beatResultSB
                    .append((beatResultSB.length() > 0) ? "<br><br>" : "")
                    .append(I18nUtil.getInstance("").getString("jobconf_beat") + "：")
                    .append("<br>address：")
                    .append(address)
                    .append("<br>code：")
                    .append(beatResult.getCode())
                    .append("<br>msg：")
                    .append(beatResult.getMessage());

            // beat success
            if (beatResult.getCode() == Result.success().getCode()) {

                beatResult.setMessage(beatResultSB.toString());
                beatResult.setData(address);
                return beatResult;
            }
        }
        return Result.fail(500, beatResultSB.toString());
    }
}
