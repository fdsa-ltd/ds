package ltd.fdsa.ds.api.container;

import ltd.fdsa.ds.api.pipeline.Pipeline;
import org.aopalliance.intercept.*;
import org.springframework.aop.framework.ProxyFactory;

public class PipelineProxyFactory {


    public Pipeline getProxy() {
    return     ProxyFactory.getProxy(Pipeline.class, new Interceptor() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        });
    }
    public  class PipelineInterceptor implements ConstructorInterceptor, MethodInterceptor
    {
        @Override
        public Object construct(ConstructorInvocation constructorInvocation) throws Throwable {
            return null;
        }
        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            return null;
        }


    }
}
