package ltd.fdsa.ds.api.job.thread;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.job.coordinator.Coordinator;
import ltd.fdsa.ds.api.job.enums.RegistryConfig;
import ltd.fdsa.ds.api.job.model.RegistryParam;
import ltd.fdsa.ds.api.model.Result;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.TaskScheduler;

import java.math.BigInteger;
import java.util.Properties;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class ExecutorRegistryThread implements SmartLifecycle {
    private final Properties properties;
    private final TaskScheduler taskScheduler;
    private final Coordinator client;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicReference<BigInteger> catalogServicesIndex = new AtomicReference<>();
    private ScheduledFuture<?> serviceWatchFuture;

    public ExecutorRegistryThread(Properties properties, Coordinator client, TaskScheduler taskScheduler) {
        this.properties = properties;
        this.taskScheduler = taskScheduler;
        this.client = client;
    }

    @Override
    public void start() {
        if (this.running.compareAndSet(false, true)) {
            serviceRegister();
        }
    }

    void serviceRegister() {
        //判断服务是否已经启动
        if (!this.running.get()) {
            return;
        }
        try {
            var appName = this.properties.getProperty("name");
            if (appName == null || appName.trim().length() == 0) {
                log.warn("project.executor registry config fail, appName is null.");
                return;
            }


            var address = this.properties.getProperty("address", "");
            RegistryParam registryParam = new RegistryParam(RegistryConfig.EXECUTOR.name(), appName, address);

            try {
                Result<String> registryResult = client.registry(registryParam);
                if (registryResult != null && Result.success().getCode() == registryResult.getCode()) {
                    registryResult = Result.success();
                    log.debug("project.registry success, registryParam:{}, registryResult:{}", new Object[]{registryParam, registryResult});

                } else {
                    log.info("project.registry fail, registryParam:{},registryResult:{}", new Object[]{registryParam, registryResult});
                }
            } catch (Exception e) {
                log.info("project.registry error, registryParam:{}", registryParam, e);
            }

        } catch (Exception e) {
            log.error("Error Consul register", e);
        }
    }


    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return this.running.get();
    }

    @Override
    public int getPhase() {
        return 0;
    }

    @Override
    public void stop(Runnable callback) {
        this.stop();
        callback.run();
    }


    @Override
    public void stop() {
        if (this.running.compareAndSet(true, false)) {
            if (this.serviceWatchFuture != null) {
                this.serviceWatchFuture.cancel(true);

                // registry remove
                var appName = this.properties.getProperty("name");
                var address = this.properties.getProperty("address");
                try {
                    RegistryParam registryParam = new RegistryParam(RegistryConfig.EXECUTOR.name(), appName, address);

                    try {
                        Result<String> registryResult = client.registryRemove(registryParam);
                        if (registryResult != null
                                && Result.success().getCode() == registryResult.getCode()) {
                            registryResult = Result.success();
                            log.info("project.registry - remove success, registryParam:{},registryResult:{}",
                                    new Object[]{registryParam, registryResult});

                        } else {
                            log.info("project.registry - remove fail, registryParam:{},registryResult:{}",
                                    new Object[]{registryParam, registryResult});
                        }
                    } catch (Exception e) {
                        log.info("project.registry - remove error, registryParam:{}", registryParam, e);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                log.info("project.executor registry thread destory.");
            }
        }
    }
}


