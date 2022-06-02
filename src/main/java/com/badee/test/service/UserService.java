package com.badee.test.service;

import com.badee.spring.AppleValue;
import com.badee.spring.Autowired;
import com.badee.spring.Component;

@Component("userService")
public class UserService  implements ProxyInterface{

    @Autowired
    private  OrderService orderService;

    @AppleValue("apple")
    String value;

    @Override
    public String getName(){
        System.out.println("my name is apple............");
        return "apple";
    }

    public String stdValue(){
        System.out.println(value);
        return null;
    }
}
