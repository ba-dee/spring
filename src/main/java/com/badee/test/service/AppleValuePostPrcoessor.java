package com.badee.test.service;

import com.badee.spring.AppleValue;
import com.badee.spring.BeanPostProcessor;
import com.badee.spring.Component;

import java.lang.reflect.Field;

@Component
public class AppleValuePostPrcoessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        for (Field field : bean.getClass().getDeclaredFields()) {
            if(field.isAnnotationPresent(AppleValue.class)){
                field.setAccessible(true);
                try{
                    field.set(bean,field.getAnnotation(AppleValue.class).value());
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
        return bean;
    }
}
