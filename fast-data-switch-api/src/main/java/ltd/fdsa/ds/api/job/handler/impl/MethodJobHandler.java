package ltd.fdsa.ds.api.job.handler.impl;

import lombok.extern.slf4j.Slf4j;
import ltd.fdsa.ds.api.model.Record;
import ltd.fdsa.ds.api.pipeline.Process;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class MethodJobHandler implements Process {

    private final Object target;
    private final Method method;
    private Method initMethod;
    private Method destroyMethod;

    public MethodJobHandler(Object target, Method method, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.method = method;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }


    @Override
    public void init() {
        if (initMethod != null) {
            try {
                initMethod.invoke(target);
            } catch (IllegalAccessException e) {
                log.error("", e);
            } catch (InvocationTargetException e) {
                log.error("", e);
            }
        }
    }

    @Override
    public void execute(Record... records) {
        Arrays.stream(records)
                .map(m -> m.toNormalMap())
                .forEach(context -> {
                    try {
                        method.invoke(target, context.values().toArray());
                    } catch (IllegalAccessException e) {
                        log.error("", e);
                    } catch (InvocationTargetException e) {
                        log.error("", e);
                    }
                });

    }

    @Override
    public void stop() {
        if (destroyMethod != null) {
            try {
                destroyMethod.invoke(target);
            } catch (IllegalAccessException e) {
                log.error("", e);
            } catch (InvocationTargetException e) {
                log.error("", e);
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + "[" + target.getClass() + "#" + method.getName() + "]";
    }
}
