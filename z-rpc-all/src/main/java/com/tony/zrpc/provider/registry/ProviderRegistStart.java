package com.tony.zrpc.provider.registry;

import com.tony.zrpc.provider.config.RegistryConfig;
import com.tony.zrpc.provider.config.ServerConfig;
import com.tony.zrpc.provider.config.ZrpcProviderContext;
import com.tony.zrpc.registry.redis.RedisRegistry;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * 启动redisRegistry
 */
@Component
public class ProviderRegistStart implements SmartApplicationListener, ApplicationContextAware {
    // 指定  哪些事件下会被触发
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return  eventType == ContextStartedEvent.class;
    }

    private  static RedisRegistry redisRegistry = new RedisRegistry();

    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        try {
            // 获取所有 service 信息， 挨个注册到 注册中心
            RegistryConfig registryConfig = applicationContext.getBean(RegistryConfig.class);
            ServerConfig serverConfig = applicationContext.getBean(ServerConfig.class);
            redisRegistry.init(new URI(registryConfig.getAddress()));
            // 遍历 - service这么多》哪些需要对外暴露？
            for (String serviceName : ZrpcProviderContext.serviceMap.keySet()) {
                URI serviceURI = new URI("//" + serverConfig.getHost() + ":" + serverConfig.getPort()
                +"/" + serviceName + "/");
                redisRegistry.register(serviceURI);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return 9999;
    }

    ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
