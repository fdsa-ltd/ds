package ltd.fdsa.job.admin.route.strategy;

import ltd.fdsa.job.admin.route.ExecutorRouter;
import ltd.fdsa.ds.core.model.Result;
import ltd.fdsa.ds.core.job.model.TriggerParam;


import java.util.List;

public class ExecutorRouteFirst extends ExecutorRouter {

    @Override
    public Result<String> route(TriggerParam triggerParam, List<String> addressList) {
        return Result.success(addressList.get(0));
    }
}
