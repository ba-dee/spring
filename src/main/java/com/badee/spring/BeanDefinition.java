package com.badee.spring;

public class BeanDefinition {
    Class type;
    String scope;

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }


    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
