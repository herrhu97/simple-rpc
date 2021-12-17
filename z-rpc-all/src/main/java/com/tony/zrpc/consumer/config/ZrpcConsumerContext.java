package com.tony.zrpc.consumer.config;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * 上下文，保留了系统里面所有依赖的服务信息
 * @author herrhu
 * @date 2021/12/17 20:42
 **/
public class ZrpcConsumerContext {
    public static ConcurrentHashMap<String, ReferenceConfig> serviceMap = new ConcurrentHashMap<>();

    public static void saveServiceConfig(ReferenceConfig referenceConfig) {
        serviceMap.putIfAbsent(referenceConfig.getService().getName(), referenceConfig);
    }
}
