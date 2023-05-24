package cn.zhumingwu.dataswitch.admin.route.strategy;

import cn.zhumingwu.dataswitch.admin.route.ExecutorRouter;
import cn.zhumingwu.dataswitch.core.model.Result;
import cn.zhumingwu.dataswitch.core.job.model.TriggerParam;


import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ExecutorRouteRound extends ExecutorRouter {

    private static ConcurrentMap<Integer, Integer> routeCountEachJob =
            new ConcurrentHashMap<Integer, Integer>();
    private static long CACHE_VALID_TIME = 0;

    private static int count(int jobId) {
        // cache clear
        if (System.currentTimeMillis() > CACHE_VALID_TIME) {
            routeCountEachJob.clear();
            CACHE_VALID_TIME = System.currentTimeMillis() + 1000 * 60 * 60 * 24;
        }

        // count++
        Integer count = routeCountEachJob.get(jobId);
        count =
                (count == null || count > 1000000)
                        ? (new Random().nextInt(100))
                        : ++count; // 初始化时主动Random一次，缓解首次压力
        routeCountEachJob.put(jobId, count);
        return count;
    }

    @Override
    public Result<String> route(TriggerParam triggerParam, List<String> addressList) {
        String address = addressList.get(count(triggerParam.getJobId()) % addressList.size());
        return Result.success(address);
    }
}
