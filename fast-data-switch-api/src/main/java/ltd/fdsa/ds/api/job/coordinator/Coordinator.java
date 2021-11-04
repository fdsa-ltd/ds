package ltd.fdsa.ds.api.job.coordinator;

import ltd.fdsa.ds.api.job.model.HandleCallbackParam;
import ltd.fdsa.ds.api.job.model.RegistryParam;
import ltd.fdsa.ds.api.model.Result;


import java.util.List;
/**
 * 客户端调用服务端的接口定义
 */

public interface Coordinator {

    // ---------------------- callback ----------------------

    /**
     * callback
     *
     * @param callbackParamList
     * @return
     */
    public Result<String> callback(List<HandleCallbackParam> callbackParamList);

    // ---------------------- registry ----------------------

    /**
     * 客户端将自己的Handler 注册到服务中心
     *
     * @param registryParam
     * @return
     */
    public Result<String> registry(RegistryParam registryParam);

    /**
     * registry remove
     *
     * @param registryParam
     * @return
     */
    public Result<String> registryRemove(RegistryParam registryParam);
}
