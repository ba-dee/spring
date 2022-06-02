package com.badee.test;

import com.badee.spring.AnnotationConfigApplicationContext;
import com.badee.test.service.BadeeBeanPostProcessor;
import com.badee.test.service.OrderService;
import com.badee.test.service.ProxyInterface;
import com.badee.test.service.UserService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Test {
    public static void main(String[] args) {


        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Appconfig.class);
        ProxyInterface userService =  (ProxyInterface) context.getBean("userService");

        OrderService orderService =  (OrderService) context.getBean("orderService");

        orderService.order();

        userService.getName();
        userService.stdValue();


        UserService te = new UserService();
        System.getProperties().put("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");

        ProxyInterface proxy  = (ProxyInterface)Proxy.newProxyInstance(BadeeBeanPostProcessor.class.getClassLoader(),
                te.getClass().getInterfaces(), new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        System.out.println("开启事务.........................");
                        Object rs = method.invoke(te,args);
                        System.out.println("关闭事务.........................");
                        return  rs;
                    }
                });

        proxy.sa


    }
}
