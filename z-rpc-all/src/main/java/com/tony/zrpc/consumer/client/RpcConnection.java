package com.tony.zrpc.consumer.client;

import com.tony.zrpc.provider.server.RpcRequest;
import com.tony.zrpc.provider.server.RpcResponse;
import io.netty.channel.Channel;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对连接对象Channel的封装
 * @author herrhu
 * @date 2021/12/17 12:37
 **/
public class RpcConnection {
    final static ConcurrentHashMap<String, CompletableFuture> invokeMap = new ConcurrentHashMap<>();
    //netty中对于socket对象的封装
    Channel channel;

    public RpcConnection(Channel channel) {
        this.channel = channel;
    }

    //由应用程序的线程调用
    public Object call(RpcRequest rpcRequest) throws Exception {
        CompletableFuture future = new CompletableFuture();
        invokeMap.putIfAbsent(rpcRequest.getId(), future);
        channel.writeAndFlush(rpcRequest);
        return future.get();
    }

    //由netty的线程调用
    public void complete(RpcResponse response) {
        invokeMap.get(response.getRequestId()).complete(response.getContent());
        invokeMap.remove(response.getRequestId());
    }
}
