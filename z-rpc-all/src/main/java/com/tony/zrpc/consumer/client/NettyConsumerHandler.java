package com.tony.zrpc.consumer.client;

import com.tony.zrpc.provider.server.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author herrhu
 * @date 2021/12/17 13:32
 **/
public class NettyConsumerHandler extends SimpleChannelInboundHandler<RpcResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        System.out.println("收到一个响应" + rpcResponse);
        if (rpcResponse.getStatus() == 99) {
            throw new Exception(rpcResponse.getContent().toString());
        }

        RpcConnection rpcConnection = NettyConsumerClient.connectionInfo.get(ctx.channel().remoteAddress());
        rpcConnection.complete(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        NettyConsumerClient.connectionInfo.remove(ctx.channel().remoteAddress());
    }
}
