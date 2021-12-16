package com.tony.zrpc.provider.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

/**
 * netty对于发来的RpcRequest处理
 */
// 处理器
public class NettyProviderHandler extends SimpleChannelInboundHandler<RpcRequest> {

    ApplicationContext applicationContext;

    public NettyProviderHandler(ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    // 方法 自动被触发执行
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {

        // 根据请求的参数，定义调用的目标
        String className = rpcRequest.getClassName();
        String methodName = rpcRequest.getMethodName();
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] arguments = rpcRequest.getArguments();

        Class<?> serviceClass = Class.forName(className);
        // 从spring的容器中获取一个bean实例
        Object serviceBean = applicationContext.getBean(serviceClass);
        Method method = serviceBean.getClass().getMethod(methodName, parameterTypes);
        Object result = method.invoke(serviceBean, arguments);
        // 执行结果 包装成 对下
        RpcResponse response = new RpcResponse();
        response.setStatus(200);
        response.setContent(result);

        // out
        channelHandlerContext.writeAndFlush(response);

    }
}
