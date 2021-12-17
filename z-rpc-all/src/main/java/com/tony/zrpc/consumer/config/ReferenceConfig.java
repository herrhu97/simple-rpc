package com.tony.zrpc.consumer.config;

/**
 * @author herrhu
 * @date 2021/12/17 20:41
 **/
public class ReferenceConfig {
    private Class<?> service;

    public Class<?> getService() {
        return service;
    }

    public void setService(Class<?> service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "ReferenceConfig{" +
                "service=" + service +
                '}';
    }
}
