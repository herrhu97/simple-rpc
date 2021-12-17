package com.tony.zrpc.consumer.client;

import com.tony.zrpc.provider.server.RpcRequest;
import io.netty.channel.Channel;

/**
 * 对连接对象Channel的封装
 * @author herrhu
 * @date 2021/12/17 12:37
 **/
public class RpcConnection {
    //netty中对于socket对象的封装
    Channel channel;

    public RpcConnection(Channel channel) {
        this.channel = channel;
    }

    public void call(RpcRequest rpcRequest) {
        channel.writeAndFlush(rpcRequest);
    }
}
