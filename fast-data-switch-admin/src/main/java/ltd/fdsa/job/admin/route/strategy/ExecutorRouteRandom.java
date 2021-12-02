package ltd.fdsa.job.admin.route.strategy;

import ltd.fdsa.job.admin.route.ExecutorRouter;
import ltd.fdsa.ds.core.model.Result;
import ltd.fdsa.ds.core.job.model.TriggerParam;


import java.util.List;
import java.util.Random;

public class ExecutorRouteRandom extends ExecutorRouter {

    private static Random localRandom = new Random();

    @Override
    public Result<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = addressList.get(localRandom.nextInt(addressList.size()));
        return Result.success(address);
    }
}
