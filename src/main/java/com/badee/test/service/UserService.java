package com.badee.test.service;

import com.badee.spring.Component;

@Component("userService")
public class UserService {

    public String getName(){
        System.out.println("userService getName.............");
        return null;
    }
}
