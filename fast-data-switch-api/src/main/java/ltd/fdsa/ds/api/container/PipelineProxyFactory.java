package ltd.fdsa.ds.api.container;

import com.google.common.base.Strings;
import lombok.var;
import ltd.fdsa.ds.api.props.Configuration;
import ltd.fdsa.ds.api.constant.Constants;
import ltd.fdsa.ds.api.pipeline.Pipeline;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PipelineProxyFactory {
    // step 1 初始化引擎,扫描配置文件中的插件
    static final PluginManager pluginManager = PluginManager.getDefaultInstance();

    static {

        pluginManager.scan("./plugins");
    }
    public static Pipeline getProxy(Configuration config, Object pipeline) {
        var interceptor = new PipelineInterceptor(config, pipeline);
        return ProxyFactory.getProxy(Pipeline.class, interceptor);
    }

    static class PipelineInterceptor implements MethodInterceptor {
        private final Configuration config;
        private final Object pipeline;
        private final String name;
        private final PluginType type;
        private final String description;
        protected final AtomicBoolean running = new AtomicBoolean(false);
        protected final List<Pipeline> nextSteps = new LinkedList<>();

        public PipelineInterceptor(Configuration config, Object pipeline) {
            this.config = config;
            this.pipeline = pipeline;
            this.name = this.config.getString(Constants.JOB_NAME, Constants.JOB_NAME_DEFAULT);
            this.type = PluginType.valueOf(this.config.getString(Constants.PIPELINE_TYPE, Constants.PIPELINE_TYPE_DEFAULT));
            this.description = this.config.getString(Constants.JOB_DESCRIPTION, "");
            var configurations = this.config.getConfigurations(Constants.PIPELINE_NODES);
            if (configurations != null && configurations.length > 0) {
                loadPipelines(configurations);
            }
        }


        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            var method = methodInvocation.getMethod();
            var arguments = methodInvocation.getArguments();
            switch (method.getName()) {
                case "config":
                    return this.config;
                case "start":
                    if (this.running.compareAndSet(false, true)) {
                        for (var pipeline : this.nextSteps) {
                            pipeline.start();
                        }
                        return method.invoke(this.pipeline, arguments);
                    }
                    return null;
                case "stop":
                    if (this.running.compareAndSet(true, false)) {
                        method.invoke(this.pipeline, arguments);
                        for (var pipeline : this.nextSteps) {
                            pipeline.stop();
                        }
                    }
                    return null;
                case "isRunning":
                    return this.running.get();
                case "nextSteps":
                    return this.nextSteps;
                default:
                    return method.invoke(this.pipeline, arguments);
            }

        }


        private void loadPipelines(Configuration[] configurations) {
            for (var configuration : configurations) {
                var className = configuration.get(Constants.PIPELINE_CLASS_NAME);
                if (Strings.isNullOrEmpty(className)) {
                    continue;
                }
                try (ClassLoaderSwapper classLoaderSwapper = ClassLoaderSwapper.createClassLoaderSwapper()) {
                    classLoaderSwapper.newClassLoader(pluginManager.getClassLoader(className));
                    var pipeline =pluginManager.getInstance(className, Pipeline.class);
                    if (pipeline == null) {
                        continue;
                    }
                    configuration.set(Constants.JOB_NAME, this.name + "." + configuration.getString(Constants.JOB_NAME, Constants.JOB_NAME_DEFAULT));
                    pipeline.init();
                    this.nextSteps.add(pipeline);
                    classLoaderSwapper.restoreClassLoader();
                }
            }
        }
    }
}
