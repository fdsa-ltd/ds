package ltd.fdsa.ds.api.job.handler.impl;

import ltd.fdsa.ds.api.job.handler.JobHandler;
import ltd.fdsa.ds.api.job.log.JobLogger;
import ltd.fdsa.ds.api.model.Result;
import ltd.fdsa.ds.api.util.ShardingUtil;


import java.util.Map;

public class ShardingJobHandler implements JobHandler {

    @Override
    public Result<Object> execute(Map<String, String> context) {
        // 分片参数
        ShardingUtil.ShardingVO shardingVO = ShardingUtil.getShardingVo();
        JobLogger.log("分片参数：当前分片序号 = {}, 总分片数 = {}", shardingVO.getIndex(), shardingVO.getTotal());

        // 业务逻辑
        for (int i = 0; i < shardingVO.getTotal(); i++) {
            if (i == shardingVO.getIndex()) {
                JobLogger.log("第 {} 片, 命中分片开始处理", i);
            } else {
                JobLogger.log("第 {} 片, 忽略", i);
            }
        }

        return SUCCESS;
    }
}
