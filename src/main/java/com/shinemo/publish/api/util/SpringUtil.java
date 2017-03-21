/**
 * 
 */
package com.shinemo.publish.api.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author david
 *
 */
public class SpringUtil implements ApplicationContextAware{

	private static ApplicationContext context;
	public void setApplicationContext(ApplicationContext context2)
			throws BeansException {
		context = context2;
	}

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}
	
	public static String[] getBeans(){
		return context.getBeanDefinitionNames();
	}
}
