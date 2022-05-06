package com.badee.test;

import com.badee.spring.AnnotationConfigApplicationContext;
import com.badee.test.service.UserService;

public class Test {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Appconfig.class);
        UserService userService = (UserService) context.getBean("userService");
        userService.getName();
    }
}
