package com.badee.test.service;

import com.badee.spring.BeanPostProcessor;
import com.badee.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class BadeeBeanPostProcessor  implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {


        if(beanName.equals("userService")){
            System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");

            Object proxy  = Proxy.newProxyInstance(BadeeBeanPostProcessor.class.getClassLoader(),
                    bean.getClass().getInterfaces(), new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("开启事务.........................");
                     Object rs = method.invoke(bean,args);
                    System.out.println("关闭事务.........................");
                    return  rs;
                }
            });

            return proxy;
        }
        return bean;
    }
}
