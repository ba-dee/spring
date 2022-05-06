package com.badee.spring;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class AnnotationConfigApplicationContext {
    Class configClass;
    Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();
    Map<String, Object> singletonObjects = new HashMap<String, Object>();

    public AnnotationConfigApplicationContext(Class configClass) {
        this.configClass = configClass;

        if (!this.configClass.isAnnotationPresent(ComponentScan.class)) {
            throw new NullPointerException();
        }
        //扫描生成BeanDefinition 缓冲到 BeanDefinitionMap中
        scan(configClass);
        for (Map.Entry<String, BeanDefinition> stringBeanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            String beanName = stringBeanDefinitionEntry.getKey();
            BeanDefinition beanDefinition = stringBeanDefinitionEntry.getValue();
            if (beanDefinition.getScope().equals("singleton")) {
                Object object = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, object);
            }
        }
    }

    private void scan(Class configClass) {
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
        String scanPath = componentScanAnnotation.value().replace(".", "/");
        URL path = this.configClass.getClassLoader().getResource(scanPath);
        File dir = new File(path.getPath());
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                try {
                    String p = file.getAbsolutePath();
                    String cp = p.substring(p.indexOf("com"), p.indexOf(".class")).replace("\\", ".");
                    Class<?> aClass = this.configClass.getClassLoader().loadClass(cp);
                    if (aClass.isAnnotationPresent(Component.class)) {
                        Component componentAnnotation = aClass.getAnnotation(Component.class);
                        String beanName = componentAnnotation.value();

                        Scope scopeAnnotation = aClass.getAnnotation(Scope.class);

                        BeanDefinition beanDefinition = new BeanDefinition();
                        if (scopeAnnotation != null) {
                            beanDefinition.setScope(scopeAnnotation.value());
                        } else {
                            beanDefinition.setScope("singleton");
                        }
                        beanDefinition.setType(aClass);
                        beanDefinitionMap.put(beanName, beanDefinition);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }


    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        try {
            //TODO:
            Object bean = beanDefinition.getType().newInstance();
            return bean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        }
        if (beanDefinition.getScope() == "singleton") {
            return singletonObjects.get(beanName);
        } else {
            return createBean(beanName, beanDefinition);
        }
    }
}
