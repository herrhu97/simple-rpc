package com.tony.zrpc.consumer.proxy;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理处理类
 * @author herrhu
 * @date 2021/12/17 11:38
 **/
public class RpcInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method.getName() + " 我被调用了");
        return "模拟的值";
    }
}
