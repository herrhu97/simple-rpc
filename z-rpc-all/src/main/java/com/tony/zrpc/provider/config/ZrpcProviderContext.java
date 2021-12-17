package com.tony.zrpc.provider.config;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 上下文，保留了当前系统里面所有的服务提供者接口实例信息
 * @author herrhu
 * @date 2021/12/17 20:08
 **/
public class ZrpcProviderContext {
    public static ConcurrentHashMap<String,ServiceConfig> serviceMap = new ConcurrentHashMap<>();

    public static void saveServiceConfig(ServiceConfig serviceConfig) {
        serviceMap.putIfAbsent(serviceConfig.getService().getName(), serviceConfig);
    }
}
