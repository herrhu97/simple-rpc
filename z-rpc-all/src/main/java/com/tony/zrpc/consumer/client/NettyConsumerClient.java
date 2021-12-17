package com.tony.zrpc.consumer.client;

import com.tony.zrpc.provider.server.NettyCodec;
import com.tony.zrpc.provider.server.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费者和服务提供者建立长连接
 * @author herrhu
 * @date 2021/12/17 12:36
 **/
public class NettyConsumerClient {

    //记录连接的信息 -- 同一个服务提供者，同一个消费者服务器 之间
    public static ConcurrentHashMap<InetSocketAddress, RpcConnection> connectionInfo = new ConcurrentHashMap<>();

    public static RpcConnection connect(String host, int port) throws InterruptedException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress(host, port);
        //如果已经存在直接返回
        if (null != connectionInfo.get(inetSocketAddress)) {
            return connectionInfo.get(inetSocketAddress);
        }

        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);

        // todo bootstrap.handler()
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline().addLast(new NettyCodec(RpcResponse.class));
                socketChannel.pipeline().addLast(new NettyConsumerHandler());
            }
        });

        Channel channel = bootstrap.connect(inetSocketAddress).await().channel();
        RpcConnection rpcConnection = new RpcConnection(channel);
        if (connectionInfo.putIfAbsent(inetSocketAddress, rpcConnection) != null) {
            channel.closeFuture();
            return connectionInfo.get(inetSocketAddress);
        }
        return rpcConnection;
    }
}
