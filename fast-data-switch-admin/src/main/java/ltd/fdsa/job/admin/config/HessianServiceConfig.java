package ltd.fdsa.job.admin.config;

import ltd.fdsa.ds.api.job.coordinator.Coordinator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

import javax.annotation.Resource;

/**
 * <用于配置发布hessian接口>
 * <这里只是做了最简单的配置，还可以设置超时时间，密码这些安全参数>
 * @author wzh
 * @version 2018-11-18 16:55
 * @see [相关类/方法] (可选)
 **/
@Configuration //标记为spring 配置类
public class HessianServiceConfig {

    @Resource
    private Coordinator coordinator;

    /**
     * 1. HessianServiceExporter是由Spring.web框架提供的Hessian工具类，能够将bean转化为Hessian服务
     * 2. @Bean(name = "/helloHessian.do")加斜杠方式会被spring暴露服务路径,发布服务。
     * @return
     */
    @Bean("/coordinator")
    public HessianServiceExporter exportHelloHessian()
    {
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(coordinator);
        exporter.setServiceInterface(Coordinator.class);
        return exporter;
    }
}
