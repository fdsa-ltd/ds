package ltd.fdsa.job.client.config;

import ltd.fdsa.ds.core.job.coordinator.Coordinator;
import ltd.fdsa.job.client.utils.HessianProxyFactoryUtil;
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
