package com.tony.edu.rpc;

import com.tony.zrpc.provider.annotation.EnableZrpcProvider;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.tony.edu.rpc") // spring注解扫描 basepackage
@PropertySource("classpath:/rpc.properties") // spring
@EnableZrpcProvider // spring scan
public class SmsApplication {
    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SmsApplication.class);
        context.start();
        // 启动完毕，开启网络
        // 阻塞不退出
        System.in.read();
        context.close();
    }
}
