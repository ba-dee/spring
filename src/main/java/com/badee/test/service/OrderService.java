package com.badee.test.service;

import com.badee.spring.Component;
import com.badee.spring.InitializingBean;

@Component
public class OrderService implements InitializingBean  {

    public void afterPropertiesSet() {

        System.out.println("初始化完成");
    }

    public void order(){
        System.out.println("order...........");
    }
}
