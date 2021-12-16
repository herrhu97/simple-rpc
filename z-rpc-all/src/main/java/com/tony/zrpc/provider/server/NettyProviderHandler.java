package com.tony.zrpc.provider.server;

import com.tony.zrpc.common.serialize.json.JsonSerialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

// 处理器
public class NettyProviderHandler extends SimpleChannelInboundHandler {

    ApplicationContext applicationContext;

    public NettyProviderHandler(ApplicationContext applicationContext) {
        super();
        this.applicationContext = applicationContext;
    }

    // 方法 自动被触发执行
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        ByteBuf request = (ByteBuf) msg;
        // netty 数据读出来 默认 都是 bytebuf。 转成 字节数组
        byte[] msgs = new byte[request.readableBytes()];
        request.readBytes(msgs);

        RpcRequest rpcRequest = (RpcRequest) new JsonSerialization().deserialize(msgs,RpcRequest.class);
        // 根据请求的参数，定义调用的目标
        String className = rpcRequest.getClassName();
        String methodName = rpcRequest.getMethodName();
        Class[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] arguments = rpcRequest.getArguments();

        Class<?> serviceClass = Class.forName(className);// jdbc
        // 从spring的容器中获取一个bean实例
        Object serviceBean = applicationContext.getBean(serviceClass);
        Method method = serviceBean.getClass().getMethod(methodName, parameterTypes);
        Object result = method.invoke(serviceBean, arguments);
        // 执行结果 包装成 对下
        RpcResponse response = new RpcResponse();
        response.setStatus(200);
        response.setContent(result);

        // response - 序列化为 json字符串 -- 字节数组
        byte[] serialize = new JsonSerialization().serialize(response);
        // 包装为 netty 识别的 bytebuf
        channelHandlerContext.writeAndFlush(Unpooled.wrappedBuffer(serialize));

        System.out.println(msg);
    }
}
