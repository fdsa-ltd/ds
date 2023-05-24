package cn.zhumingwu.dataswitch.core.config;

import cn.zhumingwu.dataswitch.core.container.PluginManager;
import cn.zhumingwu.dataswitch.core.job.coordinator.Coordinator;
import cn.zhumingwu.dataswitch.core.job.executor.Executor;
import cn.zhumingwu.dataswitch.core.job.executor.ExecutorImpl;
import cn.zhumingwu.dataswitch.core.job.thread.ExecutorRegistryThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.scheduling.TaskScheduler;

import java.util.Properties;

@Configuration
@Slf4j
public class ExecutorConfig {

    @Bean("/executor")
    public HessianServiceExporter exportExecutorHessian() {
        ExecutorImpl executor = new ExecutorImpl();
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(executor);
        exporter.setServiceInterface(Executor.class);
        return exporter;
    }

    @Bean
    public ExecutorRegistryThread executorRegistry(Properties properties, Coordinator client, TaskScheduler taskScheduler, PluginManager pluginManager) {
        log.info("ConsulWatch Started");
        return new ExecutorRegistryThread(properties, client, taskScheduler, pluginManager);
    }
}
