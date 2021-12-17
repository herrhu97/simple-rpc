package com.tony.zrpc.consumer.proxy;

import java.lang.reflect.Proxy;

/**
 * 工厂方法生成代理类
 * @author herrhu
 * @date 2021/12/17 11:37
 **/
public class ProxyFactory {
    public static Object getProxy(Class<?>[] interfaces) {
        return Proxy.newProxyInstance(ProxyFactory.class.getClassLoader(), interfaces, new RpcInvocationHandler());
    }
}
