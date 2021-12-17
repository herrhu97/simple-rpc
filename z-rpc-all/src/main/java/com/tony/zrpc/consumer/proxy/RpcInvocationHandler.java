package com.tony.zrpc.consumer.proxy;


import com.tony.zrpc.consumer.client.NettyConsumerClient;
import com.tony.zrpc.consumer.client.RpcConnection;
import com.tony.zrpc.provider.server.RpcRequest;

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
        if (method.getName().equals("toString")) {
            return proxy.toString();
        }

        //组装RpcRequest对象
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setArguments(args);
        rpcRequest.setClassName(method.getDeclaringClass().getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        System.out.println("客户端发起的一次动态代理调用" + rpcRequest);

        //网络连接
        RpcConnection connect = NettyConsumerClient.connect("127.0.0.1", 8080);
        //发送数据
        connect.call(rpcRequest);
        return "模拟的值";
    }
}
