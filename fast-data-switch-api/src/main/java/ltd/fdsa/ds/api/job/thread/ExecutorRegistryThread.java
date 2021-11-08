package ltd.fdsa.ds.api.job.thread;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import ltd.fdsa.ds.api.container.PluginManager;
import ltd.fdsa.ds.api.job.coordinator.Coordinator;
import ltd.fdsa.ds.api.job.enums.RegistryConfig;
import ltd.fdsa.ds.api.model.NewService;
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
    private final PluginManager pluginManager;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicReference<BigInteger> catalogServicesIndex = new AtomicReference<>();
    private ScheduledFuture<?> serviceWatchFuture;

    public ExecutorRegistryThread(Properties properties, Coordinator client, TaskScheduler taskScheduler, PluginManager pluginManager) {
        this.properties = properties;
        this.taskScheduler = taskScheduler;
        this.client = client;
        this.pluginManager = pluginManager;
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
            var list = this.pluginManager.getPlugins();

            var address = this.properties.getProperty("address", "");
            NewService newService = NewService.builder()
                    .url(address)
                    .id(RegistryConfig.EXECUTOR.name())
                    .name(appName)
                    .handles(list)
                    .build();


            try {
                Result<String> registryResult = client.registry(newService);
                if (registryResult != null && Result.OK == registryResult.getCode()) {
                    log.info("project.registry success, registryParam:{}, registryResult:{}", new Object[]{newService, registryResult});
                } else {
                    log.warn("project.registry fail, registryParam:{},registryResult:{}", new Object[]{newService, registryResult});
                }
            } catch (Exception e) {
                log.error("project.registry error, registryParam:{}", newService, e);
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
                    NewService registryParam = NewService.builder()
                            .id(RegistryConfig.EXECUTOR.name())
                            .name(appName)
                            .url(address)
                            .build();
                    try {
                        Result<String> registryResult = client.unRegistry(registryParam);
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


