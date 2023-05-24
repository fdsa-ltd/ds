package cn.zhumingwu.dataswitch.client.config;

import cn.zhumingwu.dataswitch.core.job.coordinator.Coordinator;
import cn.zhumingwu.dataswitch.client.utils.HessianProxyFactoryUtil;
import org.springframework.beans.factory.annotation.Value;

public class HessianClientConfig {

    @Value("rpc.host")
    String url;

    public Coordinator coordinator() {
        try {
            return HessianProxyFactoryUtil.getHessianClientBean(Coordinator.class, url);
        } catch (Exception e) {
            return null;
        }
    }

}
