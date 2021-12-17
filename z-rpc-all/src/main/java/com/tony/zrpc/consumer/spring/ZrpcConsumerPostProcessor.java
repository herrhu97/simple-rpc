package com.tony.zrpc.consumer.spring;

import com.tony.zrpc.consumer.annotation.ZRpcReference;
import com.tony.zrpc.consumer.proxy.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;

/**
 * 对标注{@ZRpcReference}的field进行用动态代理生成的对象注入
 * @author herrhu
 * @date 2021/12/17 11:30
 **/
public class ZrpcConsumerPostProcessor implements ApplicationContextAware, InstantiationAwareBeanPostProcessor {
    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 每一个bean在初始化完毕之后，交给这个方法进行再次处理
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(ZRpcReference.class)) {
                continue;
            }

            ZRpcReference zRpcReference = field.getAnnotation(ZRpcReference.class);

            Object proxy = ProxyFactory.getProxy(new Class[]{field.getType()});

            field.setAccessible(true);
            try {
                field.set(bean, proxy);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return false;
    }
}
