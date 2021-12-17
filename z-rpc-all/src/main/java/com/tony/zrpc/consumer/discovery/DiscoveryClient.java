package com.tony.zrpc.consumer.discovery;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务发现的客户端
 */
public class DiscoveryClient {
    static AtomicInteger counter = new AtomicInteger(0);

    public static URI chose(String serviceName) {
        ArrayList<URI> serviceUris = new ArrayList<>();
        serviceUris.addAll(ConsumerDiscoveryStart.serverList.get(serviceName));

        // 随机返回一个
        // int index = new Random().nextInt(serviceUris.size());
        // 不完美轮询 -- 但是体现了轮询的原理 -- 请求计数
        int index = counter.incrementAndGet() % serviceUris.size();
        return serviceUris.get(index);
    }
}
