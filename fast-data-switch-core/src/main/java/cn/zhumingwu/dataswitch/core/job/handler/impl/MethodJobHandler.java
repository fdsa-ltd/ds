package cn.zhumingwu.dataswitch.core.job.handler.impl;

import cn.zhumingwu.dataswitch.core.job.handler.IJobHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class MethodJobHandler extends IJobHandler {

    private final Object target;
    private final Method executeMethod;
    private final Method initMethod;
    private final Method destroyMethod;

    public MethodJobHandler(Object target, Method executeMethod, Method initMethod, Method destroyMethod) {
        this.target = target;
        this.executeMethod = executeMethod;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
    }


    @Override
    public void init() throws Exception {
        if (initMethod != null) {
            initMethod.invoke(target);
        }
    }

    @Override
    public void execute() throws Exception {
        if (executeMethod != null) {
            executeMethod.invoke(target);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (destroyMethod != null) {
            destroyMethod.invoke(target);
        }
    }

    @Override
    public String toString() {
        return super.toString() + "[" + target.getClass() + "#" + executeMethod.getName() + "]";
    }
}
