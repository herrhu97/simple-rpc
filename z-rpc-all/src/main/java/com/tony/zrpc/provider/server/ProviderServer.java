package com.tony.zrpc.provider.server;

import com.tony.zrpc.common.serialize.json.JsonSerialization;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

// spring事件通知机制 - 观察者模式
// 此处就是 网络开放 的地方
@Component
public class ProviderServer implements SmartApplicationListener, ApplicationContextAware {
    // 指定  哪些事件下会被触发
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == ContextClosedEvent.class || eventType == ContextStartedEvent.class;
    }

    // 当spring加载完毕的时间,启动网络端口
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        try{
            if(applicationEvent instanceof ContextStartedEvent ) {
                System.out.println("spring 启动啦, start server");
                //
                ServerSocket socketServer = new ServerSocket(8080);
                while(true) { //
                    Socket connection = socketServer.accept();// block
                    byte[] request = new byte[1024];
                    connection.getInputStream().read(request);
                    System.out.println(new String(request));
                    RpcRequest rpcRequest = (RpcRequest) new JsonSerialization().deserialize(request, RpcRequest.class);
                    // call interface
                    String className = rpcRequest.getClassName();
                    String methodName = rpcRequest.getMethodName();
                    Class[] parameterTypes = rpcRequest.getParameterTypes();
                    Object[] arguments = rpcRequest.getArguments();

                    Class<?> serviceClass = Class.forName(className);// jdbc
                    // get bean form spring context
                    Object serviceBean = applicationContext.getBean(serviceClass);
                    Method method = serviceBean.getClass().getMethod(methodName, parameterTypes);
                    Object result = method.invoke(serviceBean, arguments);

                    RpcResponse response = new RpcResponse();
                    response.setStatus(200);
                    response.setContent(result);

                    // response
                    byte[] serialize = new JsonSerialization().serialize(response);
                    connection.getOutputStream().write(serialize);

                    connection.close();
                }
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
