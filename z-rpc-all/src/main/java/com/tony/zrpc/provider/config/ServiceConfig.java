package com.tony.zrpc.provider.config;

/**
 * @author herrhu
 * @date 2021/12/17 20:07
 **/
public class ServiceConfig {
    // 接口
    private Class<?> service;
    // 实例
    private Object reference;

    public Class<?> getService() {
        return service;
    }

    public void setService(Class<?> service) {
        this.service = service;
    }

    public Object getReference() {
        return reference;
    }

    public void setReference(Object reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "ServiceConfig{" +
                "service=" + service +
                ", reference=" + reference +
                '}';
    }
}
