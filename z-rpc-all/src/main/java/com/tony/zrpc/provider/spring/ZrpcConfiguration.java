package com.tony.zrpc.provider.spring;

import com.tony.zrpc.provider.config.RegistryConfig;
import com.tony.zrpc.provider.config.ServerConfig;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.reflect.Field;

/**
 * 实现ServerConfig与RegistryConfig在Spring中BeanDefinition的生成
 * @author herrhu
 * @date 2021/12/17 19:48
 **/
public class ZrpcConfiguration implements ImportBeanDefinitionRegistrar {
    StandardEnvironment environment;

    public ZrpcConfiguration(Environment environment) {
        this.environment = (StandardEnvironment) environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
        BeanDefinitionBuilder beanDefinitionBuilder = null;
        beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(ServerConfig.class);
        for (Field field : ServerConfig.class.getDeclaredFields()) {
            String value = environment.getProperty("zrpc.server." + field.getName());
            beanDefinitionBuilder.addPropertyValue(field.getName(), value);
        }
        registry.registerBeanDefinition("serverConfig", beanDefinitionBuilder.getBeanDefinition());

        beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(RegistryConfig.class);
        for (Field field : RegistryConfig.class.getDeclaredFields()) {
            String value = environment.getProperty("zrpc.registry." + field.getName());
            beanDefinitionBuilder.addPropertyValue(field.getName(), value);
        }
        registry.registerBeanDefinition("registryConfig", beanDefinitionBuilder.getBeanDefinition());

    }
}
