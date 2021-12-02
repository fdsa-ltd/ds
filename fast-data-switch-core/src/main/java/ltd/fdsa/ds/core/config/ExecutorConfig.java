package ltd.fdsa.ds.core.config;

import lombok.extern.slf4j.Slf4j;
import ltd.fdsa.ds.core.container.PluginManager;
import ltd.fdsa.ds.core.job.coordinator.Coordinator;
import ltd.fdsa.ds.core.job.executor.Executor;
import ltd.fdsa.ds.core.job.executor.ExecutorImpl;
import ltd.fdsa.ds.core.job.thread.ExecutorRegistryThread;
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
