package com.shinemo.publish.utils;

import static org.apache.commons.lang.Validate.notEmpty;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    private final static Logger log = LoggerFactory.getLogger(SpringContextHolder.class);
    
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
    	SpringContextHolder.context = context;
    }

    public static Object getSpringBean(String beanName) {
        notEmpty(beanName, "bean name is required");
        return context==null?null:context.getBean(beanName);
    }
    
    public static <T> T getBean(String beanName, Class<T> clazz) {
        return context.getBean(beanName, clazz);
    }

    public static String[] getBeanDefinitionNames() {
        return context.getBeanDefinitionNames();
    }
    
    
    
    public static void toLog(){
        String instanceNamesForCater[] = getBeanDefinitionNames();
        int countBeans = instanceNamesForCater.length;
        log.warn("==========Cater System Beans Count:" + countBeans + "==========");
        for(int i = 0 ; i <countBeans ; i++){
            log.warn("num:" + (i+1) + "\t" + instanceNamesForCater[i]);
        }
        log.warn("==========Count Finished !!!==========");
    }
}

