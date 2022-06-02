package com.badee.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnnotationConfigApplicationContext {
    Class configClass;
    Map<String, BeanDefinition> beanDefinitionMap = new HashMap<String, BeanDefinition>();
    Map<String, Object> singletonObjects = new HashMap<String, Object>();
    List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public AnnotationConfigApplicationContext(Class configClass) {
        this.configClass = configClass;

        if (!this.configClass.isAnnotationPresent(ComponentScan.class)) {
            throw new NullPointerException();
        }
        //扫描生成BeanDefinition 缓冲到 BeanDefinitionMap中
        scan(configClass);

        //创建bean
        for (Map.Entry<String, BeanDefinition> stringBeanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
            //创建bean
            String beanName = stringBeanDefinitionEntry.getKey();
            getBean(beanName);
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

                        //如果实现了beanPostProcessor
                        if(BeanPostProcessor.class.isAssignableFrom(aClass)){

                            BeanPostProcessor postProcessor  = (BeanPostProcessor)aClass.getConstructor().newInstance();
                            beanPostProcessorList.add(postProcessor);
                        }
                        Component componentAnnotation = aClass.getAnnotation(Component.class);
                        String beanName = componentAnnotation.value();

                        if ("".equals(beanName)) {
                            beanName = Introspector.decapitalize(aClass.getSimpleName());
                        }

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
            //创建bean
            Object bean = beanDefinition.getType().newInstance();

            //自动装配
            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    field.set(bean,getBean(field.getName()));
                }
            }

            //前置处理
            for (BeanPostProcessor postProcessor : beanPostProcessorList) {
               System.out.println(postProcessor.getClass().getName());
               bean = postProcessor.postProcessBeforeInitialization(bean,beanName);
            }
            //初始化
            if(bean instanceof InitializingBean){
                ((InitializingBean) bean).afterPropertiesSet();
            }

            //前置处理
            for (BeanPostProcessor postProcessor : beanPostProcessorList) {
               bean =  postProcessor.postProcessAfterInitialization(bean,beanName);
            }
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
        if ("".equals(beanDefinition.getScope()) || beanDefinition.getScope() == "singleton") {
            Object bean = singletonObjects.get(beanName);
            if (bean == null) {

                bean = createBean(beanName, beanDefinition);
                singletonObjects.put(beanName, bean);
            }
            return bean;

        } else {
            return createBean(beanName, beanDefinition);
        }
    }
}
