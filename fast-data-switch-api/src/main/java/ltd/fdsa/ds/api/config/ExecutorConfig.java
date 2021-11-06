package ltd.fdsa.ds.api.config;

import ltd.fdsa.ds.api.job.executor.Executor;
import ltd.fdsa.ds.api.job.executor.ExecutorImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;

@Configuration
class HessianExecutorConfig {

    @Bean("/executor")
    public HessianServiceExporter exportExecutorHessian() {
        ExecutorImpl executor = new ExecutorImpl();
        HessianServiceExporter exporter = new HessianServiceExporter();
        exporter.setService(executor);
        exporter.setServiceInterface(Executor.class);
        return exporter;
    }
}
