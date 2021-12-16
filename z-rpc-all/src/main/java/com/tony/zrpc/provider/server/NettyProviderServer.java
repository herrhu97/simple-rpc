package com.tony.zrpc.provider.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

// spring事件通知机制 - 观察者模式
// 此处就是 网络开放 的地方
@Component
public class NettyProviderServer implements SmartApplicationListener, ApplicationContextAware {
    // 指定  哪些事件下会被触发
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == ContextClosedEvent.class || eventType == ContextStartedEvent.class;
    }

    EventLoopGroup boss = new NioEventLoopGroup();
    EventLoopGroup worker = new NioEventLoopGroup();

    // 当spring加载完毕的时间,启动网络端口 - 基于netty实现
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        try{
            if(applicationEvent instanceof ContextStartedEvent ) {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                // 1. 类似 tomcat 的配置
                serverBootstrap
                        .group(boss, worker)
                        // 指定 所使用的  NIO
                        .channel(NioServerSocketChannel.class)
                        // 监听 端口
                        .localAddress(new InetSocketAddress("127.0.0.1", 8080));
                // 处理器 handler -- 职责链 --- filter
                // 2. 添加handler - 有了链接之后 处理逻辑
                serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 定义具体的handler处理顺序
                        socketChannel.pipeline().addLast(new NettyCodec(RpcRequest.class));
                        socketChannel.pipeline().addLast(new NettyProviderHandler(applicationContext));
                    }
                });
                // 3. 绑定端口
                serverBootstrap.bind().sync();
                System.out.println("完成端口绑定和服务器启动");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getOrder() {
        return 9999;
    }

    ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
