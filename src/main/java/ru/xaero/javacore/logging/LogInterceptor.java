package ru.xaero.javacore.logging;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Loggable
@Interceptor
public class LogInterceptor {

    @AroundInvoke
    public Object intercept(final InvocationContext context) throws Exception {
        System.out.println(context.getTarget().getClass().getSimpleName() + ":" + context.getMethod().getName());
        return context.proceed();
    }
}
