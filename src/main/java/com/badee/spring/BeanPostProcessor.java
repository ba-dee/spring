package com.badee.spring;

public interface BeanPostProcessor {
    //前置处理器
    default Object postProcessBeforeInitialization(Object bean,String beanName){
        return bean;
    }

    //后置处理器
    default Object postProcessAfterInitialization(Object bean,String beanName){
        return bean;
    }
}
